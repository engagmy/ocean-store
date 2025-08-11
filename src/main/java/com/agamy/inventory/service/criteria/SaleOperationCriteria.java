package com.agamy.inventory.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.agamy.inventory.domain.SaleOperation} entity. This class is used
 * in {@link com.agamy.inventory.web.rest.SaleOperationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /sale-operations?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SaleOperationCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter date;

    private IntegerFilter totalQuantity;

    private BigDecimalFilter totalAmount;

    private BigDecimalFilter totalDiscount;

    private BigDecimalFilter grandTotal;

    private BooleanFilter active;

    private StringFilter createdBy;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private InstantFilter lastModifiedDate;

    private LongFilter billId;

    private LongFilter customerId;

    private LongFilter saleId;

    private Boolean distinct;

    public SaleOperationCriteria() {}

    public SaleOperationCriteria(SaleOperationCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.date = other.optionalDate().map(InstantFilter::copy).orElse(null);
        this.totalQuantity = other.optionalTotalQuantity().map(IntegerFilter::copy).orElse(null);
        this.totalAmount = other.optionalTotalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.totalDiscount = other.optionalTotalDiscount().map(BigDecimalFilter::copy).orElse(null);
        this.grandTotal = other.optionalGrandTotal().map(BigDecimalFilter::copy).orElse(null);
        this.active = other.optionalActive().map(BooleanFilter::copy).orElse(null);
        this.createdBy = other.optionalCreatedBy().map(StringFilter::copy).orElse(null);
        this.createdDate = other.optionalCreatedDate().map(InstantFilter::copy).orElse(null);
        this.lastModifiedBy = other.optionalLastModifiedBy().map(StringFilter::copy).orElse(null);
        this.lastModifiedDate = other.optionalLastModifiedDate().map(InstantFilter::copy).orElse(null);
        this.billId = other.optionalBillId().map(LongFilter::copy).orElse(null);
        this.customerId = other.optionalCustomerId().map(LongFilter::copy).orElse(null);
        this.saleId = other.optionalSaleId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public SaleOperationCriteria copy() {
        return new SaleOperationCriteria(this);
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

    public InstantFilter getDate() {
        return date;
    }

    public Optional<InstantFilter> optionalDate() {
        return Optional.ofNullable(date);
    }

    public InstantFilter date() {
        if (date == null) {
            setDate(new InstantFilter());
        }
        return date;
    }

    public void setDate(InstantFilter date) {
        this.date = date;
    }

    public IntegerFilter getTotalQuantity() {
        return totalQuantity;
    }

    public Optional<IntegerFilter> optionalTotalQuantity() {
        return Optional.ofNullable(totalQuantity);
    }

    public IntegerFilter totalQuantity() {
        if (totalQuantity == null) {
            setTotalQuantity(new IntegerFilter());
        }
        return totalQuantity;
    }

    public void setTotalQuantity(IntegerFilter totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimalFilter getTotalAmount() {
        return totalAmount;
    }

    public Optional<BigDecimalFilter> optionalTotalAmount() {
        return Optional.ofNullable(totalAmount);
    }

    public BigDecimalFilter totalAmount() {
        if (totalAmount == null) {
            setTotalAmount(new BigDecimalFilter());
        }
        return totalAmount;
    }

    public void setTotalAmount(BigDecimalFilter totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimalFilter getTotalDiscount() {
        return totalDiscount;
    }

    public Optional<BigDecimalFilter> optionalTotalDiscount() {
        return Optional.ofNullable(totalDiscount);
    }

    public BigDecimalFilter totalDiscount() {
        if (totalDiscount == null) {
            setTotalDiscount(new BigDecimalFilter());
        }
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimalFilter totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public BigDecimalFilter getGrandTotal() {
        return grandTotal;
    }

    public Optional<BigDecimalFilter> optionalGrandTotal() {
        return Optional.ofNullable(grandTotal);
    }

    public BigDecimalFilter grandTotal() {
        if (grandTotal == null) {
            setGrandTotal(new BigDecimalFilter());
        }
        return grandTotal;
    }

    public void setGrandTotal(BigDecimalFilter grandTotal) {
        this.grandTotal = grandTotal;
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

    public LongFilter getBillId() {
        return billId;
    }

    public Optional<LongFilter> optionalBillId() {
        return Optional.ofNullable(billId);
    }

    public LongFilter billId() {
        if (billId == null) {
            setBillId(new LongFilter());
        }
        return billId;
    }

    public void setBillId(LongFilter billId) {
        this.billId = billId;
    }

    public LongFilter getCustomerId() {
        return customerId;
    }

    public Optional<LongFilter> optionalCustomerId() {
        return Optional.ofNullable(customerId);
    }

    public LongFilter customerId() {
        if (customerId == null) {
            setCustomerId(new LongFilter());
        }
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
    }

    public LongFilter getSaleId() {
        return saleId;
    }

    public Optional<LongFilter> optionalSaleId() {
        return Optional.ofNullable(saleId);
    }

    public LongFilter saleId() {
        if (saleId == null) {
            setSaleId(new LongFilter());
        }
        return saleId;
    }

    public void setSaleId(LongFilter saleId) {
        this.saleId = saleId;
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
        final SaleOperationCriteria that = (SaleOperationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(date, that.date) &&
            Objects.equals(totalQuantity, that.totalQuantity) &&
            Objects.equals(totalAmount, that.totalAmount) &&
            Objects.equals(totalDiscount, that.totalDiscount) &&
            Objects.equals(grandTotal, that.grandTotal) &&
            Objects.equals(active, that.active) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(billId, that.billId) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(saleId, that.saleId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            date,
            totalQuantity,
            totalAmount,
            totalDiscount,
            grandTotal,
            active,
            createdBy,
            createdDate,
            lastModifiedBy,
            lastModifiedDate,
            billId,
            customerId,
            saleId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SaleOperationCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalDate().map(f -> "date=" + f + ", ").orElse("") +
            optionalTotalQuantity().map(f -> "totalQuantity=" + f + ", ").orElse("") +
            optionalTotalAmount().map(f -> "totalAmount=" + f + ", ").orElse("") +
            optionalTotalDiscount().map(f -> "totalDiscount=" + f + ", ").orElse("") +
            optionalGrandTotal().map(f -> "grandTotal=" + f + ", ").orElse("") +
            optionalActive().map(f -> "active=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalCreatedDate().map(f -> "createdDate=" + f + ", ").orElse("") +
            optionalLastModifiedBy().map(f -> "lastModifiedBy=" + f + ", ").orElse("") +
            optionalLastModifiedDate().map(f -> "lastModifiedDate=" + f + ", ").orElse("") +
            optionalBillId().map(f -> "billId=" + f + ", ").orElse("") +
            optionalCustomerId().map(f -> "customerId=" + f + ", ").orElse("") +
            optionalSaleId().map(f -> "saleId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
