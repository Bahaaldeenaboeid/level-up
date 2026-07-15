package com.yourstore.pattern.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class PercentageDiscountStrategy implements DiscountStrategy {

    @Override
    public BigDecimal apply(BigDecimal price, BigDecimal discountValue) {
        if (price == null || discountValue == null) {
            throw new IllegalArgumentException("Price and discount value must not be null");
        }
        BigDecimal discountRate = discountValue.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        BigDecimal multiplier = BigDecimal.ONE.subtract(discountRate);
        BigDecimal result = price.multiply(multiplier);
        return result.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }
}
