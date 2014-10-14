package com.digout.service.payment;

import java.io.StringReader;
import java.io.StringWriter;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import tr.com.garanti.Card;
import tr.com.garanti.Customer;
import tr.com.garanti.GVPSRequest;
import tr.com.garanti.GVPSResponse;
import tr.com.garanti.ObjectFactory;
import tr.com.garanti.Order;
import tr.com.garanti.RequestTransaction;
import tr.com.garanti.ResponseTransaction;
import tr.com.garanti.Terminal;

import com.digout.exception.ApplicationException;
import com.digout.model.entity.common.BankInfoEntity;
import com.digout.repository.BankInfoRepository;
import com.digout.support.i18n.I18nMessageSource;
import com.digout.utils.Asserts;
import com.digout.utils.BankUtils;
import com.digout.utils.HttpsUtils;
import com.digout.utils.SecureUtils;
import com.digout.utils.StringsHelper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;

public class GarantiVirtualPosService implements PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GarantiVirtualPosService.class);
    private static final JAXBContext JAXB_CONTEXT;
    
    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Autowired
    private BankInfoRepository bankInfoRepository;
    @Autowired
    private I18nMessageSource i18n;

    private GVPSResponse fromXml(final String xmlResponse) {
        Object result = null;
        try {
            final Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
            result = unmarshaller.unmarshal(new StringReader(xmlResponse));
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return (GVPSResponse) result;
    }

    @Override
    public PaymentResult performPayment(final PaymentData paymentRequest) throws ApplicationException {
        final BankInfoEntity bankInfoEntity = this.bankInfoRepository.findOne(1L);
        Asserts.notNull(bankInfoEntity, this.i18n.getMessage("no.bank.info.in.db"));

        final String transactionReferenceId = SecureUtils.generateSecureId();
        final GVPSRequest gvpsRequest = prepareRequest(paymentRequest, bankInfoEntity, transactionReferenceId);
        final String xmlRequest = toXml(gvpsRequest);

        final ClientConfig clientConfig = HttpsUtils.getSSLClientConfig();
        final WebResource resource = Client.create(clientConfig).resource(bankInfoEntity.getUri());
        final ClientResponse response = resource.type(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class,
                xmlRequest);

        final String xmlResponse = response.getEntity(String.class);
        final GVPSResponse gvpsResponse = fromXml(xmlResponse);

        final PaymentResult paymentResult = new PaymentResult();
        final ResponseTransaction responseTransaction = gvpsResponse.getTransaction();
        final ResponseTransaction.Response transactionResponse = responseTransaction.getResponse();

        paymentResult.setResponseCode(transactionResponse.getCode());
        paymentResult.setTransactionReferenceId(transactionReferenceId);
        paymentResult.setSysErrorMessage(transactionResponse.getSysErrMsg());
        paymentResult.setErrorMessage(transactionResponse.getErrorMsg());

        return paymentResult;
    }

    private GVPSRequest prepareRequest(final PaymentData paymentRequest, final BankInfoEntity bankInfoEntity,
            final String transactionReferenceId) {
        final String password = bankInfoEntity.getPassword();
        final String terminalId = bankInfoEntity.getTerminalId();
        final String cardNumber = paymentRequest.getCardNumber();
        final String amount = StringsHelper.convertDoubleToBankAmountType(paymentRequest.getOrderPrice());
        final String transactionType = bankInfoEntity.getTransactionType();
        final String installCnt = bankInfoEntity.getInstallmentCnt();
        final String cardHolderCode = bankInfoEntity.getCardHolderPresentCode();
        final String motoInd = bankInfoEntity.getMotoInd();
        final String originalRetrefNum = bankInfoEntity.getOriginalRetrefNum();

        final String hashData = BankUtils.generateHashData(password, terminalId, transactionReferenceId, cardNumber,
                amount);

        final GVPSRequest vpos = new GVPSRequest();
        vpos.setMode(bankInfoEntity.getMode());
        vpos.setVersion(bankInfoEntity.getVersion());

        final Terminal terminal = new Terminal();
        terminal.setProvUserID(bankInfoEntity.getProvUserId());
        terminal.setHashData(hashData);
        terminal.setUserID(bankInfoEntity.getUserId());
        terminal.setID(bankInfoEntity.getTerminalId());
        terminal.setMerchantId(bankInfoEntity.getMerchantId());
        vpos.setTerminal(terminal);

        final Customer customer = new Customer();
        customer.setIPAddress(paymentRequest.getUserIpAddress());
        customer.setEmailAddress(paymentRequest.getUserEmail());
        vpos.setCustomer(customer);

        final Card card = new Card();
        card.setNumber(paymentRequest.getCardNumber());
        card.setExpireDate(paymentRequest.getCardExpireDate().toString("MMYY"));
        card.setCVV2(paymentRequest.getCardSecurityNumber());
        vpos.setCard(card);

        final Order order = new Order();
        order.setOrderID(transactionReferenceId);
        order.setGroupId("");
        vpos.setOrder(order);

        final RequestTransaction transaction = new RequestTransaction();
        transaction.setType(transactionType);
        transaction.setInstallmentCnt(installCnt);
        transaction.setAmount(amount);
        transaction.setCurrencyCode(paymentRequest.getCurrencyUnit().getCode());
        transaction.setCardholderPresentCode(cardHolderCode);
        transaction.setMotoInd(motoInd);
        transaction.setOriginalRetrefNum(originalRetrefNum);
        vpos.setTransaction(transaction);

        return vpos;
    }

    private String toXml(final GVPSRequest gvpsRequest) {
        final StringWriter xmlPaymentDataWriter = new StringWriter();
        try {
            final Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
            marshaller.marshal(gvpsRequest, xmlPaymentDataWriter);
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return xmlPaymentDataWriter.toString();
    }
}
