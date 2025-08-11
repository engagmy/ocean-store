package com.agamy.inventory.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.agamy.inventory.domain.Bill} entity. This class is used
 * in {@link com.agamy.inventory.web.rest.BillResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /bills?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BillCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter billNumber;

    private InstantFilter date;

    private BigDecimalFilter totalAmount;

    private BigDecimalFilter taxAmount;

    private BigDecimalFilter discountAmount;

    private BigDecimalFilter paidAmount;

    private BigDecimalFilter dueAmount;

    private StringFilter notes;

    private BooleanFilter active;

    private StringFilter createdBy;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private InstantFilter lastModifiedDate;

    private LongFilter saleOperationId;

    private LongFilter purchaseOperationId;

    private Boolean distinct;

    public BillCriteria() {}

    public BillCriteria(BillCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.billNumber = other.optionalBillNumber().map(StringFilter::copy).orElse(null);
        this.date = other.optionalDate().map(InstantFilter::copy).orElse(null);
        this.totalAmount = other.optionalTotalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.taxAmount = other.optionalTaxAmount().map(BigDecimalFilter::copy).orElse(null);
        this.discountAmount = other.optionalDiscountAmount().map(BigDecimalFilter::copy).orElse(null);
        this.paidAmount = other.optionalPaidAmount().map(BigDecimalFilter::copy).orElse(null);
        this.dueAmount = other.optionalDueAmount().map(BigDecimalFilter::copy).orElse(null);
        this.notes = other.optionalNotes().map(StringFilter::copy).orElse(null);
        this.active = other.optionalActive().map(BooleanFilter::copy).orElse(null);
        this.createdBy = other.optionalCreatedBy().map(StringFilter::copy).orElse(null);
        this.createdDate = other.optionalCreatedDate().map(InstantFilter::copy).orElse(null);
        this.lastModifiedBy = other.optionalLastModifiedBy().map(StringFilter::copy).orElse(null);
        this.lastModifiedDate = other.optionalLastModifiedDate().map(InstantFilter::copy).orElse(null);
        this.saleOperationId = other.optionalSaleOperationId().map(LongFilter::copy).orElse(null);
        this.purchaseOperationId = other.optionalPurchaseOperationId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public BillCriteria copy() {
        return new BillCriteria(this);
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

    public StringFilter getBillNumber() {
        return billNumber;
    }

    public Optional<StringFilter> optionalBillNumber() {
        return Optional.ofNullable(billNumber);
    }

    public StringFilter billNumber() {
        if (billNumber == null) {
            setBillNumber(new StringFilter());
        }
        return billNumber;
    }

    public void setBillNumber(StringFilter billNumber) {
        this.billNumber = billNumber;
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

    public BigDecimalFilter getTaxAmount() {
        return taxAmount;
    }

    public Optional<BigDecimalFilter> optionalTaxAmount() {
        return Optional.ofNullable(taxAmount);
    }

    public BigDecimalFilter taxAmount() {
        if (taxAmount == null) {
            setTaxAmount(new BigDecimalFilter());
        }
        return taxAmount;
    }

    public void setTaxAmount(BigDecimalFilter taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimalFilter getDiscountAmount() {
        return discountAmount;
    }

    public Optional<BigDecimalFilter> optionalDiscountAmount() {
        return Optional.ofNullable(discountAmount);
    }

    public BigDecimalFilter discountAmount() {
        if (discountAmount == null) {
            setDiscountAmount(new BigDecimalFilter());
        }
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimalFilter discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimalFilter getPaidAmount() {
        return paidAmount;
    }

    public Optional<BigDecimalFilter> optionalPaidAmount() {
        return Optional.ofNullable(paidAmount);
    }

    public BigDecimalFilter paidAmount() {
        if (paidAmount == null) {
            setPaidAmount(new BigDecimalFilter());
        }
        return paidAmount;
    }

    public void setPaidAmount(BigDecimalFilter paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimalFilter getDueAmount() {
        return dueAmount;
    }

    public Optional<BigDecimalFilter> optionalDueAmount() {
        return Optional.ofNullable(dueAmount);
    }

    public BigDecimalFilter dueAmount() {
        if (dueAmount == null) {
            setDueAmount(new BigDecimalFilter());
        }
        return dueAmount;
    }

    public void setDueAmount(BigDecimalFilter dueAmount) {
        this.dueAmount = dueAmount;
    }

    public StringFilter getNotes() {
        return notes;
    }

    public Optional<StringFilter> optionalNotes() {
        return Optional.ofNullable(notes);
    }

    public StringFilter notes() {
        if (notes == null) {
            setNotes(new StringFilter());
        }
        return notes;
    }

    public void setNotes(StringFilter notes) {
        this.notes = notes;
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

    public LongFilter getPurchaseOperationId() {
        return purchaseOperationId;
    }

    public Optional<LongFilter> optionalPurchaseOperationId() {
        return Optional.ofNullable(purchaseOperationId);
    }

    public LongFilter purchaseOperationId() {
        if (purchaseOperationId == null) {
            setPurchaseOperationId(new LongFilter());
        }
        return purchaseOperationId;
    }

    public void setPurchaseOperationId(LongFilter purchaseOperationId) {
        this.purchaseOperationId = purchaseOperationId;
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
        final BillCriteria that = (BillCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(billNumber, that.billNumber) &&
            Objects.equals(date, that.date) &&
            Objects.equals(totalAmount, that.totalAmount) &&
            Objects.equals(taxAmount, that.taxAmount) &&
            Objects.equals(discountAmount, that.discountAmount) &&
            Objects.equals(paidAmount, that.paidAmount) &&
            Objects.equals(dueAmount, that.dueAmount) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(active, that.active) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(saleOperationId, that.saleOperationId) &&
            Objects.equals(purchaseOperationId, that.purchaseOperationId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            billNumber,
            date,
            totalAmount,
            taxAmount,
            discountAmount,
            paidAmount,
            dueAmount,
            notes,
            active,
            createdBy,
            createdDate,
            lastModifiedBy,
            lastModifiedDate,
            saleOperationId,
            purchaseOperationId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BillCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalBillNumber().map(f -> "billNumber=" + f + ", ").orElse("") +
            optionalDate().map(f -> "date=" + f + ", ").orElse("") +
            optionalTotalAmount().map(f -> "totalAmount=" + f + ", ").orElse("") +
            optionalTaxAmount().map(f -> "taxAmount=" + f + ", ").orElse("") +
            optionalDiscountAmount().map(f -> "discountAmount=" + f + ", ").orElse("") +
            optionalPaidAmount().map(f -> "paidAmount=" + f + ", ").orElse("") +
            optionalDueAmount().map(f -> "dueAmount=" + f + ", ").orElse("") +
            optionalNotes().map(f -> "notes=" + f + ", ").orElse("") +
            optionalActive().map(f -> "active=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalCreatedDate().map(f -> "createdDate=" + f + ", ").orElse("") +
            optionalLastModifiedBy().map(f -> "lastModifiedBy=" + f + ", ").orElse("") +
            optionalLastModifiedDate().map(f -> "lastModifiedDate=" + f + ", ").orElse("") +
            optionalSaleOperationId().map(f -> "saleOperationId=" + f + ", ").orElse("") +
            optionalPurchaseOperationId().map(f -> "purchaseOperationId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
