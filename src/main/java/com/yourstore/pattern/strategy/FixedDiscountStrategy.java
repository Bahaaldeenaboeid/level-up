package com.yourstore.pattern.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class FixedDiscountStrategy implements DiscountStrategy {

    @Override
    public BigDecimal apply(BigDecimal price, BigDecimal discountValue) {
        if (price == null || discountValue == null) {
            throw new IllegalArgumentException("Price and discount value must not be null");
        }
        BigDecimal result = price.subtract(discountValue);
        return result.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }
}
