package com.agamy.inventory.web.rest;

import static com.agamy.inventory.domain.SaleOperationAsserts.*;
import static com.agamy.inventory.web.rest.TestUtil.createUpdateProxyForBean;
import static com.agamy.inventory.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.agamy.inventory.IntegrationTest;
import com.agamy.inventory.domain.Bill;
import com.agamy.inventory.domain.Customer;
import com.agamy.inventory.domain.SaleOperation;
import com.agamy.inventory.repository.SaleOperationRepository;
import com.agamy.inventory.service.dto.SaleOperationDTO;
import com.agamy.inventory.service.mapper.SaleOperationMapper;
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
 * Integration tests for the {@link SaleOperationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SaleOperationResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_TOTAL_QUANTITY = 1;
    private static final Integer UPDATED_TOTAL_QUANTITY = 2;
    private static final Integer SMALLER_TOTAL_QUANTITY = 1 - 1;

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_TOTAL_AMOUNT = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_TOTAL_DISCOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_DISCOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_TOTAL_DISCOUNT = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_GRAND_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_GRAND_TOTAL = new BigDecimal(2);
    private static final BigDecimal SMALLER_GRAND_TOTAL = new BigDecimal(1 - 1);

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/sale-operations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SaleOperationRepository saleOperationRepository;

    @Autowired
    private SaleOperationMapper saleOperationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSaleOperationMockMvc;

    private SaleOperation saleOperation;

    private SaleOperation insertedSaleOperation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SaleOperation createEntity() {
        return new SaleOperation()
            .date(DEFAULT_DATE)
            .totalQuantity(DEFAULT_TOTAL_QUANTITY)
            .totalAmount(DEFAULT_TOTAL_AMOUNT)
            .totalDiscount(DEFAULT_TOTAL_DISCOUNT)
            .grandTotal(DEFAULT_GRAND_TOTAL)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SaleOperation createUpdatedEntity() {
        return new SaleOperation()
            .date(UPDATED_DATE)
            .totalQuantity(UPDATED_TOTAL_QUANTITY)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .totalDiscount(UPDATED_TOTAL_DISCOUNT)
            .grandTotal(UPDATED_GRAND_TOTAL)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        saleOperation = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSaleOperation != null) {
            saleOperationRepository.delete(insertedSaleOperation);
            insertedSaleOperation = null;
        }
    }

    @Test
    @Transactional
    void createSaleOperation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SaleOperation
        SaleOperationDTO saleOperationDTO = saleOperationMapper.toDto(saleOperation);
        var returnedSaleOperationDTO = om.readValue(
            restSaleOperationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleOperationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SaleOperationDTO.class
        );

        // Validate the SaleOperation in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSaleOperation = saleOperationMapper.toEntity(returnedSaleOperationDTO);
        assertSaleOperationUpdatableFieldsEquals(returnedSaleOperation, getPersistedSaleOperation(returnedSaleOperation));

        insertedSaleOperation = returnedSaleOperation;
    }

    @Test
    @Transactional
    void createSaleOperationWithExistingId() throws Exception {
        // Create the SaleOperation with an existing ID
        saleOperation.setId(1L);
        SaleOperationDTO saleOperationDTO = saleOperationMapper.toDto(saleOperation);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSaleOperationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleOperationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SaleOperation in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        saleOperation.setDate(null);

        // Create the SaleOperation, which fails.
        SaleOperationDTO saleOperationDTO = saleOperationMapper.toDto(saleOperation);

        restSaleOperationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleOperationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        saleOperation.setTotalQuantity(null);

        // Create the SaleOperation, which fails.
        SaleOperationDTO saleOperationDTO = saleOperationMapper.toDto(saleOperation);

        restSaleOperationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleOperationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        saleOperation.setTotalAmount(null);

        // Create the SaleOperation, which fails.
        SaleOperationDTO saleOperationDTO = saleOperationMapper.toDto(saleOperation);

        restSaleOperationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleOperationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGrandTotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        saleOperation.setGrandTotal(null);

        // Create the SaleOperation, which fails.
        SaleOperationDTO saleOperationDTO = saleOperationMapper.toDto(saleOperation);

        restSaleOperationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleOperationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSaleOperations() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList
        restSaleOperationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(saleOperation.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].totalQuantity").value(hasItem(DEFAULT_TOTAL_QUANTITY)))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].totalDiscount").value(hasItem(sameNumber(DEFAULT_TOTAL_DISCOUNT))))
            .andExpect(jsonPath("$.[*].grandTotal").value(hasItem(sameNumber(DEFAULT_GRAND_TOTAL))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getSaleOperation() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get the saleOperation
        restSaleOperationMockMvc
            .perform(get(ENTITY_API_URL_ID, saleOperation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(saleOperation.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.totalQuantity").value(DEFAULT_TOTAL_QUANTITY))
            .andExpect(jsonPath("$.totalAmount").value(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .andExpect(jsonPath("$.totalDiscount").value(sameNumber(DEFAULT_TOTAL_DISCOUNT)))
            .andExpect(jsonPath("$.grandTotal").value(sameNumber(DEFAULT_GRAND_TOTAL)))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getSaleOperationsByIdFiltering() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        Long id = saleOperation.getId();

        defaultSaleOperationFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSaleOperationFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSaleOperationFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSaleOperationsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where date equals to
        defaultSaleOperationFiltering("date.equals=" + DEFAULT_DATE, "date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllSaleOperationsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where date in
        defaultSaleOperationFiltering("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE, "date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllSaleOperationsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where date is not null
        defaultSaleOperationFiltering("date.specified=true", "date.specified=false");
    }

    @Test
    @Transactional
    void getAllSaleOperationsByTotalQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where totalQuantity equals to
        defaultSaleOperationFiltering("totalQuantity.equals=" + DEFAULT_TOTAL_QUANTITY, "totalQuantity.equals=" + UPDATED_TOTAL_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSaleOperationsByTotalQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where totalQuantity in
        defaultSaleOperationFiltering(
            "totalQuantity.in=" + DEFAULT_TOTAL_QUANTITY + "," + UPDATED_TOTAL_QUANTITY,
            "totalQuantity.in=" + UPDATED_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllSaleOperationsByTotalQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where totalQuantity is not null
        defaultSaleOperationFiltering("totalQuantity.specified=true", "totalQuantity.specified=false");
    }

    @Test
    @Transactional
    void getAllSaleOperationsByTotalQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where totalQuantity is greater than or equal to
        defaultSaleOperationFiltering(
            "totalQuantity.greaterThanOrEqual=" + DEFAULT_TOTAL_QUANTITY,
            "totalQuantity.greaterThanOrEqual=" + UPDATED_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllSaleOperationsByTotalQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where totalQuantity is less than or equal to
        defaultSaleOperationFiltering(
            "totalQuantity.lessThanOrEqual=" + DEFAULT_TOTAL_QUANTITY,
            "totalQuantity.lessThanOrEqual=" + SMALLER_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllSaleOperationsByTotalQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where totalQuantity is less than
        defaultSaleOperationFiltering(
            "totalQuantity.lessThan=" + UPDATED_TOTAL_QUANTITY,
            "totalQuantity.lessThan=" + DEFAULT_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllSaleOperationsByTotalQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where totalQuantity is greater than
        defaultSaleOperationFiltering(
            "totalQuantity.greaterThan=" + SMALLER_TOTAL_QUANTITY,
            "totalQuantity.greaterThan=" + DEFAULT_TOTAL_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllSaleOperationsByTotalAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where totalAmount equals to
        defaultSaleOperationFiltering("totalAmount.equals=" + DEFAULT_TOTAL_AMOUNT, "totalAmount.equals=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSaleOperationsByTotalAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where totalAmount in
        defaultSaleOperationFiltering(
            "totalAmount.in=" + DEFAULT_TOTAL_AMOUNT + "," + UPDATED_TOTAL_AMOUNT,
            "totalAmount.in=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllSaleOperationsByTotalAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where totalAmount is not null
        defaultSaleOperationFiltering("totalAmount.specified=true", "totalAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllSaleOperationsByTotalAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where totalAmount is greater than or equal to
        defaultSaleOperationFiltering(
            "totalAmount.greaterThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.greaterThanOrEqual=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllSaleOperationsByTotalAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where totalAmount is less than or equal to
        defaultSaleOperationFiltering(
            "totalAmount.lessThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.lessThanOrEqual=" + SMALLER_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllSaleOperationsByTotalAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where totalAmount is less than
        defaultSaleOperationFiltering("totalAmount.lessThan=" + UPDATED_TOTAL_AMOUNT, "totalAmount.lessThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSaleOperationsByTotalAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where totalAmount is greater than
        defaultSaleOperationFiltering("totalAmount.greaterThan=" + SMALLER_TOTAL_AMOUNT, "totalAmount.greaterThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSaleOperationsByTotalDiscountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where totalDiscount equals to
        defaultSaleOperationFiltering("totalDiscount.equals=" + DEFAULT_TOTAL_DISCOUNT, "totalDiscount.equals=" + UPDATED_TOTAL_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllSaleOperationsByTotalDiscountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where totalDiscount in
        defaultSaleOperationFiltering(
            "totalDiscount.in=" + DEFAULT_TOTAL_DISCOUNT + "," + UPDATED_TOTAL_DISCOUNT,
            "totalDiscount.in=" + UPDATED_TOTAL_DISCOUNT
        );
    }

    @Test
    @Transactional
    void getAllSaleOperationsByTotalDiscountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where totalDiscount is not null
        defaultSaleOperationFiltering("totalDiscount.specified=true", "totalDiscount.specified=false");
    }

    @Test
    @Transactional
    void getAllSaleOperationsByTotalDiscountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where totalDiscount is greater than or equal to
        defaultSaleOperationFiltering(
            "totalDiscount.greaterThanOrEqual=" + DEFAULT_TOTAL_DISCOUNT,
            "totalDiscount.greaterThanOrEqual=" + UPDATED_TOTAL_DISCOUNT
        );
    }

    @Test
    @Transactional
    void getAllSaleOperationsByTotalDiscountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where totalDiscount is less than or equal to
        defaultSaleOperationFiltering(
            "totalDiscount.lessThanOrEqual=" + DEFAULT_TOTAL_DISCOUNT,
            "totalDiscount.lessThanOrEqual=" + SMALLER_TOTAL_DISCOUNT
        );
    }

    @Test
    @Transactional
    void getAllSaleOperationsByTotalDiscountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where totalDiscount is less than
        defaultSaleOperationFiltering(
            "totalDiscount.lessThan=" + UPDATED_TOTAL_DISCOUNT,
            "totalDiscount.lessThan=" + DEFAULT_TOTAL_DISCOUNT
        );
    }

    @Test
    @Transactional
    void getAllSaleOperationsByTotalDiscountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where totalDiscount is greater than
        defaultSaleOperationFiltering(
            "totalDiscount.greaterThan=" + SMALLER_TOTAL_DISCOUNT,
            "totalDiscount.greaterThan=" + DEFAULT_TOTAL_DISCOUNT
        );
    }

    @Test
    @Transactional
    void getAllSaleOperationsByGrandTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where grandTotal equals to
        defaultSaleOperationFiltering("grandTotal.equals=" + DEFAULT_GRAND_TOTAL, "grandTotal.equals=" + UPDATED_GRAND_TOTAL);
    }

    @Test
    @Transactional
    void getAllSaleOperationsByGrandTotalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where grandTotal in
        defaultSaleOperationFiltering(
            "grandTotal.in=" + DEFAULT_GRAND_TOTAL + "," + UPDATED_GRAND_TOTAL,
            "grandTotal.in=" + UPDATED_GRAND_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllSaleOperationsByGrandTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where grandTotal is not null
        defaultSaleOperationFiltering("grandTotal.specified=true", "grandTotal.specified=false");
    }

    @Test
    @Transactional
    void getAllSaleOperationsByGrandTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where grandTotal is greater than or equal to
        defaultSaleOperationFiltering(
            "grandTotal.greaterThanOrEqual=" + DEFAULT_GRAND_TOTAL,
            "grandTotal.greaterThanOrEqual=" + UPDATED_GRAND_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllSaleOperationsByGrandTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where grandTotal is less than or equal to
        defaultSaleOperationFiltering(
            "grandTotal.lessThanOrEqual=" + DEFAULT_GRAND_TOTAL,
            "grandTotal.lessThanOrEqual=" + SMALLER_GRAND_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllSaleOperationsByGrandTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where grandTotal is less than
        defaultSaleOperationFiltering("grandTotal.lessThan=" + UPDATED_GRAND_TOTAL, "grandTotal.lessThan=" + DEFAULT_GRAND_TOTAL);
    }

    @Test
    @Transactional
    void getAllSaleOperationsByGrandTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where grandTotal is greater than
        defaultSaleOperationFiltering("grandTotal.greaterThan=" + SMALLER_GRAND_TOTAL, "grandTotal.greaterThan=" + DEFAULT_GRAND_TOTAL);
    }

    @Test
    @Transactional
    void getAllSaleOperationsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where active equals to
        defaultSaleOperationFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllSaleOperationsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where active in
        defaultSaleOperationFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllSaleOperationsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        // Get all the saleOperationList where active is not null
        defaultSaleOperationFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllSaleOperationsByBillIsEqualToSomething() throws Exception {
        Bill bill;
        if (TestUtil.findAll(em, Bill.class).isEmpty()) {
            saleOperationRepository.saveAndFlush(saleOperation);
            bill = BillResourceIT.createEntity();
        } else {
            bill = TestUtil.findAll(em, Bill.class).get(0);
        }
        em.persist(bill);
        em.flush();
        saleOperation.setBill(bill);
        saleOperationRepository.saveAndFlush(saleOperation);
        Long billId = bill.getId();
        // Get all the saleOperationList where bill equals to billId
        defaultSaleOperationShouldBeFound("billId.equals=" + billId);

        // Get all the saleOperationList where bill equals to (billId + 1)
        defaultSaleOperationShouldNotBeFound("billId.equals=" + (billId + 1));
    }

    @Test
    @Transactional
    void getAllSaleOperationsByCustomerIsEqualToSomething() throws Exception {
        Customer customer;
        if (TestUtil.findAll(em, Customer.class).isEmpty()) {
            saleOperationRepository.saveAndFlush(saleOperation);
            customer = CustomerResourceIT.createEntity();
        } else {
            customer = TestUtil.findAll(em, Customer.class).get(0);
        }
        em.persist(customer);
        em.flush();
        saleOperation.setCustomer(customer);
        saleOperationRepository.saveAndFlush(saleOperation);
        Long customerId = customer.getId();
        // Get all the saleOperationList where customer equals to customerId
        defaultSaleOperationShouldBeFound("customerId.equals=" + customerId);

        // Get all the saleOperationList where customer equals to (customerId + 1)
        defaultSaleOperationShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    private void defaultSaleOperationFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSaleOperationShouldBeFound(shouldBeFound);
        defaultSaleOperationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSaleOperationShouldBeFound(String filter) throws Exception {
        restSaleOperationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(saleOperation.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].totalQuantity").value(hasItem(DEFAULT_TOTAL_QUANTITY)))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].totalDiscount").value(hasItem(sameNumber(DEFAULT_TOTAL_DISCOUNT))))
            .andExpect(jsonPath("$.[*].grandTotal").value(hasItem(sameNumber(DEFAULT_GRAND_TOTAL))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));

        // Check, that the count call also returns 1
        restSaleOperationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSaleOperationShouldNotBeFound(String filter) throws Exception {
        restSaleOperationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSaleOperationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSaleOperation() throws Exception {
        // Get the saleOperation
        restSaleOperationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSaleOperation() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the saleOperation
        SaleOperation updatedSaleOperation = saleOperationRepository.findById(saleOperation.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSaleOperation are not directly saved in db
        em.detach(updatedSaleOperation);
        updatedSaleOperation
            .date(UPDATED_DATE)
            .totalQuantity(UPDATED_TOTAL_QUANTITY)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .totalDiscount(UPDATED_TOTAL_DISCOUNT)
            .grandTotal(UPDATED_GRAND_TOTAL)
            .active(UPDATED_ACTIVE);
        SaleOperationDTO saleOperationDTO = saleOperationMapper.toDto(updatedSaleOperation);

        restSaleOperationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, saleOperationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(saleOperationDTO))
            )
            .andExpect(status().isOk());

        // Validate the SaleOperation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSaleOperationToMatchAllProperties(updatedSaleOperation);
    }

    @Test
    @Transactional
    void putNonExistingSaleOperation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleOperation.setId(longCount.incrementAndGet());

        // Create the SaleOperation
        SaleOperationDTO saleOperationDTO = saleOperationMapper.toDto(saleOperation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleOperationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, saleOperationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(saleOperationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleOperation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSaleOperation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleOperation.setId(longCount.incrementAndGet());

        // Create the SaleOperation
        SaleOperationDTO saleOperationDTO = saleOperationMapper.toDto(saleOperation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleOperationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(saleOperationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleOperation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSaleOperation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleOperation.setId(longCount.incrementAndGet());

        // Create the SaleOperation
        SaleOperationDTO saleOperationDTO = saleOperationMapper.toDto(saleOperation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleOperationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleOperationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SaleOperation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSaleOperationWithPatch() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the saleOperation using partial update
        SaleOperation partialUpdatedSaleOperation = new SaleOperation();
        partialUpdatedSaleOperation.setId(saleOperation.getId());

        partialUpdatedSaleOperation
            .date(UPDATED_DATE)
            .totalQuantity(UPDATED_TOTAL_QUANTITY)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .totalDiscount(UPDATED_TOTAL_DISCOUNT)
            .active(UPDATED_ACTIVE);

        restSaleOperationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSaleOperation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSaleOperation))
            )
            .andExpect(status().isOk());

        // Validate the SaleOperation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSaleOperationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSaleOperation, saleOperation),
            getPersistedSaleOperation(saleOperation)
        );
    }

    @Test
    @Transactional
    void fullUpdateSaleOperationWithPatch() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the saleOperation using partial update
        SaleOperation partialUpdatedSaleOperation = new SaleOperation();
        partialUpdatedSaleOperation.setId(saleOperation.getId());

        partialUpdatedSaleOperation
            .date(UPDATED_DATE)
            .totalQuantity(UPDATED_TOTAL_QUANTITY)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .totalDiscount(UPDATED_TOTAL_DISCOUNT)
            .grandTotal(UPDATED_GRAND_TOTAL)
            .active(UPDATED_ACTIVE);

        restSaleOperationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSaleOperation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSaleOperation))
            )
            .andExpect(status().isOk());

        // Validate the SaleOperation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSaleOperationUpdatableFieldsEquals(partialUpdatedSaleOperation, getPersistedSaleOperation(partialUpdatedSaleOperation));
    }

    @Test
    @Transactional
    void patchNonExistingSaleOperation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleOperation.setId(longCount.incrementAndGet());

        // Create the SaleOperation
        SaleOperationDTO saleOperationDTO = saleOperationMapper.toDto(saleOperation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleOperationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, saleOperationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(saleOperationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleOperation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSaleOperation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleOperation.setId(longCount.incrementAndGet());

        // Create the SaleOperation
        SaleOperationDTO saleOperationDTO = saleOperationMapper.toDto(saleOperation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleOperationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(saleOperationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleOperation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSaleOperation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleOperation.setId(longCount.incrementAndGet());

        // Create the SaleOperation
        SaleOperationDTO saleOperationDTO = saleOperationMapper.toDto(saleOperation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleOperationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(saleOperationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SaleOperation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSaleOperation() throws Exception {
        // Initialize the database
        insertedSaleOperation = saleOperationRepository.saveAndFlush(saleOperation);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the saleOperation
        restSaleOperationMockMvc
            .perform(delete(ENTITY_API_URL_ID, saleOperation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return saleOperationRepository.count();
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

    protected SaleOperation getPersistedSaleOperation(SaleOperation saleOperation) {
        return saleOperationRepository.findById(saleOperation.getId()).orElseThrow();
    }

    protected void assertPersistedSaleOperationToMatchAllProperties(SaleOperation expectedSaleOperation) {
        assertSaleOperationAllPropertiesEquals(expectedSaleOperation, getPersistedSaleOperation(expectedSaleOperation));
    }

    protected void assertPersistedSaleOperationToMatchUpdatableProperties(SaleOperation expectedSaleOperation) {
        assertSaleOperationAllUpdatablePropertiesEquals(expectedSaleOperation, getPersistedSaleOperation(expectedSaleOperation));
    }
}
