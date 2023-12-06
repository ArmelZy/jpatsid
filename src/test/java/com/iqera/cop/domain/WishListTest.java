package com.iqera.cop.domain;

import static com.iqera.cop.domain.CustomerDetailsTestSamples.*;
import static com.iqera.cop.domain.ProductTestSamples.*;
import static com.iqera.cop.domain.WishListTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.iqera.cop.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class WishListTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WishList.class);
        WishList wishList1 = getWishListSample1();
        WishList wishList2 = new WishList();
        assertThat(wishList1).isNotEqualTo(wishList2);

        wishList2.setId(wishList1.getId());
        assertThat(wishList1).isEqualTo(wishList2);

        wishList2 = getWishListSample2();
        assertThat(wishList1).isNotEqualTo(wishList2);
    }

    @Test
    void customerDetailsTest() throws Exception {
        WishList wishList = getWishListRandomSampleGenerator();
        CustomerDetails customerDetailsBack = getCustomerDetailsRandomSampleGenerator();

        wishList.setCustomerDetails(customerDetailsBack);
        assertThat(wishList.getCustomerDetails()).isEqualTo(customerDetailsBack);

        wishList.customerDetails(null);
        assertThat(wishList.getCustomerDetails()).isNull();
    }

    @Test
    void productTest() throws Exception {
        WishList wishList = getWishListRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        wishList.addProduct(productBack);
        assertThat(wishList.getProducts()).containsOnly(productBack);
        assertThat(productBack.getWishLists()).containsOnly(wishList);

        wishList.removeProduct(productBack);
        assertThat(wishList.getProducts()).doesNotContain(productBack);
        assertThat(productBack.getWishLists()).doesNotContain(wishList);

        wishList.products(new HashSet<>(Set.of(productBack)));
        assertThat(wishList.getProducts()).containsOnly(productBack);
        assertThat(productBack.getWishLists()).containsOnly(wishList);

        wishList.setProducts(new HashSet<>());
        assertThat(wishList.getProducts()).doesNotContain(productBack);
        assertThat(productBack.getWishLists()).doesNotContain(wishList);
    }
}
