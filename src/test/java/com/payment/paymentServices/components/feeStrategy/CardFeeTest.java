package com.payment.paymentServices.components.feeStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class CardFeeTest {

    private CardFee cardFee;

    @BeforeEach
    void setUp() {
        cardFee = new CardFee();
    }

    @Test
    void calculateFee_shouldReturnFivePercentOfAmount() {
        assertThat(cardFee.calculateFee(100.0)).isCloseTo(5.0, within(0.001));
    }

    @Test
    void calculateFee_shouldReturnZeroForZeroAmount() {
        assertThat(cardFee.calculateFee(0.0)).isCloseTo(0.0, within(0.001));
    }

    @Test
    void calculateFee_shouldHandleLargeAmount() {
        assertThat(cardFee.calculateFee(10_000.0)).isCloseTo(500.0, within(0.001));
    }

    @Test
    void getType_shouldReturnCARD() {
        assertThat(cardFee.getType()).isEqualTo("CARD");
    }
}
