package com.digout.model.entity.product;

import javax.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PRODUCT_TAG")
public class ProductTagEntity implements Serializable {
    private static final long serialVersionUID = 5020467555817243750L;

    @Id
    @Column(name = "tag", nullable = false)
    private String tag;

    @ManyToMany(targetEntity = ProductEntity.class, cascade = CascadeType.ALL, mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<ProductEntity> products = new HashSet<ProductEntity>();

    public ProductTagEntity() {
    }

    public ProductTagEntity(final String tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProductTagEntity that = (ProductTagEntity) o;
        return this.tag != null && this.tag.equals(that.tag);
    }

    public Set<ProductEntity> getProducts() {
        return this.products;
    }

    public String getTag() {
        return this.tag;
    }

    @Override
    public int hashCode() {
        return this.tag != null ? 37 * this.tag.hashCode() : 0;
    }

    public void setProducts(final Set<ProductEntity> products) {
        this.products = products;
    }

    public void setTag(final String tag) {
        this.tag = tag;
    }
}
