package com.agamy.inventory.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.agamy.inventory.domain.DailyCashReconciliation} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DailyCashReconciliationDTO implements Serializable {

    private Long id;

    @NotNull
    private LocalDate date;

    private BigDecimal openingBalance;

    private BigDecimal totalSales;

    private BigDecimal totalPurchases;

    private BigDecimal totalSalaryPaid;

    private BigDecimal ownerDeposits;

    private BigDecimal withdrawals;

    private BigDecimal closingBalance;

    private String notes;

    private Boolean active;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public BigDecimal getTotalPurchases() {
        return totalPurchases;
    }

    public void setTotalPurchases(BigDecimal totalPurchases) {
        this.totalPurchases = totalPurchases;
    }

    public BigDecimal getTotalSalaryPaid() {
        return totalSalaryPaid;
    }

    public void setTotalSalaryPaid(BigDecimal totalSalaryPaid) {
        this.totalSalaryPaid = totalSalaryPaid;
    }

    public BigDecimal getOwnerDeposits() {
        return ownerDeposits;
    }

    public void setOwnerDeposits(BigDecimal ownerDeposits) {
        this.ownerDeposits = ownerDeposits;
    }

    public BigDecimal getWithdrawals() {
        return withdrawals;
    }

    public void setWithdrawals(BigDecimal withdrawals) {
        this.withdrawals = withdrawals;
    }

    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DailyCashReconciliationDTO)) {
            return false;
        }

        DailyCashReconciliationDTO dailyCashReconciliationDTO = (DailyCashReconciliationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, dailyCashReconciliationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DailyCashReconciliationDTO{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", openingBalance=" + getOpeningBalance() +
            ", totalSales=" + getTotalSales() +
            ", totalPurchases=" + getTotalPurchases() +
            ", totalSalaryPaid=" + getTotalSalaryPaid() +
            ", ownerDeposits=" + getOwnerDeposits() +
            ", withdrawals=" + getWithdrawals() +
            ", closingBalance=" + getClosingBalance() +
            ", notes='" + getNotes() + "'" +
            ", active='" + getActive() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
