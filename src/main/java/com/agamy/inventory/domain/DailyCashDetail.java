package com.agamy.inventory.domain;

import com.agamy.inventory.domain.enumeration.CashTransactionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A DailyCashDetail.
 */
@Entity
@Table(name = "daily_cash_detail")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DailyCashDetail extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CashTransactionType type;

    @NotNull
    @Column(name = "reference_id", nullable = false)
    private Long referenceId;

    @NotNull
    @Column(name = "reference_type", nullable = false)
    private String referenceType;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "active")
    private Boolean active;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition
    @org.springframework.data.annotation.Transient
    @Transient
    private boolean isPersisted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "dailyCashDetails" }, allowSetters = true)
    private DailyCashReconciliation dailyCashReconciliation;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DailyCashDetail id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CashTransactionType getType() {
        return this.type;
    }

    public DailyCashDetail type(CashTransactionType type) {
        this.setType(type);
        return this;
    }

    public void setType(CashTransactionType type) {
        this.type = type;
    }

    public Long getReferenceId() {
        return this.referenceId;
    }

    public DailyCashDetail referenceId(Long referenceId) {
        this.setReferenceId(referenceId);
        return this;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public String getReferenceType() {
        return this.referenceType;
    }

    public DailyCashDetail referenceType(String referenceType) {
        this.setReferenceType(referenceType);
        return this;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public DailyCashDetail amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return this.description;
    }

    public DailyCashDetail description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public DailyCashDetail timestamp(Instant timestamp) {
        this.setTimestamp(timestamp);
        return this;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getActive() {
        return this.active;
    }

    public DailyCashDetail active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    // Inherited createdBy methods
    public DailyCashDetail createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public DailyCashDetail createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public DailyCashDetail lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public DailyCashDetail lastModifiedDate(Instant lastModifiedDate) {
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

    public DailyCashDetail setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public DailyCashReconciliation getDailyCashReconciliation() {
        return this.dailyCashReconciliation;
    }

    public void setDailyCashReconciliation(DailyCashReconciliation dailyCashReconciliation) {
        this.dailyCashReconciliation = dailyCashReconciliation;
    }

    public DailyCashDetail dailyCashReconciliation(DailyCashReconciliation dailyCashReconciliation) {
        this.setDailyCashReconciliation(dailyCashReconciliation);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DailyCashDetail)) {
            return false;
        }
        return getId() != null && getId().equals(((DailyCashDetail) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DailyCashDetail{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", referenceId=" + getReferenceId() +
            ", referenceType='" + getReferenceType() + "'" +
            ", amount=" + getAmount() +
            ", description='" + getDescription() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", active='" + getActive() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
