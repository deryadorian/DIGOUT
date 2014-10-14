package com.digout.converter;

import com.digout.artifact.Product;
import com.digout.model.common.SellType;
import com.digout.model.entity.product.ProductEntity;
import com.digout.support.money.CurrencyUnit;

public class ProductUpdateConverter {

    public ProductEntity syncProduct(final ProductEntity entity, final Product product) {
        if (product.isSetName()) {
            entity.setName(product.getName());
        }
        if (product.isSetPrice()) {
            entity.setPrice(Double.parseDouble(product.getPrice()));
        }
        if (product.isSetCurrency()) {
            entity.setCurrency(CurrencyUnit.valueOf(product.getCurrency()));
        }
        if (product.isSetInformation()) {
            entity.setInformation(product.getInformation());
        }
        if (product.isSetShipmentType()) {
            entity.setShipmentType(product.getShipmentType());
        }
        if (product.isSetShipmentId()) {
            entity.setShipmentId(product.getShipmentId());
        }
        if (product.isSetSellType()) {
            entity.setSellType(SellType.valueOf(product.getSellType().value()));
        }
        return entity;
    }

}
