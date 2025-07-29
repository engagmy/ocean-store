package com.agamy.inventory.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A Bill.
 */
@Entity
@Table(name = "bill")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Bill extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "bill_number", nullable = false)
    private String billNumber;

    @NotNull
    @Column(name = "date", nullable = false)
    private Instant date;

    @NotNull
    @Column(name = "total_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "tax_amount", precision = 21, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "discount_amount", precision = 21, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "paid_amount", precision = 21, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "due_amount", precision = 21, scale = 2)
    private BigDecimal dueAmount;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bill")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "bill", "customer", "sales" }, allowSetters = true)
    private Set<SaleOperation> saleOperations = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bill")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "bill", "purchases" }, allowSetters = true)
    private Set<PurchaseOperation> purchaseOperations = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Bill id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBillNumber() {
        return this.billNumber;
    }

    public Bill billNumber(String billNumber) {
        this.setBillNumber(billNumber);
        return this;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public Instant getDate() {
        return this.date;
    }

    public Bill date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public Bill totalAmount(BigDecimal totalAmount) {
        this.setTotalAmount(totalAmount);
        return this;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTaxAmount() {
        return this.taxAmount;
    }

    public Bill taxAmount(BigDecimal taxAmount) {
        this.setTaxAmount(taxAmount);
        return this;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getDiscountAmount() {
        return this.discountAmount;
    }

    public Bill discountAmount(BigDecimal discountAmount) {
        this.setDiscountAmount(discountAmount);
        return this;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getPaidAmount() {
        return this.paidAmount;
    }

    public Bill paidAmount(BigDecimal paidAmount) {
        this.setPaidAmount(paidAmount);
        return this;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getDueAmount() {
        return this.dueAmount;
    }

    public Bill dueAmount(BigDecimal dueAmount) {
        this.setDueAmount(dueAmount);
        return this;
    }

    public void setDueAmount(BigDecimal dueAmount) {
        this.dueAmount = dueAmount;
    }

    public String getNotes() {
        return this.notes;
    }

    public Bill notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Bill active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    // Inherited createdBy methods
    public Bill createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public Bill createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public Bill lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public Bill lastModifiedDate(Instant lastModifiedDate) {
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

    public Bill setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Set<SaleOperation> getSaleOperations() {
        return this.saleOperations;
    }

    public void setSaleOperations(Set<SaleOperation> saleOperations) {
        if (this.saleOperations != null) {
            this.saleOperations.forEach(i -> i.setBill(null));
        }
        if (saleOperations != null) {
            saleOperations.forEach(i -> i.setBill(this));
        }
        this.saleOperations = saleOperations;
    }

    public Bill saleOperations(Set<SaleOperation> saleOperations) {
        this.setSaleOperations(saleOperations);
        return this;
    }

    public Bill addSaleOperation(SaleOperation saleOperation) {
        this.saleOperations.add(saleOperation);
        saleOperation.setBill(this);
        return this;
    }

    public Bill removeSaleOperation(SaleOperation saleOperation) {
        this.saleOperations.remove(saleOperation);
        saleOperation.setBill(null);
        return this;
    }

    public Set<PurchaseOperation> getPurchaseOperations() {
        return this.purchaseOperations;
    }

    public void setPurchaseOperations(Set<PurchaseOperation> purchaseOperations) {
        if (this.purchaseOperations != null) {
            this.purchaseOperations.forEach(i -> i.setBill(null));
        }
        if (purchaseOperations != null) {
            purchaseOperations.forEach(i -> i.setBill(this));
        }
        this.purchaseOperations = purchaseOperations;
    }

    public Bill purchaseOperations(Set<PurchaseOperation> purchaseOperations) {
        this.setPurchaseOperations(purchaseOperations);
        return this;
    }

    public Bill addPurchaseOperation(PurchaseOperation purchaseOperation) {
        this.purchaseOperations.add(purchaseOperation);
        purchaseOperation.setBill(this);
        return this;
    }

    public Bill removePurchaseOperation(PurchaseOperation purchaseOperation) {
        this.purchaseOperations.remove(purchaseOperation);
        purchaseOperation.setBill(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Bill)) {
            return false;
        }
        return getId() != null && getId().equals(((Bill) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Bill{" +
            "id=" + getId() +
            ", billNumber='" + getBillNumber() + "'" +
            ", date='" + getDate() + "'" +
            ", totalAmount=" + getTotalAmount() +
            ", taxAmount=" + getTaxAmount() +
            ", discountAmount=" + getDiscountAmount() +
            ", paidAmount=" + getPaidAmount() +
            ", dueAmount=" + getDueAmount() +
            ", notes='" + getNotes() + "'" +
            ", active='" + getActive() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
