package com.iqera.cop.domain;

import static com.iqera.cop.domain.ProductCategoryTestSamples.*;
import static com.iqera.cop.domain.ProductTestSamples.*;
import static com.iqera.cop.domain.WishListTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.iqera.cop.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Product.class);
        Product product1 = getProductSample1();
        Product product2 = new Product();
        assertThat(product1).isNotEqualTo(product2);

        product2.setId(product1.getId());
        assertThat(product1).isEqualTo(product2);

        product2 = getProductSample2();
        assertThat(product1).isNotEqualTo(product2);
    }

    @Test
    void wishListTest() throws Exception {
        Product product = getProductRandomSampleGenerator();
        WishList wishListBack = getWishListRandomSampleGenerator();

        product.addWishList(wishListBack);
        assertThat(product.getWishLists()).containsOnly(wishListBack);

        product.removeWishList(wishListBack);
        assertThat(product.getWishLists()).doesNotContain(wishListBack);

        product.wishLists(new HashSet<>(Set.of(wishListBack)));
        assertThat(product.getWishLists()).containsOnly(wishListBack);

        product.setWishLists(new HashSet<>());
        assertThat(product.getWishLists()).doesNotContain(wishListBack);
    }

    @Test
    void productCategoryTest() throws Exception {
        Product product = getProductRandomSampleGenerator();
        ProductCategory productCategoryBack = getProductCategoryRandomSampleGenerator();

        product.setProductCategory(productCategoryBack);
        assertThat(product.getProductCategory()).isEqualTo(productCategoryBack);

        product.productCategory(null);
        assertThat(product.getProductCategory()).isNull();
    }
}
