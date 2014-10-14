package com.digout.validation;

import com.digout.artifact.Address;
import com.digout.artifact.Product;
import com.digout.exception.ValidationException;
import com.digout.support.context.MessageContext;
import com.digout.support.context.MessageContextFactory;
import com.digout.support.i18n.I18nMessageSource;
import com.digout.support.money.CurrencyUnit;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import static com.google.common.base.Strings.isNullOrEmpty;

public class ProductValidator implements ExceptionAwareValidator<Product> {

    @Autowired
    private I18nMessageSource i18n;

    /*
     * @Autowired private AddressValidator addressValidator;
     */

    private MessageContext preValidate(final Product product) {
        MessageContext messageContext = MessageContextFactory.newContext();

        if (product == null) {
            messageContext.addMessage(this.i18n.getMessage("product.data.required"));
            return messageContext;
        }

        String productName = product.getName();
        if (isNullOrEmpty(productName)) {
            messageContext.addMessage(this.i18n.getMessage("product.field.empty"));
        } else if (productName.length() < 3) {
            messageContext.addMessage(this.i18n.getMessage("product.name.min.3.chars"));
        }

        String price = product.getPrice();
        boolean pricePresent = !isNullOrEmpty(price);

        if (pricePresent) {
            if (!NumberUtils.isNumber(price)) {
                messageContext.addMessage(this.i18n.getMessage("product.price.format.invalid"));
            } else if (price.length() > 6) {
                messageContext.addMessage(this.i18n.getMessage("product.price.field.more.6.digits"));
            }
            String currency = product.getCurrency();
            boolean currencyPresent = !isNullOrEmpty(currency);
            if (!currencyPresent) {
                messageContext.addMessage(this.i18n.getMessage("product.currency.required"));
            } else if (!CurrencyUnit.isValid(currency)) {
                messageContext.addMessage(this.i18n.getMessage("currency.invalid.or.unsupported"));
            }
        } else {
            messageContext.addMessage(this.i18n.getMessage("product.price.field.empty"));
        }

        if (product.isSetInformation() ? product.getInformation().length() > 180 : false) {
            messageContext.addMessage(this.i18n.getMessage("product.description.more.180.symbols"));
        }

        if (!product.isSetSellType()) {
            messageContext.addMessage(this.i18n.getMessage("product.without.sell.type"));
        }

        Address address = product.getAddress();
        if (address != null) {
            String city = address.getCity().trim();
            if (city == null || city.isEmpty()) {
                messageContext.addMessage(this.i18n.getMessage("city.required"));
            }
        } else {
            messageContext.addMessage(this.i18n.getMessage("city.required"));
        }

        /*
         * if(product.isSetAddress()){ try { addressValidator.validateAndRaise(product.getAddress()); } catch
         * (ValidationException e) { messageContext.addMessages(e.getMessageContext().getMessages()); } }
         */

        return messageContext;
    }

    @Override
    public void validateAndRaise(final Product product) throws ValidationException {
        MessageContext messageContext = preValidate(product);
        if (!messageContext.isEmpty()) {
            throw new ValidationException(messageContext);
        }
    }

}
