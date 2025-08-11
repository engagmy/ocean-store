package com.agamy.inventory.service.dto;

import com.agamy.inventory.domain.enumeration.CashTransactionType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.agamy.inventory.domain.DailyCashDetail} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DailyCashDetailDTO implements Serializable {

    private Long id;

    @NotNull
    private CashTransactionType type;

    @NotNull
    private Long referenceId;

    @NotNull
    private String referenceType;

    @NotNull
    private BigDecimal amount;

    private String description;

    @NotNull
    private Instant timestamp;

    private Boolean active;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private DailyCashReconciliationDTO dailyCashReconciliation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CashTransactionType getType() {
        return type;
    }

    public void setType(CashTransactionType type) {
        this.type = type;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
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

    public DailyCashReconciliationDTO getDailyCashReconciliation() {
        return dailyCashReconciliation;
    }

    public void setDailyCashReconciliation(DailyCashReconciliationDTO dailyCashReconciliation) {
        this.dailyCashReconciliation = dailyCashReconciliation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DailyCashDetailDTO)) {
            return false;
        }

        DailyCashDetailDTO dailyCashDetailDTO = (DailyCashDetailDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, dailyCashDetailDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DailyCashDetailDTO{" +
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
            ", dailyCashReconciliation=" + getDailyCashReconciliation() +
            "}";
    }
}
