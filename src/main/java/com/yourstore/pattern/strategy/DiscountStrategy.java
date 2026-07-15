package com.yourstore.pattern.strategy;

import java.math.BigDecimal;

public interface DiscountStrategy {

    BigDecimal apply(BigDecimal price, BigDecimal discountValue);
}
