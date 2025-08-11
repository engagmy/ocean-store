package com.agamy.inventory.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.agamy.inventory.domain.Sale} entity. This class is used
 * in {@link com.agamy.inventory.web.rest.SaleResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /sales?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SaleCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter productName;

    private IntegerFilter quantity;

    private BigDecimalFilter unitPrice;

    private BigDecimalFilter discount;

    private BigDecimalFilter lineTotal;

    private BooleanFilter active;

    private StringFilter createdBy;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private InstantFilter lastModifiedDate;

    private LongFilter saleOperationId;

    private LongFilter productId;

    private LongFilter salePaymentId;

    private Boolean distinct;

    public SaleCriteria() {}

    public SaleCriteria(SaleCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.productName = other.optionalProductName().map(StringFilter::copy).orElse(null);
        this.quantity = other.optionalQuantity().map(IntegerFilter::copy).orElse(null);
        this.unitPrice = other.optionalUnitPrice().map(BigDecimalFilter::copy).orElse(null);
        this.discount = other.optionalDiscount().map(BigDecimalFilter::copy).orElse(null);
        this.lineTotal = other.optionalLineTotal().map(BigDecimalFilter::copy).orElse(null);
        this.active = other.optionalActive().map(BooleanFilter::copy).orElse(null);
        this.createdBy = other.optionalCreatedBy().map(StringFilter::copy).orElse(null);
        this.createdDate = other.optionalCreatedDate().map(InstantFilter::copy).orElse(null);
        this.lastModifiedBy = other.optionalLastModifiedBy().map(StringFilter::copy).orElse(null);
        this.lastModifiedDate = other.optionalLastModifiedDate().map(InstantFilter::copy).orElse(null);
        this.saleOperationId = other.optionalSaleOperationId().map(LongFilter::copy).orElse(null);
        this.productId = other.optionalProductId().map(LongFilter::copy).orElse(null);
        this.salePaymentId = other.optionalSalePaymentId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public SaleCriteria copy() {
        return new SaleCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getProductName() {
        return productName;
    }

    public Optional<StringFilter> optionalProductName() {
        return Optional.ofNullable(productName);
    }

    public StringFilter productName() {
        if (productName == null) {
            setProductName(new StringFilter());
        }
        return productName;
    }

    public void setProductName(StringFilter productName) {
        this.productName = productName;
    }

    public IntegerFilter getQuantity() {
        return quantity;
    }

    public Optional<IntegerFilter> optionalQuantity() {
        return Optional.ofNullable(quantity);
    }

    public IntegerFilter quantity() {
        if (quantity == null) {
            setQuantity(new IntegerFilter());
        }
        return quantity;
    }

    public void setQuantity(IntegerFilter quantity) {
        this.quantity = quantity;
    }

    public BigDecimalFilter getUnitPrice() {
        return unitPrice;
    }

    public Optional<BigDecimalFilter> optionalUnitPrice() {
        return Optional.ofNullable(unitPrice);
    }

    public BigDecimalFilter unitPrice() {
        if (unitPrice == null) {
            setUnitPrice(new BigDecimalFilter());
        }
        return unitPrice;
    }

    public void setUnitPrice(BigDecimalFilter unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimalFilter getDiscount() {
        return discount;
    }

    public Optional<BigDecimalFilter> optionalDiscount() {
        return Optional.ofNullable(discount);
    }

    public BigDecimalFilter discount() {
        if (discount == null) {
            setDiscount(new BigDecimalFilter());
        }
        return discount;
    }

    public void setDiscount(BigDecimalFilter discount) {
        this.discount = discount;
    }

    public BigDecimalFilter getLineTotal() {
        return lineTotal;
    }

    public Optional<BigDecimalFilter> optionalLineTotal() {
        return Optional.ofNullable(lineTotal);
    }

    public BigDecimalFilter lineTotal() {
        if (lineTotal == null) {
            setLineTotal(new BigDecimalFilter());
        }
        return lineTotal;
    }

    public void setLineTotal(BigDecimalFilter lineTotal) {
        this.lineTotal = lineTotal;
    }

    public BooleanFilter getActive() {
        return active;
    }

    public Optional<BooleanFilter> optionalActive() {
        return Optional.ofNullable(active);
    }

    public BooleanFilter active() {
        if (active == null) {
            setActive(new BooleanFilter());
        }
        return active;
    }

    public void setActive(BooleanFilter active) {
        this.active = active;
    }

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public Optional<StringFilter> optionalCreatedBy() {
        return Optional.ofNullable(createdBy);
    }

    public StringFilter createdBy() {
        if (createdBy == null) {
            setCreatedBy(new StringFilter());
        }
        return createdBy;
    }

    public void setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
    }

    public InstantFilter getCreatedDate() {
        return createdDate;
    }

    public Optional<InstantFilter> optionalCreatedDate() {
        return Optional.ofNullable(createdDate);
    }

    public InstantFilter createdDate() {
        if (createdDate == null) {
            setCreatedDate(new InstantFilter());
        }
        return createdDate;
    }

    public void setCreatedDate(InstantFilter createdDate) {
        this.createdDate = createdDate;
    }

    public StringFilter getLastModifiedBy() {
        return lastModifiedBy;
    }

    public Optional<StringFilter> optionalLastModifiedBy() {
        return Optional.ofNullable(lastModifiedBy);
    }

    public StringFilter lastModifiedBy() {
        if (lastModifiedBy == null) {
            setLastModifiedBy(new StringFilter());
        }
        return lastModifiedBy;
    }

    public void setLastModifiedBy(StringFilter lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public InstantFilter getLastModifiedDate() {
        return lastModifiedDate;
    }

    public Optional<InstantFilter> optionalLastModifiedDate() {
        return Optional.ofNullable(lastModifiedDate);
    }

    public InstantFilter lastModifiedDate() {
        if (lastModifiedDate == null) {
            setLastModifiedDate(new InstantFilter());
        }
        return lastModifiedDate;
    }

    public void setLastModifiedDate(InstantFilter lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public LongFilter getSaleOperationId() {
        return saleOperationId;
    }

    public Optional<LongFilter> optionalSaleOperationId() {
        return Optional.ofNullable(saleOperationId);
    }

    public LongFilter saleOperationId() {
        if (saleOperationId == null) {
            setSaleOperationId(new LongFilter());
        }
        return saleOperationId;
    }

    public void setSaleOperationId(LongFilter saleOperationId) {
        this.saleOperationId = saleOperationId;
    }

    public LongFilter getProductId() {
        return productId;
    }

    public Optional<LongFilter> optionalProductId() {
        return Optional.ofNullable(productId);
    }

    public LongFilter productId() {
        if (productId == null) {
            setProductId(new LongFilter());
        }
        return productId;
    }

    public void setProductId(LongFilter productId) {
        this.productId = productId;
    }

    public LongFilter getSalePaymentId() {
        return salePaymentId;
    }

    public Optional<LongFilter> optionalSalePaymentId() {
        return Optional.ofNullable(salePaymentId);
    }

    public LongFilter salePaymentId() {
        if (salePaymentId == null) {
            setSalePaymentId(new LongFilter());
        }
        return salePaymentId;
    }

    public void setSalePaymentId(LongFilter salePaymentId) {
        this.salePaymentId = salePaymentId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SaleCriteria that = (SaleCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(productName, that.productName) &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(unitPrice, that.unitPrice) &&
            Objects.equals(discount, that.discount) &&
            Objects.equals(lineTotal, that.lineTotal) &&
            Objects.equals(active, that.active) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(saleOperationId, that.saleOperationId) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(salePaymentId, that.salePaymentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            productName,
            quantity,
            unitPrice,
            discount,
            lineTotal,
            active,
            createdBy,
            createdDate,
            lastModifiedBy,
            lastModifiedDate,
            saleOperationId,
            productId,
            salePaymentId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SaleCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalProductName().map(f -> "productName=" + f + ", ").orElse("") +
            optionalQuantity().map(f -> "quantity=" + f + ", ").orElse("") +
            optionalUnitPrice().map(f -> "unitPrice=" + f + ", ").orElse("") +
            optionalDiscount().map(f -> "discount=" + f + ", ").orElse("") +
            optionalLineTotal().map(f -> "lineTotal=" + f + ", ").orElse("") +
            optionalActive().map(f -> "active=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalCreatedDate().map(f -> "createdDate=" + f + ", ").orElse("") +
            optionalLastModifiedBy().map(f -> "lastModifiedBy=" + f + ", ").orElse("") +
            optionalLastModifiedDate().map(f -> "lastModifiedDate=" + f + ", ").orElse("") +
            optionalSaleOperationId().map(f -> "saleOperationId=" + f + ", ").orElse("") +
            optionalProductId().map(f -> "productId=" + f + ", ").orElse("") +
            optionalSalePaymentId().map(f -> "salePaymentId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
