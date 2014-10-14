package com.digout.validation;

import com.digout.artifact.Address;
import com.digout.artifact.Addresses;
import com.digout.artifact.UserProfile;
import com.digout.exception.ValidationException;
import com.digout.support.context.MessageContext;
import com.digout.support.context.MessageContextFactory;
import com.digout.support.i18n.I18nMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.digout.utils.Matchers.matches;
import static com.digout.validation.Patterns.FULLNAME_PATTERN;
import static com.digout.validation.Patterns.PHONE_PATTERN;
import static com.digout.validation.Patterns.IBAN_PATTERN;

public class UserProfileValidator implements ExceptionAwareValidator<UserProfile> {

    @Autowired
    private I18nMessageSource i18n;

    @Autowired
    private AddressValidator addressValidator;

    private MessageContext preValidate(final UserProfile userProfile) {
        MessageContext messageContext = MessageContextFactory.newContext();

        if (userProfile.isSetFullname()) {
            String fullname = userProfile.getFullname();
            if (fullname.length() > 25) {
                messageContext.addMessage(this.i18n.getMessage("profile.fullname.max.25.symbols"));
            } else if (!matches(fullname, FULLNAME_PATTERN)) {
                messageContext.addMessage(this.i18n.getMessage("fullname.format.invalid"));
            }
        }

        if (userProfile.isSetPhone()) {
            if (!userProfile.getPhone().isEmpty() && !matches(userProfile.getPhone(), PHONE_PATTERN)) {
                messageContext.addMessage(this.i18n.getMessage("profile.number.invalid.format"));
            }
        }

        if (userProfile.isSetIban()) {
            if (!userProfile.getIban().isEmpty() && !matches(userProfile.getIban(), IBAN_PATTERN)) {
                messageContext.addMessage(this.i18n.getMessage("profile.iban.format.invalid"));
            }
        }

        List<Address> addresses = null;
        Addresses addressesAll = userProfile.getAddresses();
        if (userProfile.isSetAddresses()) {
            addresses = addressesAll.getAddresses();
        }

        if (!CollectionUtils.isEmpty(addresses)) {
            for (Address address : addresses) {
                try {
                    this.addressValidator.validateAndRaise(address);
                } catch (ValidationException ve) {
                    messageContext.addMessages(ve.getMessageContext().getMessages());
                }
            }
        }
        return messageContext;
    }

    @Override
    public void validateAndRaise(final UserProfile userProfile) throws ValidationException {
        MessageContext messageContext = preValidate(userProfile);
        if (!messageContext.isEmpty()) {
            throw new ValidationException(messageContext);
        }
    }
}
