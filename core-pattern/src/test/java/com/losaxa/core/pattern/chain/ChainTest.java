package com.losaxa.core.pattern.chain;

import org.junit.Test;

import java.math.BigDecimal;

public class ChainTest {



    @Test
    public void test() {
        PriceContext  priceContext  = new PriceContext();
        priceContext.price=new BigDecimal("100");
        DiscountChain discountChain = new DiscountChain();
        discountChain.setNext(new FullReductionChain());
        PriceContext rs = discountChain.handle(priceContext);
        System.out.println(rs.price);
    }

}

class PriceContext extends ChainContext {

    public BigDecimal price;

}

class DiscountChain extends AbstractChain<PriceContext>{

    @Override
    PriceContext preNext(PriceContext chainContext) {
        if (chainContext.price.compareTo(new BigDecimal("100")) >= 0) {
            chainContext.price = chainContext.price.multiply(new BigDecimal("0.9"));
        }
        return chainContext;
    }

}

class FullReductionChain extends AbstractChain<PriceContext> {

    @Override
    PriceContext preNext(PriceContext chainContext) {
        if (chainContext.price.compareTo(new BigDecimal("90")) >= 0) {
            chainContext.price = chainContext.price.subtract(new BigDecimal("5"));
        }
        return chainContext;
    }
}