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
 * A Purchase.
 */
@Entity
@Table(name = "purchase")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Purchase extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "product_name")
    private String productName;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "unit_price", precision = 21, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @NotNull
    @Column(name = "line_total", precision = 21, scale = 2, nullable = false)
    private BigDecimal lineTotal;

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
    @JsonIgnoreProperties(value = { "bill", "purchases" }, allowSetters = true)
    private PurchaseOperation purchaseOperation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "brand", "productCategory", "inventoryTransactions", "sales", "purchases" }, allowSetters = true)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "purchases" }, allowSetters = true)
    private Supplier supplier;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "purchase")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "purchase" }, allowSetters = true)
    private Set<PurchasePayment> purchasePayments = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Purchase id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return this.productName;
    }

    public Purchase productName(String productName) {
        this.setProductName(productName);
        return this;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public Purchase quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }

    public Purchase unitPrice(BigDecimal unitPrice) {
        this.setUnitPrice(unitPrice);
        return this;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getLineTotal() {
        return this.lineTotal;
    }

    public Purchase lineTotal(BigDecimal lineTotal) {
        this.setLineTotal(lineTotal);
        return this;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Purchase active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    // Inherited createdBy methods
    public Purchase createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public Purchase createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public Purchase lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public Purchase lastModifiedDate(Instant lastModifiedDate) {
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

    public Purchase setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public PurchaseOperation getPurchaseOperation() {
        return this.purchaseOperation;
    }

    public void setPurchaseOperation(PurchaseOperation purchaseOperation) {
        this.purchaseOperation = purchaseOperation;
    }

    public Purchase purchaseOperation(PurchaseOperation purchaseOperation) {
        this.setPurchaseOperation(purchaseOperation);
        return this;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Purchase product(Product product) {
        this.setProduct(product);
        return this;
    }

    public Supplier getSupplier() {
        return this.supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Purchase supplier(Supplier supplier) {
        this.setSupplier(supplier);
        return this;
    }

    public Set<PurchasePayment> getPurchasePayments() {
        return this.purchasePayments;
    }

    public void setPurchasePayments(Set<PurchasePayment> purchasePayments) {
        if (this.purchasePayments != null) {
            this.purchasePayments.forEach(i -> i.setPurchase(null));
        }
        if (purchasePayments != null) {
            purchasePayments.forEach(i -> i.setPurchase(this));
        }
        this.purchasePayments = purchasePayments;
    }

    public Purchase purchasePayments(Set<PurchasePayment> purchasePayments) {
        this.setPurchasePayments(purchasePayments);
        return this;
    }

    public Purchase addPurchasePayment(PurchasePayment purchasePayment) {
        this.purchasePayments.add(purchasePayment);
        purchasePayment.setPurchase(this);
        return this;
    }

    public Purchase removePurchasePayment(PurchasePayment purchasePayment) {
        this.purchasePayments.remove(purchasePayment);
        purchasePayment.setPurchase(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Purchase)) {
            return false;
        }
        return getId() != null && getId().equals(((Purchase) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Purchase{" +
            "id=" + getId() +
            ", productName='" + getProductName() + "'" +
            ", quantity=" + getQuantity() +
            ", unitPrice=" + getUnitPrice() +
            ", lineTotal=" + getLineTotal() +
            ", active='" + getActive() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
