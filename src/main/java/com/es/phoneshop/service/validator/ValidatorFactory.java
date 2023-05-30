package com.es.phoneshop.service.validator;

import lombok.Data;

@Data
public class ValidatorFactory {

    private final PhoneNumberValidatorImpl phoneNumberValidator = new PhoneNumberValidatorImpl();
    private final PaymentMethodValidatorImpl paymentMethodValidator = new PaymentMethodValidatorImpl();
    private final DeliveryDateValidatorImpl deliveryDateValidator = new DeliveryDateValidatorImpl();
    private final EmptyValidatorImpl emptyValidator = new EmptyValidatorImpl();

    private static class Holder {
        private static final ValidatorFactory INSTANCE = new ValidatorFactory();
    }

    public static ValidatorFactory getInstance() {
        return Holder.INSTANCE;
    }

}
