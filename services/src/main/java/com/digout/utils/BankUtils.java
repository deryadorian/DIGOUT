package com.digout.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class BankUtils {

    /*
     * private static final String mode = "TEST"; private static final String userId = "GARANTI"; private static final
     * String password = "123qweASD";
     * 
     * 
     * private static final String provUserId = "PROVAUNT"; private static final String merchantId = "7000679"; private
     * static final String terminalId = "30691297";
     */

    public static String generateHash(final String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.reset();
            byte[] buffer = input.getBytes();
            md.update(buffer);
            byte[] digest = md.digest();
            String hexStr = "";
            for (int i = 0; i < digest.length; i++) {
                hexStr += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
            }
            return hexStr;
        } catch (NoSuchAlgorithmException e) {
            System.out.println("error");
            return null;
        }
    }

    public static String generateHashData(final String password, final String terminalId, final String orderId,
            final String cardnumber, final String amount) {
        int lengthTerminalId = terminalId.length();
        String preparedTerminalId = lengthTerminalId < 9 ? StringsHelper.addPrefix(terminalId, "0",
                Math.abs(lengthTerminalId - 9)) : terminalId;
        String hashedPassword = generateHash(password + preparedTerminalId).toUpperCase();
        return generateHash(orderId + terminalId + cardnumber + amount + hashedPassword).toUpperCase();
    }

    public static String generateXML(final String mode, final String version, final String provUserId,
            final String hash, final String userID, final String merchantId, final String terminalId,
            final String customerIpAddress, final String customerEmail, final String paymentToolNumber,
            final String expDate, final String cvc, final String orderID, final String groupId,
            final String transactionType, final String installmentCnt, final String amount, final String currency,
            final String cardholderPresentCode, final String motoInd, final String originalRetrefNum) {
        try {
            // Create instance of DocumentBuilderFactory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // Get the DocumentBuilder
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            // Create blank DOM Document
            Document doc = docBuilder.newDocument();

            Element root = doc.createElement("GVPSRequest");
            doc.appendChild(root);

            Element modeElement = doc.createElement("Mode");
            modeElement.appendChild(doc.createTextNode(mode));
            root.appendChild(modeElement);

            Element versionElement = doc.createElement("Version");
            versionElement.appendChild(doc.createTextNode(version));
            root.appendChild(versionElement);

            Element terminalElement = doc.createElement("Terminal");
            root.appendChild(terminalElement);

            Element provUserIdElement = doc.createElement("ProvUserID");
            // ProvUserID.appendChild(doc.createTextNode(userName));
            provUserIdElement.appendChild(doc.createTextNode(provUserId));
            terminalElement.appendChild(provUserIdElement);

            Element hashDataElement = doc.createElement("HashData");
            hashDataElement.appendChild(doc.createTextNode(hash));
            terminalElement.appendChild(hashDataElement);

            Element userIdElement = doc.createElement("UserID");
            userIdElement.appendChild(doc.createTextNode(userID));
            terminalElement.appendChild(userIdElement);

            Element idElement = doc.createElement("ID");
            idElement.appendChild(doc.createTextNode(terminalId));
            terminalElement.appendChild(idElement);

            Element merchantIdElement = doc.createElement("MerchantID");
            merchantIdElement.appendChild(doc.createTextNode(merchantId));
            terminalElement.appendChild(merchantIdElement);

            Element customerElement = doc.createElement("Customer");
            root.appendChild(customerElement);

            Element ipAddressElement = doc.createElement("IPAddress");
            ipAddressElement.appendChild(doc.createTextNode(customerIpAddress));
            customerElement.appendChild(ipAddressElement);

            Element emailAddressElement = doc.createElement("EmailAddress");
            emailAddressElement.appendChild(doc.createTextNode(customerEmail));
            customerElement.appendChild(emailAddressElement);

            Element cardElement = doc.createElement("Card");
            root.appendChild(cardElement);

            Element numberElement = doc.createElement("Number");
            numberElement.appendChild(doc.createTextNode(paymentToolNumber));
            cardElement.appendChild(numberElement);

            Element expireDateElement = doc.createElement("ExpireDate");
            expireDateElement.appendChild(doc.createTextNode(expDate));
            cardElement.appendChild(expireDateElement);

            Element CVV2Element = doc.createElement("CVV2");
            CVV2Element.appendChild(doc.createTextNode(cvc));
            cardElement.appendChild(CVV2Element);

            Element orderElement = doc.createElement("Order");
            root.appendChild(orderElement);

            Element orderIdElement = doc.createElement("OrderID");
            orderIdElement.appendChild(doc.createTextNode(orderID));
            orderElement.appendChild(orderIdElement);

            Element groupIdElement = doc.createElement("GroupID");
            groupIdElement.appendChild(doc.createTextNode(""));
            orderElement.appendChild(groupIdElement);

            /*
             * Element Description=doc.createElement("Description"); Description.appendChild(doc.createTextNode(""));
             * Order.appendChild(Description);
             */

            Element transactionElement = doc.createElement("Transaction");
            root.appendChild(transactionElement);

            Element typeElement = doc.createElement("Type");
            typeElement.appendChild(doc.createTextNode(transactionType));
            transactionElement.appendChild(typeElement);

            Element InstallmentCnt = doc.createElement("InstallmentCnt");
            InstallmentCnt.appendChild(doc.createTextNode(installmentCnt));
            transactionElement.appendChild(InstallmentCnt);

            Element amountElement = doc.createElement("Amount");
            amountElement.appendChild(doc.createTextNode(amount));
            transactionElement.appendChild(amountElement);

            Element currencyCodeElement = doc.createElement("CurrencyCode");
            currencyCodeElement.appendChild(doc.createTextNode(currency));
            transactionElement.appendChild(currencyCodeElement);

            Element cardholderPresentCodeElement = doc.createElement("CardholderPresentCode");
            cardholderPresentCodeElement.appendChild(doc.createTextNode(cardholderPresentCode));
            transactionElement.appendChild(cardholderPresentCodeElement);

            Element MotoInd = doc.createElement("MotoInd");
            MotoInd.appendChild(doc.createTextNode(motoInd));
            transactionElement.appendChild(MotoInd);

            /*
             * Element _Description=doc.createElement("Description"); _Description.appendChild(doc.createTextNode(""));
             * Transaction.appendChild(_Description);
             */

            Element OriginalRetrefNum = doc.createElement("OriginalRetrefNum");
            OriginalRetrefNum.appendChild(doc.createTextNode(originalRetrefNum));
            transactionElement.appendChild(OriginalRetrefNum);
            // Convert dom to String
            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer aTransformer = tranFactory.newTransformer();
            StringWriter buffer = new StringWriter();
            aTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            aTransformer.transform(new DOMSource(doc), new StreamResult(buffer));
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private BankUtils() {
    }

}
