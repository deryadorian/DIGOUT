package com.digout.validation;

import com.digout.artifact.Address;
import com.digout.exception.ValidationException;
import com.digout.support.context.MessageContext;
import com.digout.support.context.MessageContextFactory;
import com.digout.support.i18n.I18nMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import static com.google.common.base.Strings.isNullOrEmpty;

import static com.digout.utils.Matchers.matches;

import static com.digout.validation.Patterns.POSTAL_CODE_PATTERN;
import static com.digout.validation.Patterns.REGION_CITY_PATTERN;
import static com.digout.validation.Patterns.ADDRESS_LINE_PATTERN;

public class AddressValidator implements ExceptionAwareValidator<Address> {

    @Autowired
    private I18nMessageSource i18n;

    private MessageContext preValidate(final Address address) {
        MessageContext messageContext = MessageContextFactory.newContext();

        // this condition never used while userProfileValidation
        if (address == null) {
            messageContext.addMessage("Address in null");
            return messageContext;
        }

        String city = address.getCity();
        if (address.isSetCity()) {
            if (city.isEmpty()) {
                messageContext.addMessage(this.i18n.getMessage("address.city.field.empty"));
            } else if (!matches(address.getCity(), REGION_CITY_PATTERN)) {
                messageContext.addMessage(this.i18n.getMessage("city.format.invalid"));
            }
        } else if (!address.isSetId()) {
            messageContext.addMessage(this.i18n.getMessage("city.required"));
        }

        String region = address.getRegion();
        if (address.isSetRegion()) {
            if (!region.isEmpty() && !matches(region, REGION_CITY_PATTERN)) {
                messageContext.addMessage(this.i18n.getMessage("region.format.invalid"));
            }
        }

        String postCode = address.getPostCode();
        if (!isNullOrEmpty(postCode)) {
            if (!matches(postCode, POSTAL_CODE_PATTERN)) {
                messageContext.addMessage(this.i18n.getMessage("postcode.format.invalid"));
            }
        } else {
            messageContext.addMessage(this.i18n.getMessage("address.postal.field.empty"));
        }

        String addressLine = address.getLine();
        if (address.isSetLine()) {
            if (addressLine.isEmpty()) {
                messageContext.addMessage(this.i18n.getMessage("address.required"));
            } else if (!matches(addressLine, ADDRESS_LINE_PATTERN)) {
                messageContext.addMessage(this.i18n.getMessage("address.format.invalid"));
            }
        } else if (!address.isSetId()) {
            messageContext.addMessage(this.i18n.getMessage("address.required"));
        }

        return messageContext;
    }

    @Override
    public void validateAndRaise(final Address address) throws ValidationException {
        MessageContext messageContext = preValidate(address);
        if (!messageContext.isEmpty()) {
            throw new ValidationException(messageContext);
        }
    }

}
