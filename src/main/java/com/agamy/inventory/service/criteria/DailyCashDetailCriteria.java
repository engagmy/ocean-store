package com.agamy.inventory.service.criteria;

import com.agamy.inventory.domain.enumeration.CashTransactionType;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.agamy.inventory.domain.DailyCashDetail} entity. This class is used
 * in {@link com.agamy.inventory.web.rest.DailyCashDetailResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /daily-cash-details?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DailyCashDetailCriteria implements Serializable, Criteria {

    /**
     * Class for filtering CashTransactionType
     */
    public static class CashTransactionTypeFilter extends Filter<CashTransactionType> {

        public CashTransactionTypeFilter() {}

        public CashTransactionTypeFilter(CashTransactionTypeFilter filter) {
            super(filter);
        }

        @Override
        public CashTransactionTypeFilter copy() {
            return new CashTransactionTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private CashTransactionTypeFilter type;

    private LongFilter referenceId;

    private StringFilter referenceType;

    private BigDecimalFilter amount;

    private StringFilter description;

    private InstantFilter timestamp;

    private BooleanFilter active;

    private StringFilter createdBy;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private InstantFilter lastModifiedDate;

    private LongFilter dailyCashReconciliationId;

    private Boolean distinct;

    public DailyCashDetailCriteria() {}

    public DailyCashDetailCriteria(DailyCashDetailCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.type = other.optionalType().map(CashTransactionTypeFilter::copy).orElse(null);
        this.referenceId = other.optionalReferenceId().map(LongFilter::copy).orElse(null);
        this.referenceType = other.optionalReferenceType().map(StringFilter::copy).orElse(null);
        this.amount = other.optionalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.timestamp = other.optionalTimestamp().map(InstantFilter::copy).orElse(null);
        this.active = other.optionalActive().map(BooleanFilter::copy).orElse(null);
        this.createdBy = other.optionalCreatedBy().map(StringFilter::copy).orElse(null);
        this.createdDate = other.optionalCreatedDate().map(InstantFilter::copy).orElse(null);
        this.lastModifiedBy = other.optionalLastModifiedBy().map(StringFilter::copy).orElse(null);
        this.lastModifiedDate = other.optionalLastModifiedDate().map(InstantFilter::copy).orElse(null);
        this.dailyCashReconciliationId = other.optionalDailyCashReconciliationId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DailyCashDetailCriteria copy() {
        return new DailyCashDetailCriteria(this);
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

    public CashTransactionTypeFilter getType() {
        return type;
    }

    public Optional<CashTransactionTypeFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public CashTransactionTypeFilter type() {
        if (type == null) {
            setType(new CashTransactionTypeFilter());
        }
        return type;
    }

    public void setType(CashTransactionTypeFilter type) {
        this.type = type;
    }

    public LongFilter getReferenceId() {
        return referenceId;
    }

    public Optional<LongFilter> optionalReferenceId() {
        return Optional.ofNullable(referenceId);
    }

    public LongFilter referenceId() {
        if (referenceId == null) {
            setReferenceId(new LongFilter());
        }
        return referenceId;
    }

    public void setReferenceId(LongFilter referenceId) {
        this.referenceId = referenceId;
    }

    public StringFilter getReferenceType() {
        return referenceType;
    }

    public Optional<StringFilter> optionalReferenceType() {
        return Optional.ofNullable(referenceType);
    }

    public StringFilter referenceType() {
        if (referenceType == null) {
            setReferenceType(new StringFilter());
        }
        return referenceType;
    }

    public void setReferenceType(StringFilter referenceType) {
        this.referenceType = referenceType;
    }

    public BigDecimalFilter getAmount() {
        return amount;
    }

    public Optional<BigDecimalFilter> optionalAmount() {
        return Optional.ofNullable(amount);
    }

    public BigDecimalFilter amount() {
        if (amount == null) {
            setAmount(new BigDecimalFilter());
        }
        return amount;
    }

    public void setAmount(BigDecimalFilter amount) {
        this.amount = amount;
    }

    public StringFilter getDescription() {
        return description;
    }

    public Optional<StringFilter> optionalDescription() {
        return Optional.ofNullable(description);
    }

    public StringFilter description() {
        if (description == null) {
            setDescription(new StringFilter());
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public InstantFilter getTimestamp() {
        return timestamp;
    }

    public Optional<InstantFilter> optionalTimestamp() {
        return Optional.ofNullable(timestamp);
    }

    public InstantFilter timestamp() {
        if (timestamp == null) {
            setTimestamp(new InstantFilter());
        }
        return timestamp;
    }

    public void setTimestamp(InstantFilter timestamp) {
        this.timestamp = timestamp;
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

    public LongFilter getDailyCashReconciliationId() {
        return dailyCashReconciliationId;
    }

    public Optional<LongFilter> optionalDailyCashReconciliationId() {
        return Optional.ofNullable(dailyCashReconciliationId);
    }

    public LongFilter dailyCashReconciliationId() {
        if (dailyCashReconciliationId == null) {
            setDailyCashReconciliationId(new LongFilter());
        }
        return dailyCashReconciliationId;
    }

    public void setDailyCashReconciliationId(LongFilter dailyCashReconciliationId) {
        this.dailyCashReconciliationId = dailyCashReconciliationId;
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
        final DailyCashDetailCriteria that = (DailyCashDetailCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(type, that.type) &&
            Objects.equals(referenceId, that.referenceId) &&
            Objects.equals(referenceType, that.referenceType) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(description, that.description) &&
            Objects.equals(timestamp, that.timestamp) &&
            Objects.equals(active, that.active) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(dailyCashReconciliationId, that.dailyCashReconciliationId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            type,
            referenceId,
            referenceType,
            amount,
            description,
            timestamp,
            active,
            createdBy,
            createdDate,
            lastModifiedBy,
            lastModifiedDate,
            dailyCashReconciliationId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DailyCashDetailCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalReferenceId().map(f -> "referenceId=" + f + ", ").orElse("") +
            optionalReferenceType().map(f -> "referenceType=" + f + ", ").orElse("") +
            optionalAmount().map(f -> "amount=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalTimestamp().map(f -> "timestamp=" + f + ", ").orElse("") +
            optionalActive().map(f -> "active=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalCreatedDate().map(f -> "createdDate=" + f + ", ").orElse("") +
            optionalLastModifiedBy().map(f -> "lastModifiedBy=" + f + ", ").orElse("") +
            optionalLastModifiedDate().map(f -> "lastModifiedDate=" + f + ", ").orElse("") +
            optionalDailyCashReconciliationId().map(f -> "dailyCashReconciliationId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
