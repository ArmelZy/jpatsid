package com.iqera.cop.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class WishListTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static WishList getWishListSample1() {
        return new WishList().id(1L).numberOfElement(1);
    }

    public static WishList getWishListSample2() {
        return new WishList().id(2L).numberOfElement(2);
    }

    public static WishList getWishListRandomSampleGenerator() {
        return new WishList().id(longCount.incrementAndGet()).numberOfElement(intCount.incrementAndGet());
    }
}
