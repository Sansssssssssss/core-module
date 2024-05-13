package com.losaxa.core.pattern.factory;

import org.junit.Test;

import java.math.BigDecimal;

public class FactoryTest {

    @Test
    public void test() {
        AbstractFactory<BigDecimal,BigDecimal> factory;
        //这里的type变量实际会从配置或其他地方获取
        int type = 1;

        //可以考虑将工厂实例存放至容器(例如Map),可以省略分支判断
        if (type == 1) {
            factory = new DiscountCampaignFactory();
        } else if (type == 2) {
            factory = new FullReductionCampaignFactory();
        }else {
            throw new IllegalArgumentException(String.format("活动不存在:%s", type));
        }
        BigDecimal rs = factory.use(new BigDecimal("100"));
        System.out.println(rs);
    }


}

class DiscountCampaignFactory extends AbstractFactory<BigDecimal,BigDecimal> {

    @Override
    public Product<BigDecimal, BigDecimal> getObject() {
        return new DiscountCampaign(new BigDecimal("0.9"));
    }

}

class FullReductionCampaignFactory extends AbstractFactory<BigDecimal,BigDecimal> {

    @Override
    public Product<BigDecimal, BigDecimal> getObject() {
        return new FullReductionCampaign(new BigDecimal("90"), new BigDecimal("5"));
    }

}


class DiscountCampaign implements Product<BigDecimal,BigDecimal> {

    private final BigDecimal discount;

    DiscountCampaign(BigDecimal discount) {
        this.discount = discount;
    }

    @Override
    public BigDecimal use(BigDecimal price) {
        return price.multiply(discount);
    }
}

class FullReductionCampaign implements Product<BigDecimal,BigDecimal> {

    private final BigDecimal full;
    private final BigDecimal reduction;

    public FullReductionCampaign(BigDecimal full,
                                 BigDecimal reduction) {
        this.full = full;
        this.reduction = reduction;
    }

    @Override
    public BigDecimal use(BigDecimal price) {
        if (price.compareTo(full) >= 0) {
            return price.subtract(reduction);
        }
        return price;
    }

}