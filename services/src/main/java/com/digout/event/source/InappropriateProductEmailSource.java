package com.digout.event.source;

import com.digout.model.entity.product.ProductEntity;
import com.digout.model.entity.user.UserEntity;

public final class InappropriateProductEmailSource {

    private long productId;
    private long reporterId;
    private long productOwnerId;

    private InappropriateProductEmailSource(long productId, long reporterId, long productOwnerId) {
        this.productId = productId;
        this.reporterId = reporterId;
        this.productOwnerId = productOwnerId;
    }
    
    public static Builder builder() {
        return new Builder();
    }

    public long getProductId() {
        return productId;
    }

    public long getReporterId() {
        return reporterId;
    }

    public long getProductOwnerId() {
        return productOwnerId;
    }

    public static final class Builder {
        private long productId;
        private long reporterId;
        private long productOwnerId;

        public Builder forProduct(final ProductEntity productEntity) {
            this.productId = productEntity.getId();
            return this;
        }

        public Builder byReporter(final UserEntity userEntity) {
            this.reporterId = userEntity.getId();
            return this;
        }

        public Builder withProductOwner(final UserEntity userEntity) {
            this.productOwnerId = userEntity.getId();
            return this;
        }

        public InappropriateProductEmailSource build() {
            return new InappropriateProductEmailSource(productId, reporterId, productOwnerId);
        }
    }
}
