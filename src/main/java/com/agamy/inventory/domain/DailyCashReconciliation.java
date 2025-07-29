package com.agamy.inventory.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A DailyCashReconciliation.
 */
@Entity
@Table(name = "daily_cash_reconciliation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DailyCashReconciliation extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "opening_balance", precision = 21, scale = 2)
    private BigDecimal openingBalance;

    @Column(name = "total_sales", precision = 21, scale = 2)
    private BigDecimal totalSales;

    @Column(name = "total_purchases", precision = 21, scale = 2)
    private BigDecimal totalPurchases;

    @Column(name = "total_salary_paid", precision = 21, scale = 2)
    private BigDecimal totalSalaryPaid;

    @Column(name = "owner_deposits", precision = 21, scale = 2)
    private BigDecimal ownerDeposits;

    @Column(name = "withdrawals", precision = 21, scale = 2)
    private BigDecimal withdrawals;

    @Column(name = "closing_balance", precision = 21, scale = 2)
    private BigDecimal closingBalance;

    @Column(name = "notes")
    private String notes;

    @Column(name = "active")
    private Boolean active;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition
    @org.springframework.data.annotation.Transient
    @Transient
    private boolean isPersisted;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dailyCashReconciliation")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "dailyCashReconciliation" }, allowSetters = true)
    private Set<DailyCashDetail> dailyCashDetails = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DailyCashReconciliation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public DailyCashReconciliation date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getOpeningBalance() {
        return this.openingBalance;
    }

    public DailyCashReconciliation openingBalance(BigDecimal openingBalance) {
        this.setOpeningBalance(openingBalance);
        return this;
    }

    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }

    public BigDecimal getTotalSales() {
        return this.totalSales;
    }

    public DailyCashReconciliation totalSales(BigDecimal totalSales) {
        this.setTotalSales(totalSales);
        return this;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public BigDecimal getTotalPurchases() {
        return this.totalPurchases;
    }

    public DailyCashReconciliation totalPurchases(BigDecimal totalPurchases) {
        this.setTotalPurchases(totalPurchases);
        return this;
    }

    public void setTotalPurchases(BigDecimal totalPurchases) {
        this.totalPurchases = totalPurchases;
    }

    public BigDecimal getTotalSalaryPaid() {
        return this.totalSalaryPaid;
    }

    public DailyCashReconciliation totalSalaryPaid(BigDecimal totalSalaryPaid) {
        this.setTotalSalaryPaid(totalSalaryPaid);
        return this;
    }

    public void setTotalSalaryPaid(BigDecimal totalSalaryPaid) {
        this.totalSalaryPaid = totalSalaryPaid;
    }

    public BigDecimal getOwnerDeposits() {
        return this.ownerDeposits;
    }

    public DailyCashReconciliation ownerDeposits(BigDecimal ownerDeposits) {
        this.setOwnerDeposits(ownerDeposits);
        return this;
    }

    public void setOwnerDeposits(BigDecimal ownerDeposits) {
        this.ownerDeposits = ownerDeposits;
    }

    public BigDecimal getWithdrawals() {
        return this.withdrawals;
    }

    public DailyCashReconciliation withdrawals(BigDecimal withdrawals) {
        this.setWithdrawals(withdrawals);
        return this;
    }

    public void setWithdrawals(BigDecimal withdrawals) {
        this.withdrawals = withdrawals;
    }

    public BigDecimal getClosingBalance() {
        return this.closingBalance;
    }

    public DailyCashReconciliation closingBalance(BigDecimal closingBalance) {
        this.setClosingBalance(closingBalance);
        return this;
    }

    public void setClosingBalance(BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }

    public String getNotes() {
        return this.notes;
    }

    public DailyCashReconciliation notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getActive() {
        return this.active;
    }

    public DailyCashReconciliation active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    // Inherited createdBy methods
    public DailyCashReconciliation createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public DailyCashReconciliation createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public DailyCashReconciliation lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public DailyCashReconciliation lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    @PostLoad
    @PostPersist
    public void updateEntityState() {
        this.setIsPersisted();
    }

    @org.springframework.data.annotation.Transient
    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public DailyCashReconciliation setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Set<DailyCashDetail> getDailyCashDetails() {
        return this.dailyCashDetails;
    }

    public void setDailyCashDetails(Set<DailyCashDetail> dailyCashDetails) {
        if (this.dailyCashDetails != null) {
            this.dailyCashDetails.forEach(i -> i.setDailyCashReconciliation(null));
        }
        if (dailyCashDetails != null) {
            dailyCashDetails.forEach(i -> i.setDailyCashReconciliation(this));
        }
        this.dailyCashDetails = dailyCashDetails;
    }

    public DailyCashReconciliation dailyCashDetails(Set<DailyCashDetail> dailyCashDetails) {
        this.setDailyCashDetails(dailyCashDetails);
        return this;
    }

    public DailyCashReconciliation addDailyCashDetail(DailyCashDetail dailyCashDetail) {
        this.dailyCashDetails.add(dailyCashDetail);
        dailyCashDetail.setDailyCashReconciliation(this);
        return this;
    }

    public DailyCashReconciliation removeDailyCashDetail(DailyCashDetail dailyCashDetail) {
        this.dailyCashDetails.remove(dailyCashDetail);
        dailyCashDetail.setDailyCashReconciliation(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DailyCashReconciliation)) {
            return false;
        }
        return getId() != null && getId().equals(((DailyCashReconciliation) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DailyCashReconciliation{" +
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
