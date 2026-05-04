package com.payment.paymentServices.components;

import com.payment.paymentServices.components.feeStrategy.CardFee;
import com.payment.paymentServices.components.feeStrategy.PayPalFee;
import com.payment.paymentServices.exception.InvalidPaymentMethodException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeeStrategyFactoryTest {

    private FeeStrategyFactory feeStrategyFactory;

    @BeforeEach
    void setUp() {
        feeStrategyFactory = new FeeStrategyFactory(List.of(new CardFee(), new PayPalFee()));
    }

    @Test
    void getFee_shouldReturnCardFeeForCardType() {
        FeeStrategy fee = feeStrategyFactory.getFee("CARD");
        assertThat(fee).isInstanceOf(CardFee.class);
    }

    @Test
    void getFee_shouldReturnPayPalFeeForPayPalType() {
        FeeStrategy fee = feeStrategyFactory.getFee("PAYPAL");
        assertThat(fee).isInstanceOf(PayPalFee.class);
    }

    @Test
    void getFee_shouldBeCaseInsensitive() {
        FeeStrategy fee = feeStrategyFactory.getFee("card");
        assertThat(fee).isInstanceOf(CardFee.class);
    }

    @Test
    void getFee_shouldThrowInvalidPaymentMethodExceptionForUnknownType() {
        assertThatThrownBy(() -> feeStrategyFactory.getFee("BITCOIN"))
                .isInstanceOf(InvalidPaymentMethodException.class)
                .hasMessageContaining("BITCOIN");
    }
}
