package com.yourstore.pattern.strategy;

import com.yourstore.enums.DiscountType;
import com.yourstore.core.exception.InvalidRequestException;
import java.math.BigDecimal;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DiscountCalculatorFactory {

    private static final Logger logger = LoggerFactory.getLogger(DiscountCalculatorFactory.class);

    private final PercentageDiscountStrategy percentageDiscountStrategy;
    private final FixedDiscountStrategy fixedDiscountStrategy;

    public DiscountCalculatorFactory(PercentageDiscountStrategy percentageDiscountStrategy,
                                     FixedDiscountStrategy fixedDiscountStrategy) {
        this.percentageDiscountStrategy = percentageDiscountStrategy;
        this.fixedDiscountStrategy = fixedDiscountStrategy;
    }

    public BigDecimal calculateFinalPrice(BigDecimal price, DiscountType type, BigDecimal value) {
        if (price == null || type == null || value == null) {
            throw new InvalidRequestException("discount", "Price, type and value must not be null");
        }
        DiscountStrategy strategy = getStrategy(type);
        BigDecimal finalPrice = strategy.apply(price, value);
        logger.info("Calculated final price {} for type {} and value {}", finalPrice, type, value);
        return finalPrice;
    }

    private DiscountStrategy getStrategy(DiscountType type) {
        switch (type) {
            case PERCENTAGE:
                return percentageDiscountStrategy;
            case FIXED:
                return fixedDiscountStrategy;
            default:
                String message = "Unsupported discount type: " + type;
                logger.error(message);
                throw new InvalidRequestException(message);
        }
    }
}
