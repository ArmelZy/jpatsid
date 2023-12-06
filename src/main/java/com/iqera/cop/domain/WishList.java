package com.iqera.cop.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A WishList.
 */
@Entity
@Table(name = "wish_list")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WishList implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "number_of_element")
    private Integer numberOfElement;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "user", "carts" }, allowSetters = true)
    private CustomerDetails customerDetails;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "wishLists")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "wishLists", "productCategory" }, allowSetters = true)
    private Set<Product> products = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WishList id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumberOfElement() {
        return this.numberOfElement;
    }

    public WishList numberOfElement(Integer numberOfElement) {
        this.setNumberOfElement(numberOfElement);
        return this;
    }

    public void setNumberOfElement(Integer numberOfElement) {
        this.numberOfElement = numberOfElement;
    }

    public CustomerDetails getCustomerDetails() {
        return this.customerDetails;
    }

    public void setCustomerDetails(CustomerDetails customerDetails) {
        this.customerDetails = customerDetails;
    }

    public WishList customerDetails(CustomerDetails customerDetails) {
        this.setCustomerDetails(customerDetails);
        return this;
    }

    public Set<Product> getProducts() {
        return this.products;
    }

    public void setProducts(Set<Product> products) {
        if (this.products != null) {
            this.products.forEach(i -> i.removeWishList(this));
        }
        if (products != null) {
            products.forEach(i -> i.addWishList(this));
        }
        this.products = products;
    }

    public WishList products(Set<Product> products) {
        this.setProducts(products);
        return this;
    }

    public WishList addProduct(Product product) {
        this.products.add(product);
        product.getWishLists().add(this);
        return this;
    }

    public WishList removeProduct(Product product) {
        this.products.remove(product);
        product.getWishLists().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WishList)) {
            return false;
        }
        return getId() != null && getId().equals(((WishList) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WishList{" +
            "id=" + getId() +
            ", numberOfElement=" + getNumberOfElement() +
            "}";
    }
}
