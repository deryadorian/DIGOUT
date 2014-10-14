package com.digout.service.payment;

import com.digout.exception.ApplicationException;

public interface PaymentService {

    PaymentResult performPayment(PaymentData paymentRequest) throws ApplicationException;
}
