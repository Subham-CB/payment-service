package com.payment.paymentServices.components.feeStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class PayPalFeeTest {

    private PayPalFee payPalFee;

    @BeforeEach
    void setUp() {
        payPalFee = new PayPalFee();
    }

    @Test
    void calculateFee_shouldReturnTenPercentOfAmount() {
        assertThat(payPalFee.calculateFee(200.0)).isCloseTo(20.0, within(0.001));
    }

    @Test
    void calculateFee_shouldReturnZeroForZeroAmount() {
        assertThat(payPalFee.calculateFee(0.0)).isCloseTo(0.0, within(0.001));
    }

    @Test
    void calculateFee_shouldHandleLargeAmount() {
        assertThat(payPalFee.calculateFee(5_000.0)).isCloseTo(500.0, within(0.001));
    }

    @Test
    void getType_shouldReturnPAYPAL() {
        assertThat(payPalFee.getType()).isEqualTo("PAYPAL");
    }
}
