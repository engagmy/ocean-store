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
 * A PurchaseOperation.
 */
@Entity
@Table(name = "purchase_operation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PurchaseOperation extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private Instant date;

    @Column(name = "supplier_invoice_no")
    private String supplierInvoiceNo;

    @NotNull
    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    @NotNull
    @Column(name = "total_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @NotNull
    @Column(name = "grand_total", precision = 21, scale = 2, nullable = false)
    private BigDecimal grandTotal;

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
    @JsonIgnoreProperties(value = { "saleOperations", "purchaseOperations" }, allowSetters = true)
    private Bill bill;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "purchaseOperation")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "purchaseOperation", "product", "supplier", "purchasePayments" }, allowSetters = true)
    private Set<Purchase> purchases = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PurchaseOperation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate() {
        return this.date;
    }

    public PurchaseOperation date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getSupplierInvoiceNo() {
        return this.supplierInvoiceNo;
    }

    public PurchaseOperation supplierInvoiceNo(String supplierInvoiceNo) {
        this.setSupplierInvoiceNo(supplierInvoiceNo);
        return this;
    }

    public void setSupplierInvoiceNo(String supplierInvoiceNo) {
        this.supplierInvoiceNo = supplierInvoiceNo;
    }

    public Integer getTotalQuantity() {
        return this.totalQuantity;
    }

    public PurchaseOperation totalQuantity(Integer totalQuantity) {
        this.setTotalQuantity(totalQuantity);
        return this;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public PurchaseOperation totalAmount(BigDecimal totalAmount) {
        this.setTotalAmount(totalAmount);
        return this;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getGrandTotal() {
        return this.grandTotal;
    }

    public PurchaseOperation grandTotal(BigDecimal grandTotal) {
        this.setGrandTotal(grandTotal);
        return this;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public Boolean getActive() {
        return this.active;
    }

    public PurchaseOperation active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    // Inherited createdBy methods
    public PurchaseOperation createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public PurchaseOperation createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public PurchaseOperation lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public PurchaseOperation lastModifiedDate(Instant lastModifiedDate) {
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

    public PurchaseOperation setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Bill getBill() {
        return this.bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public PurchaseOperation bill(Bill bill) {
        this.setBill(bill);
        return this;
    }

    public Set<Purchase> getPurchases() {
        return this.purchases;
    }

    public void setPurchases(Set<Purchase> purchases) {
        if (this.purchases != null) {
            this.purchases.forEach(i -> i.setPurchaseOperation(null));
        }
        if (purchases != null) {
            purchases.forEach(i -> i.setPurchaseOperation(this));
        }
        this.purchases = purchases;
    }

    public PurchaseOperation purchases(Set<Purchase> purchases) {
        this.setPurchases(purchases);
        return this;
    }

    public PurchaseOperation addPurchase(Purchase purchase) {
        this.purchases.add(purchase);
        purchase.setPurchaseOperation(this);
        return this;
    }

    public PurchaseOperation removePurchase(Purchase purchase) {
        this.purchases.remove(purchase);
        purchase.setPurchaseOperation(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PurchaseOperation)) {
            return false;
        }
        return getId() != null && getId().equals(((PurchaseOperation) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PurchaseOperation{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", supplierInvoiceNo='" + getSupplierInvoiceNo() + "'" +
            ", totalQuantity=" + getTotalQuantity() +
            ", totalAmount=" + getTotalAmount() +
            ", grandTotal=" + getGrandTotal() +
            ", active='" + getActive() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
