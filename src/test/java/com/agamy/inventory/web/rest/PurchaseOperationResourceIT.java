package com.agamy.inventory.web.rest;

import static com.agamy.inventory.domain.PurchaseOperationAsserts.*;
import static com.agamy.inventory.web.rest.TestUtil.createUpdateProxyForBean;
import static com.agamy.inventory.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.agamy.inventory.IntegrationTest;
import com.agamy.inventory.domain.Bill;
import com.agamy.inventory.domain.PurchaseOperation;
import com.agamy.inventory.repository.PurchaseOperationRepository;
import com.agamy.inventory.service.dto.PurchaseOperationDTO;
import com.agamy.inventory.service.mapper.PurchaseOperationMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link PurchaseOperationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PurchaseOperationResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_SUPPLIER_INVOICE_NO = "AAAAAAAAAA";
    private static final String UPDATED_SUPPLIER_INVOICE_NO = "BBBBBBBBBB";

    private static final Integer DEFAULT_TOTAL_QUANTITY = 1;
    private static final Integer UPDATED_TOTAL_QUANTITY = 2;
    private static final Integer SMALLER_TOTAL_QUANTITY = 1 - 1;

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_TOTAL_AMOUNT = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_GRAND_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_GRAND_TOTAL = new BigDecimal(2);
    private static final BigDecimal SMALLER_GRAND_TOTAL = new BigDecimal(1 - 1);

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/purchase-operations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PurchaseOperationRepository purchaseOperationRepository;

    @Autowired
    private PurchaseOperationMapper purchaseOperationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPurchaseOperationMockMvc;

    private PurchaseOperation purchaseOperation;

    private PurchaseOperation insertedPurchaseOperation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PurchaseOperation createEntity() {
        return new PurchaseOperation()
            .date(DEFAULT_DATE)
            .supplierInvoiceNo(DEFAULT_SUPPLIER_INVOICE_NO)
            .totalQuantity(DEFAULT_TOTAL_QUANTITY)
            .totalAmount(DEFAULT_TOTAL_AMOUNT)
            .grandTotal(DEFAULT_GRAND_TOTAL)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PurchaseOperation createUpdatedEntity() {
        return new PurchaseOperation()
            .date(UPDATED_DATE)
            .supplierInvoiceNo(UPDATED_SUPPLIER_INVOICE_NO)
            .totalQuantity(UPDATED_TOTAL_QUANTITY)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .grandTotal(UPDATED_GRAND_TOTAL)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        purchaseOperation = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPurchaseOperation != null) {
            purchaseOperationRepository.delete(insertedPurchaseOperation);
            insertedPurchaseOperation = null;
        }
    }

    @Test
    @Transactional
    void createPurchaseOperation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PurchaseOperation
        PurchaseOperationDTO purchaseOperationDTO = purchaseOperationMapper.toDto(purchaseOperation);
        var returnedPurchaseOperationDTO = om.readValue(
            restPurchaseOperationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseOperationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PurchaseOperationDTO.class
        );

        // Validate the PurchaseOperation in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPurchaseOperation = purchaseOperationMapper.toEntity(returnedPurchaseOperationDTO);
        assertPurchaseOperationUpdatableFieldsEquals(returnedPurchaseOperation, getPersistedPurchaseOperation(returnedPurchaseOperation));

        insertedPurchaseOperation = returnedPurchaseOperation;
    }

    @Test
    @Transactional
    void createPurchaseOperationWithExistingId() throws Exception {
        // Create the PurchaseOperation with an existing ID
        purchaseOperation.setId(1L);
        PurchaseOperationDTO purchaseOperationDTO = purchaseOperationMapper.toDto(purchaseOperation);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPurchaseOperationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseOperationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PurchaseOperation in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        purchaseOperation.setDate(null);

        // Create the PurchaseOperation, which fails.
        PurchaseOperationDTO purchaseOperationDTO = purchaseOperationMapper.toDto(purchaseOperation);

        restPurchaseOperationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseOperationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        purchaseOperation.setTotalQuantity(null);

        // Create the PurchaseOperation, which fails.
        PurchaseOperationDTO purchaseOperationDTO = purchaseOperationMapper.toDto(purchaseOperation);

        restPurchaseOperationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseOperationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        purchaseOperation.setTotalAmount(null);

        // Create the PurchaseOperation, which fails.
        PurchaseOperationDTO purchaseOperationDTO = purchaseOperationMapper.toDto(purchaseOperation);

        restPurchaseOperationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseOperationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGrandTotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        purchaseOperation.setGrandTotal(null);

        // Create the PurchaseOperation, which fails.
        PurchaseOperationDTO purchaseOperationDTO = purchaseOperationMapper.toDto(purchaseOperation);

        restPurchaseOperationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseOperationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPurchaseOperations() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList
        restPurchaseOperationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(purchaseOperation.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].supplierInvoiceNo").value(hasItem(DEFAULT_SUPPLIER_INVOICE_NO)))
            .andExpect(jsonPath("$.[*].totalQuantity").value(hasItem(DEFAULT_TOTAL_QUANTITY)))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].grandTotal").value(hasItem(sameNumber(DEFAULT_GRAND_TOTAL))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getPurchaseOperation() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get the purchaseOperation
        restPurchaseOperationMockMvc
            .perform(get(ENTITY_API_URL_ID, purchaseOperation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(purchaseOperation.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.supplierInvoiceNo").value(DEFAULT_SUPPLIER_INVOICE_NO))
            .andExpect(jsonPath("$.totalQuantity").value(DEFAULT_TOTAL_QUANTITY))
            .andExpect(jsonPath("$.totalAmount").value(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .andExpect(jsonPath("$.grandTotal").value(sameNumber(DEFAULT_GRAND_TOTAL)))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getPurchaseOperationsByIdFiltering() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        Long id = purchaseOperation.getId();

        defaultPurchaseOperationFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPurchaseOperationFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPurchaseOperationFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where date equals to
        defaultPurchaseOperationFiltering("date.equals=" + DEFAULT_DATE, "date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where date in
        defaultPurchaseOperationFiltering("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE, "date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where date is not null
        defaultPurchaseOperationFiltering("date.specified=true", "date.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsBySupplierInvoiceNoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where supplierInvoiceNo equals to
        defaultPurchaseOperationFiltering(
            "supplierInvoiceNo.equals=" + DEFAULT_SUPPLIER_INVOICE_NO,
            "supplierInvoiceNo.equals=" + UPDATED_SUPPLIER_INVOICE_NO
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsBySupplierInvoiceNoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where supplierInvoiceNo in
        defaultPurchaseOperationFiltering(
            "supplierInvoiceNo.in=" + DEFAULT_SUPPLIER_INVOICE_NO + "," + UPDATED_SUPPLIER_INVOICE_NO,
            "supplierInvoiceNo.in=" + UPDATED_SUPPLIER_INVOICE_NO
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsBySupplierInvoiceNoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where supplierInvoiceNo is not null
        defaultPurchaseOperationFiltering("supplierInvoiceNo.specified=true", "supplierInvoiceNo.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsBySupplierInvoiceNoContainsSomething() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where supplierInvoiceNo contains
        defaultPurchaseOperationFiltering(
            "supplierInvoiceNo.contains=" + DEFAULT_SUPPLIER_INVOICE_NO,
            "supplierInvoiceNo.contains=" + UPDATED_SUPPLIER_INVOICE_NO
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsBySupplierInvoiceNoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where supplierInvoiceNo does not contain
        defaultPurchaseOperationFiltering(
            "supplierInvoiceNo.doesNotContain=" + UPDATED_SUPPLIER_INVOICE_NO,
            "supplierInvoiceNo.doesNotContain=" + DEFAULT_SUPPLIER_INVOICE_NO
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByTotalQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where totalQuantity equals to
        defaultPurchaseOperationFiltering(
            "totalQuantity.equals=" + DEFAULT_TOTAL_QUANTITY,
            "totalQuantity.equals=" + UPDATED_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByTotalQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where totalQuantity in
        defaultPurchaseOperationFiltering(
            "totalQuantity.in=" + DEFAULT_TOTAL_QUANTITY + "," + UPDATED_TOTAL_QUANTITY,
            "totalQuantity.in=" + UPDATED_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByTotalQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where totalQuantity is not null
        defaultPurchaseOperationFiltering("totalQuantity.specified=true", "totalQuantity.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByTotalQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where totalQuantity is greater than or equal to
        defaultPurchaseOperationFiltering(
            "totalQuantity.greaterThanOrEqual=" + DEFAULT_TOTAL_QUANTITY,
            "totalQuantity.greaterThanOrEqual=" + UPDATED_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByTotalQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where totalQuantity is less than or equal to
        defaultPurchaseOperationFiltering(
            "totalQuantity.lessThanOrEqual=" + DEFAULT_TOTAL_QUANTITY,
            "totalQuantity.lessThanOrEqual=" + SMALLER_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByTotalQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where totalQuantity is less than
        defaultPurchaseOperationFiltering(
            "totalQuantity.lessThan=" + UPDATED_TOTAL_QUANTITY,
            "totalQuantity.lessThan=" + DEFAULT_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByTotalQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where totalQuantity is greater than
        defaultPurchaseOperationFiltering(
            "totalQuantity.greaterThan=" + SMALLER_TOTAL_QUANTITY,
            "totalQuantity.greaterThan=" + DEFAULT_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByTotalAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where totalAmount equals to
        defaultPurchaseOperationFiltering("totalAmount.equals=" + DEFAULT_TOTAL_AMOUNT, "totalAmount.equals=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByTotalAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where totalAmount in
        defaultPurchaseOperationFiltering(
            "totalAmount.in=" + DEFAULT_TOTAL_AMOUNT + "," + UPDATED_TOTAL_AMOUNT,
            "totalAmount.in=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByTotalAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where totalAmount is not null
        defaultPurchaseOperationFiltering("totalAmount.specified=true", "totalAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByTotalAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where totalAmount is greater than or equal to
        defaultPurchaseOperationFiltering(
            "totalAmount.greaterThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.greaterThanOrEqual=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByTotalAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where totalAmount is less than or equal to
        defaultPurchaseOperationFiltering(
            "totalAmount.lessThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.lessThanOrEqual=" + SMALLER_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByTotalAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where totalAmount is less than
        defaultPurchaseOperationFiltering("totalAmount.lessThan=" + UPDATED_TOTAL_AMOUNT, "totalAmount.lessThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByTotalAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where totalAmount is greater than
        defaultPurchaseOperationFiltering(
            "totalAmount.greaterThan=" + SMALLER_TOTAL_AMOUNT,
            "totalAmount.greaterThan=" + DEFAULT_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByGrandTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where grandTotal equals to
        defaultPurchaseOperationFiltering("grandTotal.equals=" + DEFAULT_GRAND_TOTAL, "grandTotal.equals=" + UPDATED_GRAND_TOTAL);
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByGrandTotalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where grandTotal in
        defaultPurchaseOperationFiltering(
            "grandTotal.in=" + DEFAULT_GRAND_TOTAL + "," + UPDATED_GRAND_TOTAL,
            "grandTotal.in=" + UPDATED_GRAND_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByGrandTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where grandTotal is not null
        defaultPurchaseOperationFiltering("grandTotal.specified=true", "grandTotal.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByGrandTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where grandTotal is greater than or equal to
        defaultPurchaseOperationFiltering(
            "grandTotal.greaterThanOrEqual=" + DEFAULT_GRAND_TOTAL,
            "grandTotal.greaterThanOrEqual=" + UPDATED_GRAND_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByGrandTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where grandTotal is less than or equal to
        defaultPurchaseOperationFiltering(
            "grandTotal.lessThanOrEqual=" + DEFAULT_GRAND_TOTAL,
            "grandTotal.lessThanOrEqual=" + SMALLER_GRAND_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByGrandTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where grandTotal is less than
        defaultPurchaseOperationFiltering("grandTotal.lessThan=" + UPDATED_GRAND_TOTAL, "grandTotal.lessThan=" + DEFAULT_GRAND_TOTAL);
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByGrandTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where grandTotal is greater than
        defaultPurchaseOperationFiltering("grandTotal.greaterThan=" + SMALLER_GRAND_TOTAL, "grandTotal.greaterThan=" + DEFAULT_GRAND_TOTAL);
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where active equals to
        defaultPurchaseOperationFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where active in
        defaultPurchaseOperationFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        // Get all the purchaseOperationList where active is not null
        defaultPurchaseOperationFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOperationsByBillIsEqualToSomething() throws Exception {
        Bill bill;
        if (TestUtil.findAll(em, Bill.class).isEmpty()) {
            purchaseOperationRepository.saveAndFlush(purchaseOperation);
            bill = BillResourceIT.createEntity();
        } else {
            bill = TestUtil.findAll(em, Bill.class).get(0);
        }
        em.persist(bill);
        em.flush();
        purchaseOperation.setBill(bill);
        purchaseOperationRepository.saveAndFlush(purchaseOperation);
        Long billId = bill.getId();
        // Get all the purchaseOperationList where bill equals to billId
        defaultPurchaseOperationShouldBeFound("billId.equals=" + billId);

        // Get all the purchaseOperationList where bill equals to (billId + 1)
        defaultPurchaseOperationShouldNotBeFound("billId.equals=" + (billId + 1));
    }

    private void defaultPurchaseOperationFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPurchaseOperationShouldBeFound(shouldBeFound);
        defaultPurchaseOperationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPurchaseOperationShouldBeFound(String filter) throws Exception {
        restPurchaseOperationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(purchaseOperation.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].supplierInvoiceNo").value(hasItem(DEFAULT_SUPPLIER_INVOICE_NO)))
            .andExpect(jsonPath("$.[*].totalQuantity").value(hasItem(DEFAULT_TOTAL_QUANTITY)))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].grandTotal").value(hasItem(sameNumber(DEFAULT_GRAND_TOTAL))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));

        // Check, that the count call also returns 1
        restPurchaseOperationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPurchaseOperationShouldNotBeFound(String filter) throws Exception {
        restPurchaseOperationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPurchaseOperationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPurchaseOperation() throws Exception {
        // Get the purchaseOperation
        restPurchaseOperationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPurchaseOperation() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the purchaseOperation
        PurchaseOperation updatedPurchaseOperation = purchaseOperationRepository.findById(purchaseOperation.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPurchaseOperation are not directly saved in db
        em.detach(updatedPurchaseOperation);
        updatedPurchaseOperation
            .date(UPDATED_DATE)
            .supplierInvoiceNo(UPDATED_SUPPLIER_INVOICE_NO)
            .totalQuantity(UPDATED_TOTAL_QUANTITY)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .grandTotal(UPDATED_GRAND_TOTAL)
            .active(UPDATED_ACTIVE);
        PurchaseOperationDTO purchaseOperationDTO = purchaseOperationMapper.toDto(updatedPurchaseOperation);

        restPurchaseOperationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchaseOperationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(purchaseOperationDTO))
            )
            .andExpect(status().isOk());

        // Validate the PurchaseOperation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPurchaseOperationToMatchAllProperties(updatedPurchaseOperation);
    }

    @Test
    @Transactional
    void putNonExistingPurchaseOperation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchaseOperation.setId(longCount.incrementAndGet());

        // Create the PurchaseOperation
        PurchaseOperationDTO purchaseOperationDTO = purchaseOperationMapper.toDto(purchaseOperation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchaseOperationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchaseOperationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(purchaseOperationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseOperation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPurchaseOperation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchaseOperation.setId(longCount.incrementAndGet());

        // Create the PurchaseOperation
        PurchaseOperationDTO purchaseOperationDTO = purchaseOperationMapper.toDto(purchaseOperation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseOperationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(purchaseOperationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseOperation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPurchaseOperation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchaseOperation.setId(longCount.incrementAndGet());

        // Create the PurchaseOperation
        PurchaseOperationDTO purchaseOperationDTO = purchaseOperationMapper.toDto(purchaseOperation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseOperationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseOperationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PurchaseOperation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePurchaseOperationWithPatch() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the purchaseOperation using partial update
        PurchaseOperation partialUpdatedPurchaseOperation = new PurchaseOperation();
        partialUpdatedPurchaseOperation.setId(purchaseOperation.getId());

        partialUpdatedPurchaseOperation.grandTotal(UPDATED_GRAND_TOTAL).active(UPDATED_ACTIVE);

        restPurchaseOperationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchaseOperation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPurchaseOperation))
            )
            .andExpect(status().isOk());

        // Validate the PurchaseOperation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPurchaseOperationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPurchaseOperation, purchaseOperation),
            getPersistedPurchaseOperation(purchaseOperation)
        );
    }

    @Test
    @Transactional
    void fullUpdatePurchaseOperationWithPatch() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the purchaseOperation using partial update
        PurchaseOperation partialUpdatedPurchaseOperation = new PurchaseOperation();
        partialUpdatedPurchaseOperation.setId(purchaseOperation.getId());

        partialUpdatedPurchaseOperation
            .date(UPDATED_DATE)
            .supplierInvoiceNo(UPDATED_SUPPLIER_INVOICE_NO)
            .totalQuantity(UPDATED_TOTAL_QUANTITY)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .grandTotal(UPDATED_GRAND_TOTAL)
            .active(UPDATED_ACTIVE);

        restPurchaseOperationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchaseOperation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPurchaseOperation))
            )
            .andExpect(status().isOk());

        // Validate the PurchaseOperation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPurchaseOperationUpdatableFieldsEquals(
            partialUpdatedPurchaseOperation,
            getPersistedPurchaseOperation(partialUpdatedPurchaseOperation)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPurchaseOperation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchaseOperation.setId(longCount.incrementAndGet());

        // Create the PurchaseOperation
        PurchaseOperationDTO purchaseOperationDTO = purchaseOperationMapper.toDto(purchaseOperation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchaseOperationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, purchaseOperationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(purchaseOperationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseOperation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPurchaseOperation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchaseOperation.setId(longCount.incrementAndGet());

        // Create the PurchaseOperation
        PurchaseOperationDTO purchaseOperationDTO = purchaseOperationMapper.toDto(purchaseOperation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseOperationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(purchaseOperationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseOperation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPurchaseOperation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchaseOperation.setId(longCount.incrementAndGet());

        // Create the PurchaseOperation
        PurchaseOperationDTO purchaseOperationDTO = purchaseOperationMapper.toDto(purchaseOperation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseOperationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(purchaseOperationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PurchaseOperation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePurchaseOperation() throws Exception {
        // Initialize the database
        insertedPurchaseOperation = purchaseOperationRepository.saveAndFlush(purchaseOperation);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the purchaseOperation
        restPurchaseOperationMockMvc
            .perform(delete(ENTITY_API_URL_ID, purchaseOperation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return purchaseOperationRepository.count();
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

    protected PurchaseOperation getPersistedPurchaseOperation(PurchaseOperation purchaseOperation) {
        return purchaseOperationRepository.findById(purchaseOperation.getId()).orElseThrow();
    }

    protected void assertPersistedPurchaseOperationToMatchAllProperties(PurchaseOperation expectedPurchaseOperation) {
        assertPurchaseOperationAllPropertiesEquals(expectedPurchaseOperation, getPersistedPurchaseOperation(expectedPurchaseOperation));
    }

    protected void assertPersistedPurchaseOperationToMatchUpdatableProperties(PurchaseOperation expectedPurchaseOperation) {
        assertPurchaseOperationAllUpdatablePropertiesEquals(
            expectedPurchaseOperation,
            getPersistedPurchaseOperation(expectedPurchaseOperation)
        );
    }
}
