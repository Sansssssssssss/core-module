package com.losaxa.core.pattern.adapter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AdapterTest {

    //https://refactoringguru.cn/design-patterns/adapter
    @Test
    public void test() {
        RoundHole roundHole = new RoundHole();
        RoundPeg  roundPeg  = new RoundPeg(5);
        System.out.println(roundHole.fits(roundPeg));

        SquarePeg squarePeg = new SquarePeg(5);
        System.out.println(roundHole.fits(squarePeg));

        squarePeg = new SquarePeg(10);
        System.out.println(roundHole.fits(squarePeg));
    }
}

class Container {
    static List<Adapter<Object,Double>> list = new ArrayList<>();

    static {
        list.add(new RoundPegAdapter());
        list.add(new SquarePegAdapter());
    }

    public static Adapter<Object,Double> get(Object handle) {
        for (Adapter<Object,Double> adapter : list) {
            if (adapter.supports(handle)) {
                return adapter;
            }
        }
        throw new IllegalArgumentException("没有合适的适配器");
    }
}

class RoundHole {

    boolean fits(Object roundPeg) {
        Adapter<Object,Double> adapter = Container.get(roundPeg);
        Double                  rs      = adapter.adapt(roundPeg);
        return 5 >= rs;
    }
}

class RoundPeg {
    private double radius;

    RoundPeg(double radius) {
        this.radius = radius;
    }

    //返回钉子的半径
    Double getRadius() {
        return radius;
    }

}

class RoundPegAdapter implements Adapter<Object,Double> {

    @Override
    public boolean supports(Object param) {
        return param instanceof RoundPeg;
    }

    @Override
    public Double adapt(Object param) {
        return ((RoundPeg) param).getRadius();
    }

}

class SquarePeg {
    private double width;

    SquarePeg(double width) {
        this.width = width;
    }

    Double getWidth() {
        return width;
    }
}

class SquarePegAdapter implements Adapter<Object,Double> {

    @Override
    public boolean supports(Object param) {
        return param instanceof SquarePeg;
    }

    @Override
    public Double adapt(Object param) {
        return ((SquarePeg) param).getWidth() * Math.sqrt(2) / 2;
    }
}