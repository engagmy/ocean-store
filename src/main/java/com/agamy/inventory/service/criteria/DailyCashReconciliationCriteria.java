package com.agamy.inventory.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.agamy.inventory.domain.DailyCashReconciliation} entity. This class is used
 * in {@link com.agamy.inventory.web.rest.DailyCashReconciliationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /daily-cash-reconciliations?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DailyCashReconciliationCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter date;

    private BigDecimalFilter openingBalance;

    private BigDecimalFilter totalSales;

    private BigDecimalFilter totalPurchases;

    private BigDecimalFilter totalSalaryPaid;

    private BigDecimalFilter ownerDeposits;

    private BigDecimalFilter withdrawals;

    private BigDecimalFilter closingBalance;

    private StringFilter notes;

    private BooleanFilter active;

    private StringFilter createdBy;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private InstantFilter lastModifiedDate;

    private LongFilter dailyCashDetailId;

    private Boolean distinct;

    public DailyCashReconciliationCriteria() {}

    public DailyCashReconciliationCriteria(DailyCashReconciliationCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.date = other.optionalDate().map(LocalDateFilter::copy).orElse(null);
        this.openingBalance = other.optionalOpeningBalance().map(BigDecimalFilter::copy).orElse(null);
        this.totalSales = other.optionalTotalSales().map(BigDecimalFilter::copy).orElse(null);
        this.totalPurchases = other.optionalTotalPurchases().map(BigDecimalFilter::copy).orElse(null);
        this.totalSalaryPaid = other.optionalTotalSalaryPaid().map(BigDecimalFilter::copy).orElse(null);
        this.ownerDeposits = other.optionalOwnerDeposits().map(BigDecimalFilter::copy).orElse(null);
        this.withdrawals = other.optionalWithdrawals().map(BigDecimalFilter::copy).orElse(null);
        this.closingBalance = other.optionalClosingBalance().map(BigDecimalFilter::copy).orElse(null);
        this.notes = other.optionalNotes().map(StringFilter::copy).orElse(null);
        this.active = other.optionalActive().map(BooleanFilter::copy).orElse(null);
        this.createdBy = other.optionalCreatedBy().map(StringFilter::copy).orElse(null);
        this.createdDate = other.optionalCreatedDate().map(InstantFilter::copy).orElse(null);
        this.lastModifiedBy = other.optionalLastModifiedBy().map(StringFilter::copy).orElse(null);
        this.lastModifiedDate = other.optionalLastModifiedDate().map(InstantFilter::copy).orElse(null);
        this.dailyCashDetailId = other.optionalDailyCashDetailId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DailyCashReconciliationCriteria copy() {
        return new DailyCashReconciliationCriteria(this);
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

    public LocalDateFilter getDate() {
        return date;
    }

    public Optional<LocalDateFilter> optionalDate() {
        return Optional.ofNullable(date);
    }

    public LocalDateFilter date() {
        if (date == null) {
            setDate(new LocalDateFilter());
        }
        return date;
    }

    public void setDate(LocalDateFilter date) {
        this.date = date;
    }

    public BigDecimalFilter getOpeningBalance() {
        return openingBalance;
    }

    public Optional<BigDecimalFilter> optionalOpeningBalance() {
        return Optional.ofNullable(openingBalance);
    }

    public BigDecimalFilter openingBalance() {
        if (openingBalance == null) {
            setOpeningBalance(new BigDecimalFilter());
        }
        return openingBalance;
    }

    public void setOpeningBalance(BigDecimalFilter openingBalance) {
        this.openingBalance = openingBalance;
    }

    public BigDecimalFilter getTotalSales() {
        return totalSales;
    }

    public Optional<BigDecimalFilter> optionalTotalSales() {
        return Optional.ofNullable(totalSales);
    }

    public BigDecimalFilter totalSales() {
        if (totalSales == null) {
            setTotalSales(new BigDecimalFilter());
        }
        return totalSales;
    }

    public void setTotalSales(BigDecimalFilter totalSales) {
        this.totalSales = totalSales;
    }

    public BigDecimalFilter getTotalPurchases() {
        return totalPurchases;
    }

    public Optional<BigDecimalFilter> optionalTotalPurchases() {
        return Optional.ofNullable(totalPurchases);
    }

    public BigDecimalFilter totalPurchases() {
        if (totalPurchases == null) {
            setTotalPurchases(new BigDecimalFilter());
        }
        return totalPurchases;
    }

    public void setTotalPurchases(BigDecimalFilter totalPurchases) {
        this.totalPurchases = totalPurchases;
    }

    public BigDecimalFilter getTotalSalaryPaid() {
        return totalSalaryPaid;
    }

    public Optional<BigDecimalFilter> optionalTotalSalaryPaid() {
        return Optional.ofNullable(totalSalaryPaid);
    }

    public BigDecimalFilter totalSalaryPaid() {
        if (totalSalaryPaid == null) {
            setTotalSalaryPaid(new BigDecimalFilter());
        }
        return totalSalaryPaid;
    }

    public void setTotalSalaryPaid(BigDecimalFilter totalSalaryPaid) {
        this.totalSalaryPaid = totalSalaryPaid;
    }

    public BigDecimalFilter getOwnerDeposits() {
        return ownerDeposits;
    }

    public Optional<BigDecimalFilter> optionalOwnerDeposits() {
        return Optional.ofNullable(ownerDeposits);
    }

    public BigDecimalFilter ownerDeposits() {
        if (ownerDeposits == null) {
            setOwnerDeposits(new BigDecimalFilter());
        }
        return ownerDeposits;
    }

    public void setOwnerDeposits(BigDecimalFilter ownerDeposits) {
        this.ownerDeposits = ownerDeposits;
    }

    public BigDecimalFilter getWithdrawals() {
        return withdrawals;
    }

    public Optional<BigDecimalFilter> optionalWithdrawals() {
        return Optional.ofNullable(withdrawals);
    }

    public BigDecimalFilter withdrawals() {
        if (withdrawals == null) {
            setWithdrawals(new BigDecimalFilter());
        }
        return withdrawals;
    }

    public void setWithdrawals(BigDecimalFilter withdrawals) {
        this.withdrawals = withdrawals;
    }

    public BigDecimalFilter getClosingBalance() {
        return closingBalance;
    }

    public Optional<BigDecimalFilter> optionalClosingBalance() {
        return Optional.ofNullable(closingBalance);
    }

    public BigDecimalFilter closingBalance() {
        if (closingBalance == null) {
            setClosingBalance(new BigDecimalFilter());
        }
        return closingBalance;
    }

    public void setClosingBalance(BigDecimalFilter closingBalance) {
        this.closingBalance = closingBalance;
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

    public LongFilter getDailyCashDetailId() {
        return dailyCashDetailId;
    }

    public Optional<LongFilter> optionalDailyCashDetailId() {
        return Optional.ofNullable(dailyCashDetailId);
    }

    public LongFilter dailyCashDetailId() {
        if (dailyCashDetailId == null) {
            setDailyCashDetailId(new LongFilter());
        }
        return dailyCashDetailId;
    }

    public void setDailyCashDetailId(LongFilter dailyCashDetailId) {
        this.dailyCashDetailId = dailyCashDetailId;
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
        final DailyCashReconciliationCriteria that = (DailyCashReconciliationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(date, that.date) &&
            Objects.equals(openingBalance, that.openingBalance) &&
            Objects.equals(totalSales, that.totalSales) &&
            Objects.equals(totalPurchases, that.totalPurchases) &&
            Objects.equals(totalSalaryPaid, that.totalSalaryPaid) &&
            Objects.equals(ownerDeposits, that.ownerDeposits) &&
            Objects.equals(withdrawals, that.withdrawals) &&
            Objects.equals(closingBalance, that.closingBalance) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(active, that.active) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(dailyCashDetailId, that.dailyCashDetailId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            date,
            openingBalance,
            totalSales,
            totalPurchases,
            totalSalaryPaid,
            ownerDeposits,
            withdrawals,
            closingBalance,
            notes,
            active,
            createdBy,
            createdDate,
            lastModifiedBy,
            lastModifiedDate,
            dailyCashDetailId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DailyCashReconciliationCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalDate().map(f -> "date=" + f + ", ").orElse("") +
            optionalOpeningBalance().map(f -> "openingBalance=" + f + ", ").orElse("") +
            optionalTotalSales().map(f -> "totalSales=" + f + ", ").orElse("") +
            optionalTotalPurchases().map(f -> "totalPurchases=" + f + ", ").orElse("") +
            optionalTotalSalaryPaid().map(f -> "totalSalaryPaid=" + f + ", ").orElse("") +
            optionalOwnerDeposits().map(f -> "ownerDeposits=" + f + ", ").orElse("") +
            optionalWithdrawals().map(f -> "withdrawals=" + f + ", ").orElse("") +
            optionalClosingBalance().map(f -> "closingBalance=" + f + ", ").orElse("") +
            optionalNotes().map(f -> "notes=" + f + ", ").orElse("") +
            optionalActive().map(f -> "active=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalCreatedDate().map(f -> "createdDate=" + f + ", ").orElse("") +
            optionalLastModifiedBy().map(f -> "lastModifiedBy=" + f + ", ").orElse("") +
            optionalLastModifiedDate().map(f -> "lastModifiedDate=" + f + ", ").orElse("") +
            optionalDailyCashDetailId().map(f -> "dailyCashDetailId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
