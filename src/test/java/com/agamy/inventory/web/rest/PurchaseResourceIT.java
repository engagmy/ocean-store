package com.agamy.inventory.web.rest;

import static com.agamy.inventory.domain.PurchaseAsserts.*;
import static com.agamy.inventory.web.rest.TestUtil.createUpdateProxyForBean;
import static com.agamy.inventory.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.agamy.inventory.IntegrationTest;
import com.agamy.inventory.domain.Product;
import com.agamy.inventory.domain.Purchase;
import com.agamy.inventory.domain.PurchaseOperation;
import com.agamy.inventory.domain.Supplier;
import com.agamy.inventory.repository.PurchaseRepository;
import com.agamy.inventory.service.dto.PurchaseDTO;
import com.agamy.inventory.service.mapper.PurchaseMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PurchaseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PurchaseResourceIT {

    private static final String DEFAULT_PRODUCT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PRODUCT_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_UNIT_PRICE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_LINE_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_LINE_TOTAL = new BigDecimal(2);
    private static final BigDecimal SMALLER_LINE_TOTAL = new BigDecimal(1 - 1);

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/purchases";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private PurchaseMapper purchaseMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPurchaseMockMvc;

    private Purchase purchase;

    private Purchase insertedPurchase;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Purchase createEntity() {
        return new Purchase()
            .productName(DEFAULT_PRODUCT_NAME)
            .quantity(DEFAULT_QUANTITY)
            .unitPrice(DEFAULT_UNIT_PRICE)
            .lineTotal(DEFAULT_LINE_TOTAL)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Purchase createUpdatedEntity() {
        return new Purchase()
            .productName(UPDATED_PRODUCT_NAME)
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .lineTotal(UPDATED_LINE_TOTAL)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        purchase = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPurchase != null) {
            purchaseRepository.delete(insertedPurchase);
            insertedPurchase = null;
        }
    }

    @Test
    @Transactional
    void createPurchase() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Purchase
        PurchaseDTO purchaseDTO = purchaseMapper.toDto(purchase);
        var returnedPurchaseDTO = om.readValue(
            restPurchaseMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PurchaseDTO.class
        );

        // Validate the Purchase in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPurchase = purchaseMapper.toEntity(returnedPurchaseDTO);
        assertPurchaseUpdatableFieldsEquals(returnedPurchase, getPersistedPurchase(returnedPurchase));

        insertedPurchase = returnedPurchase;
    }

    @Test
    @Transactional
    void createPurchaseWithExistingId() throws Exception {
        // Create the Purchase with an existing ID
        purchase.setId(1L);
        PurchaseDTO purchaseDTO = purchaseMapper.toDto(purchase);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPurchaseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Purchase in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        purchase.setQuantity(null);

        // Create the Purchase, which fails.
        PurchaseDTO purchaseDTO = purchaseMapper.toDto(purchase);

        restPurchaseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUnitPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        purchase.setUnitPrice(null);

        // Create the Purchase, which fails.
        PurchaseDTO purchaseDTO = purchaseMapper.toDto(purchase);

        restPurchaseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLineTotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        purchase.setLineTotal(null);

        // Create the Purchase, which fails.
        PurchaseDTO purchaseDTO = purchaseMapper.toDto(purchase);

        restPurchaseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPurchases() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList
        restPurchaseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(purchase.getId().intValue())))
            .andExpect(jsonPath("$.[*].productName").value(hasItem(DEFAULT_PRODUCT_NAME)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].lineTotal").value(hasItem(sameNumber(DEFAULT_LINE_TOTAL))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getPurchase() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get the purchase
        restPurchaseMockMvc
            .perform(get(ENTITY_API_URL_ID, purchase.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(purchase.getId().intValue()))
            .andExpect(jsonPath("$.productName").value(DEFAULT_PRODUCT_NAME))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.unitPrice").value(sameNumber(DEFAULT_UNIT_PRICE)))
            .andExpect(jsonPath("$.lineTotal").value(sameNumber(DEFAULT_LINE_TOTAL)))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getPurchasesByIdFiltering() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        Long id = purchase.getId();

        defaultPurchaseFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPurchaseFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPurchaseFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPurchasesByProductNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where productName equals to
        defaultPurchaseFiltering("productName.equals=" + DEFAULT_PRODUCT_NAME, "productName.equals=" + UPDATED_PRODUCT_NAME);
    }

    @Test
    @Transactional
    void getAllPurchasesByProductNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where productName in
        defaultPurchaseFiltering(
            "productName.in=" + DEFAULT_PRODUCT_NAME + "," + UPDATED_PRODUCT_NAME,
            "productName.in=" + UPDATED_PRODUCT_NAME
        );
    }

    @Test
    @Transactional
    void getAllPurchasesByProductNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where productName is not null
        defaultPurchaseFiltering("productName.specified=true", "productName.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchasesByProductNameContainsSomething() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where productName contains
        defaultPurchaseFiltering("productName.contains=" + DEFAULT_PRODUCT_NAME, "productName.contains=" + UPDATED_PRODUCT_NAME);
    }

    @Test
    @Transactional
    void getAllPurchasesByProductNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where productName does not contain
        defaultPurchaseFiltering(
            "productName.doesNotContain=" + UPDATED_PRODUCT_NAME,
            "productName.doesNotContain=" + DEFAULT_PRODUCT_NAME
        );
    }

    @Test
    @Transactional
    void getAllPurchasesByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where quantity equals to
        defaultPurchaseFiltering("quantity.equals=" + DEFAULT_QUANTITY, "quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllPurchasesByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where quantity in
        defaultPurchaseFiltering("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY, "quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllPurchasesByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where quantity is not null
        defaultPurchaseFiltering("quantity.specified=true", "quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchasesByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where quantity is greater than or equal to
        defaultPurchaseFiltering("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY, "quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllPurchasesByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where quantity is less than or equal to
        defaultPurchaseFiltering("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY, "quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllPurchasesByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where quantity is less than
        defaultPurchaseFiltering("quantity.lessThan=" + UPDATED_QUANTITY, "quantity.lessThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllPurchasesByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where quantity is greater than
        defaultPurchaseFiltering("quantity.greaterThan=" + SMALLER_QUANTITY, "quantity.greaterThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllPurchasesByUnitPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where unitPrice equals to
        defaultPurchaseFiltering("unitPrice.equals=" + DEFAULT_UNIT_PRICE, "unitPrice.equals=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllPurchasesByUnitPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where unitPrice in
        defaultPurchaseFiltering("unitPrice.in=" + DEFAULT_UNIT_PRICE + "," + UPDATED_UNIT_PRICE, "unitPrice.in=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllPurchasesByUnitPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where unitPrice is not null
        defaultPurchaseFiltering("unitPrice.specified=true", "unitPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchasesByUnitPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where unitPrice is greater than or equal to
        defaultPurchaseFiltering(
            "unitPrice.greaterThanOrEqual=" + DEFAULT_UNIT_PRICE,
            "unitPrice.greaterThanOrEqual=" + UPDATED_UNIT_PRICE
        );
    }

    @Test
    @Transactional
    void getAllPurchasesByUnitPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where unitPrice is less than or equal to
        defaultPurchaseFiltering("unitPrice.lessThanOrEqual=" + DEFAULT_UNIT_PRICE, "unitPrice.lessThanOrEqual=" + SMALLER_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllPurchasesByUnitPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where unitPrice is less than
        defaultPurchaseFiltering("unitPrice.lessThan=" + UPDATED_UNIT_PRICE, "unitPrice.lessThan=" + DEFAULT_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllPurchasesByUnitPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where unitPrice is greater than
        defaultPurchaseFiltering("unitPrice.greaterThan=" + SMALLER_UNIT_PRICE, "unitPrice.greaterThan=" + DEFAULT_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllPurchasesByLineTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where lineTotal equals to
        defaultPurchaseFiltering("lineTotal.equals=" + DEFAULT_LINE_TOTAL, "lineTotal.equals=" + UPDATED_LINE_TOTAL);
    }

    @Test
    @Transactional
    void getAllPurchasesByLineTotalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where lineTotal in
        defaultPurchaseFiltering("lineTotal.in=" + DEFAULT_LINE_TOTAL + "," + UPDATED_LINE_TOTAL, "lineTotal.in=" + UPDATED_LINE_TOTAL);
    }

    @Test
    @Transactional
    void getAllPurchasesByLineTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where lineTotal is not null
        defaultPurchaseFiltering("lineTotal.specified=true", "lineTotal.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchasesByLineTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where lineTotal is greater than or equal to
        defaultPurchaseFiltering(
            "lineTotal.greaterThanOrEqual=" + DEFAULT_LINE_TOTAL,
            "lineTotal.greaterThanOrEqual=" + UPDATED_LINE_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllPurchasesByLineTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where lineTotal is less than or equal to
        defaultPurchaseFiltering("lineTotal.lessThanOrEqual=" + DEFAULT_LINE_TOTAL, "lineTotal.lessThanOrEqual=" + SMALLER_LINE_TOTAL);
    }

    @Test
    @Transactional
    void getAllPurchasesByLineTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where lineTotal is less than
        defaultPurchaseFiltering("lineTotal.lessThan=" + UPDATED_LINE_TOTAL, "lineTotal.lessThan=" + DEFAULT_LINE_TOTAL);
    }

    @Test
    @Transactional
    void getAllPurchasesByLineTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where lineTotal is greater than
        defaultPurchaseFiltering("lineTotal.greaterThan=" + SMALLER_LINE_TOTAL, "lineTotal.greaterThan=" + DEFAULT_LINE_TOTAL);
    }

    @Test
    @Transactional
    void getAllPurchasesByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where active equals to
        defaultPurchaseFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPurchasesByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where active in
        defaultPurchaseFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPurchasesByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList where active is not null
        defaultPurchaseFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchasesByPurchaseOperationIsEqualToSomething() throws Exception {
        PurchaseOperation purchaseOperation;
        if (TestUtil.findAll(em, PurchaseOperation.class).isEmpty()) {
            purchaseRepository.saveAndFlush(purchase);
            purchaseOperation = PurchaseOperationResourceIT.createEntity();
        } else {
            purchaseOperation = TestUtil.findAll(em, PurchaseOperation.class).get(0);
        }
        em.persist(purchaseOperation);
        em.flush();
        purchase.setPurchaseOperation(purchaseOperation);
        purchaseRepository.saveAndFlush(purchase);
        Long purchaseOperationId = purchaseOperation.getId();
        // Get all the purchaseList where purchaseOperation equals to purchaseOperationId
        defaultPurchaseShouldBeFound("purchaseOperationId.equals=" + purchaseOperationId);

        // Get all the purchaseList where purchaseOperation equals to (purchaseOperationId + 1)
        defaultPurchaseShouldNotBeFound("purchaseOperationId.equals=" + (purchaseOperationId + 1));
    }

    @Test
    @Transactional
    void getAllPurchasesByProductIsEqualToSomething() throws Exception {
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            purchaseRepository.saveAndFlush(purchase);
            product = ProductResourceIT.createEntity();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        em.persist(product);
        em.flush();
        purchase.setProduct(product);
        purchaseRepository.saveAndFlush(purchase);
        Long productId = product.getId();
        // Get all the purchaseList where product equals to productId
        defaultPurchaseShouldBeFound("productId.equals=" + productId);

        // Get all the purchaseList where product equals to (productId + 1)
        defaultPurchaseShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    @Test
    @Transactional
    void getAllPurchasesBySupplierIsEqualToSomething() throws Exception {
        Supplier supplier;
        if (TestUtil.findAll(em, Supplier.class).isEmpty()) {
            purchaseRepository.saveAndFlush(purchase);
            supplier = SupplierResourceIT.createEntity();
        } else {
            supplier = TestUtil.findAll(em, Supplier.class).get(0);
        }
        em.persist(supplier);
        em.flush();
        purchase.setSupplier(supplier);
        purchaseRepository.saveAndFlush(purchase);
        Long supplierId = supplier.getId();
        // Get all the purchaseList where supplier equals to supplierId
        defaultPurchaseShouldBeFound("supplierId.equals=" + supplierId);

        // Get all the purchaseList where supplier equals to (supplierId + 1)
        defaultPurchaseShouldNotBeFound("supplierId.equals=" + (supplierId + 1));
    }

    private void defaultPurchaseFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPurchaseShouldBeFound(shouldBeFound);
        defaultPurchaseShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPurchaseShouldBeFound(String filter) throws Exception {
        restPurchaseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(purchase.getId().intValue())))
            .andExpect(jsonPath("$.[*].productName").value(hasItem(DEFAULT_PRODUCT_NAME)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].lineTotal").value(hasItem(sameNumber(DEFAULT_LINE_TOTAL))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));

        // Check, that the count call also returns 1
        restPurchaseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPurchaseShouldNotBeFound(String filter) throws Exception {
        restPurchaseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPurchaseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPurchase() throws Exception {
        // Get the purchase
        restPurchaseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPurchase() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the purchase
        Purchase updatedPurchase = purchaseRepository.findById(purchase.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPurchase are not directly saved in db
        em.detach(updatedPurchase);
        updatedPurchase
            .productName(UPDATED_PRODUCT_NAME)
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .lineTotal(UPDATED_LINE_TOTAL)
            .active(UPDATED_ACTIVE);
        PurchaseDTO purchaseDTO = purchaseMapper.toDto(updatedPurchase);

        restPurchaseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchaseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(purchaseDTO))
            )
            .andExpect(status().isOk());

        // Validate the Purchase in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPurchaseToMatchAllProperties(updatedPurchase);
    }

    @Test
    @Transactional
    void putNonExistingPurchase() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchase.setId(longCount.incrementAndGet());

        // Create the Purchase
        PurchaseDTO purchaseDTO = purchaseMapper.toDto(purchase);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchaseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchaseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(purchaseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Purchase in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPurchase() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchase.setId(longCount.incrementAndGet());

        // Create the Purchase
        PurchaseDTO purchaseDTO = purchaseMapper.toDto(purchase);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(purchaseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Purchase in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPurchase() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchase.setId(longCount.incrementAndGet());

        // Create the Purchase
        PurchaseDTO purchaseDTO = purchaseMapper.toDto(purchase);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Purchase in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePurchaseWithPatch() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the purchase using partial update
        Purchase partialUpdatedPurchase = new Purchase();
        partialUpdatedPurchase.setId(purchase.getId());

        partialUpdatedPurchase.quantity(UPDATED_QUANTITY).lineTotal(UPDATED_LINE_TOTAL).active(UPDATED_ACTIVE);

        restPurchaseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchase.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPurchase))
            )
            .andExpect(status().isOk());

        // Validate the Purchase in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPurchaseUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPurchase, purchase), getPersistedPurchase(purchase));
    }

    @Test
    @Transactional
    void fullUpdatePurchaseWithPatch() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the purchase using partial update
        Purchase partialUpdatedPurchase = new Purchase();
        partialUpdatedPurchase.setId(purchase.getId());

        partialUpdatedPurchase
            .productName(UPDATED_PRODUCT_NAME)
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .lineTotal(UPDATED_LINE_TOTAL)
            .active(UPDATED_ACTIVE);

        restPurchaseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchase.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPurchase))
            )
            .andExpect(status().isOk());

        // Validate the Purchase in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPurchaseUpdatableFieldsEquals(partialUpdatedPurchase, getPersistedPurchase(partialUpdatedPurchase));
    }

    @Test
    @Transactional
    void patchNonExistingPurchase() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchase.setId(longCount.incrementAndGet());

        // Create the Purchase
        PurchaseDTO purchaseDTO = purchaseMapper.toDto(purchase);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchaseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, purchaseDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(purchaseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Purchase in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPurchase() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchase.setId(longCount.incrementAndGet());

        // Create the Purchase
        PurchaseDTO purchaseDTO = purchaseMapper.toDto(purchase);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(purchaseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Purchase in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPurchase() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchase.setId(longCount.incrementAndGet());

        // Create the Purchase
        PurchaseDTO purchaseDTO = purchaseMapper.toDto(purchase);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(purchaseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Purchase in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePurchase() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the purchase
        restPurchaseMockMvc
            .perform(delete(ENTITY_API_URL_ID, purchase.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return purchaseRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Purchase getPersistedPurchase(Purchase purchase) {
        return purchaseRepository.findById(purchase.getId()).orElseThrow();
    }

    protected void assertPersistedPurchaseToMatchAllProperties(Purchase expectedPurchase) {
        assertPurchaseAllPropertiesEquals(expectedPurchase, getPersistedPurchase(expectedPurchase));
    }

    protected void assertPersistedPurchaseToMatchUpdatableProperties(Purchase expectedPurchase) {
        assertPurchaseAllUpdatablePropertiesEquals(expectedPurchase, getPersistedPurchase(expectedPurchase));
    }
}
