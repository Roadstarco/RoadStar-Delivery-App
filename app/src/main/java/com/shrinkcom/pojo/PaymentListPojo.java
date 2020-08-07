package com.shrinkcom.pojo;

import java.io.Serializable;

/**
 * Created by Prem Kumar and Anitha on 11/2/2015.
 */
public class PaymentListPojo implements Serializable {
    private String paymentName, paymentCode;

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }
}
