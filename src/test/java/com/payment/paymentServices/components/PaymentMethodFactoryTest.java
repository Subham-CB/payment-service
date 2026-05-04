package com.payment.paymentServices.components;

import com.payment.paymentServices.components.paymentStrategy.Card;
import com.payment.paymentServices.components.paymentStrategy.PayPal;
import com.payment.paymentServices.exception.InvalidPaymentMethodException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentMethodFactoryTest {

    private PaymentMethodFactory paymentMethodFactory;

    @BeforeEach
    void setUp() {
        paymentMethodFactory = new PaymentMethodFactory(List.of(new Card(), new PayPal()));
    }

    @Test
    void getPayMethod_shouldReturnCardForCardType() {
        PaymentMethod method = paymentMethodFactory.getPayMethod("CARD");
        assertThat(method).isInstanceOf(Card.class);
    }

    @Test
    void getPayMethod_shouldReturnPayPalForPayPalType() {
        PaymentMethod method = paymentMethodFactory.getPayMethod("PAYPAL");
        assertThat(method).isInstanceOf(PayPal.class);
    }

    @Test
    void getPayMethod_shouldBeCaseInsensitive() {
        PaymentMethod method = paymentMethodFactory.getPayMethod("paypal");
        assertThat(method).isInstanceOf(PayPal.class);
    }

    @Test
    void getPayMethod_shouldThrowInvalidPaymentMethodExceptionForUnknownType() {
        assertThatThrownBy(() -> paymentMethodFactory.getPayMethod("CRYPTO"))
                .isInstanceOf(InvalidPaymentMethodException.class)
                .hasMessageContaining("CRYPTO");
    }
}
