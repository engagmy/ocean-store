package com.agamy.inventory.web.rest;

import static com.agamy.inventory.domain.BillAsserts.*;
import static com.agamy.inventory.web.rest.TestUtil.createUpdateProxyForBean;
import static com.agamy.inventory.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.agamy.inventory.IntegrationTest;
import com.agamy.inventory.domain.Bill;
import com.agamy.inventory.repository.BillRepository;
import com.agamy.inventory.service.dto.BillDTO;
import com.agamy.inventory.service.mapper.BillMapper;
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
 * Integration tests for the {@link BillResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BillResourceIT {

    private static final String DEFAULT_BILL_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_BILL_NUMBER = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_TOTAL_AMOUNT = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_TAX_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TAX_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_TAX_AMOUNT = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_DISCOUNT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_DISCOUNT_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_DISCOUNT_AMOUNT = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_PAID_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_PAID_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_PAID_AMOUNT = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_DUE_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_DUE_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_DUE_AMOUNT = new BigDecimal(1 - 1);

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/bills";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillMapper billMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBillMockMvc;

    private Bill bill;

    private Bill insertedBill;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bill createEntity() {
        return new Bill()
            .billNumber(DEFAULT_BILL_NUMBER)
            .date(DEFAULT_DATE)
            .totalAmount(DEFAULT_TOTAL_AMOUNT)
            .taxAmount(DEFAULT_TAX_AMOUNT)
            .discountAmount(DEFAULT_DISCOUNT_AMOUNT)
            .paidAmount(DEFAULT_PAID_AMOUNT)
            .dueAmount(DEFAULT_DUE_AMOUNT)
            .notes(DEFAULT_NOTES)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bill createUpdatedEntity() {
        return new Bill()
            .billNumber(UPDATED_BILL_NUMBER)
            .date(UPDATED_DATE)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .paidAmount(UPDATED_PAID_AMOUNT)
            .dueAmount(UPDATED_DUE_AMOUNT)
            .notes(UPDATED_NOTES)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        bill = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBill != null) {
            billRepository.delete(insertedBill);
            insertedBill = null;
        }
    }

    @Test
    @Transactional
    void createBill() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Bill
        BillDTO billDTO = billMapper.toDto(bill);
        var returnedBillDTO = om.readValue(
            restBillMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(billDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BillDTO.class
        );

        // Validate the Bill in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBill = billMapper.toEntity(returnedBillDTO);
        assertBillUpdatableFieldsEquals(returnedBill, getPersistedBill(returnedBill));

        insertedBill = returnedBill;
    }

    @Test
    @Transactional
    void createBillWithExistingId() throws Exception {
        // Create the Bill with an existing ID
        bill.setId(1L);
        BillDTO billDTO = billMapper.toDto(bill);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBillMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(billDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Bill in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkBillNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        bill.setBillNumber(null);

        // Create the Bill, which fails.
        BillDTO billDTO = billMapper.toDto(bill);

        restBillMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(billDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        bill.setDate(null);

        // Create the Bill, which fails.
        BillDTO billDTO = billMapper.toDto(bill);

        restBillMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(billDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        bill.setTotalAmount(null);

        // Create the Bill, which fails.
        BillDTO billDTO = billMapper.toDto(bill);

        restBillMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(billDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBills() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList
        restBillMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bill.getId().intValue())))
            .andExpect(jsonPath("$.[*].billNumber").value(hasItem(DEFAULT_BILL_NUMBER)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].taxAmount").value(hasItem(sameNumber(DEFAULT_TAX_AMOUNT))))
            .andExpect(jsonPath("$.[*].discountAmount").value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT))))
            .andExpect(jsonPath("$.[*].paidAmount").value(hasItem(sameNumber(DEFAULT_PAID_AMOUNT))))
            .andExpect(jsonPath("$.[*].dueAmount").value(hasItem(sameNumber(DEFAULT_DUE_AMOUNT))))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getBill() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get the bill
        restBillMockMvc
            .perform(get(ENTITY_API_URL_ID, bill.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bill.getId().intValue()))
            .andExpect(jsonPath("$.billNumber").value(DEFAULT_BILL_NUMBER))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.totalAmount").value(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .andExpect(jsonPath("$.taxAmount").value(sameNumber(DEFAULT_TAX_AMOUNT)))
            .andExpect(jsonPath("$.discountAmount").value(sameNumber(DEFAULT_DISCOUNT_AMOUNT)))
            .andExpect(jsonPath("$.paidAmount").value(sameNumber(DEFAULT_PAID_AMOUNT)))
            .andExpect(jsonPath("$.dueAmount").value(sameNumber(DEFAULT_DUE_AMOUNT)))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getBillsByIdFiltering() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        Long id = bill.getId();

        defaultBillFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBillFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBillFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBillsByBillNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where billNumber equals to
        defaultBillFiltering("billNumber.equals=" + DEFAULT_BILL_NUMBER, "billNumber.equals=" + UPDATED_BILL_NUMBER);
    }

    @Test
    @Transactional
    void getAllBillsByBillNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where billNumber in
        defaultBillFiltering("billNumber.in=" + DEFAULT_BILL_NUMBER + "," + UPDATED_BILL_NUMBER, "billNumber.in=" + UPDATED_BILL_NUMBER);
    }

    @Test
    @Transactional
    void getAllBillsByBillNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where billNumber is not null
        defaultBillFiltering("billNumber.specified=true", "billNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllBillsByBillNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where billNumber contains
        defaultBillFiltering("billNumber.contains=" + DEFAULT_BILL_NUMBER, "billNumber.contains=" + UPDATED_BILL_NUMBER);
    }

    @Test
    @Transactional
    void getAllBillsByBillNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where billNumber does not contain
        defaultBillFiltering("billNumber.doesNotContain=" + UPDATED_BILL_NUMBER, "billNumber.doesNotContain=" + DEFAULT_BILL_NUMBER);
    }

    @Test
    @Transactional
    void getAllBillsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where date equals to
        defaultBillFiltering("date.equals=" + DEFAULT_DATE, "date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllBillsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where date in
        defaultBillFiltering("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE, "date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllBillsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where date is not null
        defaultBillFiltering("date.specified=true", "date.specified=false");
    }

    @Test
    @Transactional
    void getAllBillsByTotalAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where totalAmount equals to
        defaultBillFiltering("totalAmount.equals=" + DEFAULT_TOTAL_AMOUNT, "totalAmount.equals=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByTotalAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where totalAmount in
        defaultBillFiltering(
            "totalAmount.in=" + DEFAULT_TOTAL_AMOUNT + "," + UPDATED_TOTAL_AMOUNT,
            "totalAmount.in=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllBillsByTotalAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where totalAmount is not null
        defaultBillFiltering("totalAmount.specified=true", "totalAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllBillsByTotalAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where totalAmount is greater than or equal to
        defaultBillFiltering(
            "totalAmount.greaterThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.greaterThanOrEqual=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllBillsByTotalAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where totalAmount is less than or equal to
        defaultBillFiltering("totalAmount.lessThanOrEqual=" + DEFAULT_TOTAL_AMOUNT, "totalAmount.lessThanOrEqual=" + SMALLER_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByTotalAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where totalAmount is less than
        defaultBillFiltering("totalAmount.lessThan=" + UPDATED_TOTAL_AMOUNT, "totalAmount.lessThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByTotalAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where totalAmount is greater than
        defaultBillFiltering("totalAmount.greaterThan=" + SMALLER_TOTAL_AMOUNT, "totalAmount.greaterThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByTaxAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where taxAmount equals to
        defaultBillFiltering("taxAmount.equals=" + DEFAULT_TAX_AMOUNT, "taxAmount.equals=" + UPDATED_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByTaxAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where taxAmount in
        defaultBillFiltering("taxAmount.in=" + DEFAULT_TAX_AMOUNT + "," + UPDATED_TAX_AMOUNT, "taxAmount.in=" + UPDATED_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByTaxAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where taxAmount is not null
        defaultBillFiltering("taxAmount.specified=true", "taxAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllBillsByTaxAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where taxAmount is greater than or equal to
        defaultBillFiltering("taxAmount.greaterThanOrEqual=" + DEFAULT_TAX_AMOUNT, "taxAmount.greaterThanOrEqual=" + UPDATED_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByTaxAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where taxAmount is less than or equal to
        defaultBillFiltering("taxAmount.lessThanOrEqual=" + DEFAULT_TAX_AMOUNT, "taxAmount.lessThanOrEqual=" + SMALLER_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByTaxAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where taxAmount is less than
        defaultBillFiltering("taxAmount.lessThan=" + UPDATED_TAX_AMOUNT, "taxAmount.lessThan=" + DEFAULT_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByTaxAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where taxAmount is greater than
        defaultBillFiltering("taxAmount.greaterThan=" + SMALLER_TAX_AMOUNT, "taxAmount.greaterThan=" + DEFAULT_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByDiscountAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where discountAmount equals to
        defaultBillFiltering("discountAmount.equals=" + DEFAULT_DISCOUNT_AMOUNT, "discountAmount.equals=" + UPDATED_DISCOUNT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByDiscountAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where discountAmount in
        defaultBillFiltering(
            "discountAmount.in=" + DEFAULT_DISCOUNT_AMOUNT + "," + UPDATED_DISCOUNT_AMOUNT,
            "discountAmount.in=" + UPDATED_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllBillsByDiscountAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where discountAmount is not null
        defaultBillFiltering("discountAmount.specified=true", "discountAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllBillsByDiscountAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where discountAmount is greater than or equal to
        defaultBillFiltering(
            "discountAmount.greaterThanOrEqual=" + DEFAULT_DISCOUNT_AMOUNT,
            "discountAmount.greaterThanOrEqual=" + UPDATED_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllBillsByDiscountAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where discountAmount is less than or equal to
        defaultBillFiltering(
            "discountAmount.lessThanOrEqual=" + DEFAULT_DISCOUNT_AMOUNT,
            "discountAmount.lessThanOrEqual=" + SMALLER_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllBillsByDiscountAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where discountAmount is less than
        defaultBillFiltering("discountAmount.lessThan=" + UPDATED_DISCOUNT_AMOUNT, "discountAmount.lessThan=" + DEFAULT_DISCOUNT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByDiscountAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where discountAmount is greater than
        defaultBillFiltering(
            "discountAmount.greaterThan=" + SMALLER_DISCOUNT_AMOUNT,
            "discountAmount.greaterThan=" + DEFAULT_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllBillsByPaidAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where paidAmount equals to
        defaultBillFiltering("paidAmount.equals=" + DEFAULT_PAID_AMOUNT, "paidAmount.equals=" + UPDATED_PAID_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByPaidAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where paidAmount in
        defaultBillFiltering("paidAmount.in=" + DEFAULT_PAID_AMOUNT + "," + UPDATED_PAID_AMOUNT, "paidAmount.in=" + UPDATED_PAID_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByPaidAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where paidAmount is not null
        defaultBillFiltering("paidAmount.specified=true", "paidAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllBillsByPaidAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where paidAmount is greater than or equal to
        defaultBillFiltering(
            "paidAmount.greaterThanOrEqual=" + DEFAULT_PAID_AMOUNT,
            "paidAmount.greaterThanOrEqual=" + UPDATED_PAID_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllBillsByPaidAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where paidAmount is less than or equal to
        defaultBillFiltering("paidAmount.lessThanOrEqual=" + DEFAULT_PAID_AMOUNT, "paidAmount.lessThanOrEqual=" + SMALLER_PAID_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByPaidAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where paidAmount is less than
        defaultBillFiltering("paidAmount.lessThan=" + UPDATED_PAID_AMOUNT, "paidAmount.lessThan=" + DEFAULT_PAID_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByPaidAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where paidAmount is greater than
        defaultBillFiltering("paidAmount.greaterThan=" + SMALLER_PAID_AMOUNT, "paidAmount.greaterThan=" + DEFAULT_PAID_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByDueAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where dueAmount equals to
        defaultBillFiltering("dueAmount.equals=" + DEFAULT_DUE_AMOUNT, "dueAmount.equals=" + UPDATED_DUE_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByDueAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where dueAmount in
        defaultBillFiltering("dueAmount.in=" + DEFAULT_DUE_AMOUNT + "," + UPDATED_DUE_AMOUNT, "dueAmount.in=" + UPDATED_DUE_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByDueAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where dueAmount is not null
        defaultBillFiltering("dueAmount.specified=true", "dueAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllBillsByDueAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where dueAmount is greater than or equal to
        defaultBillFiltering("dueAmount.greaterThanOrEqual=" + DEFAULT_DUE_AMOUNT, "dueAmount.greaterThanOrEqual=" + UPDATED_DUE_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByDueAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where dueAmount is less than or equal to
        defaultBillFiltering("dueAmount.lessThanOrEqual=" + DEFAULT_DUE_AMOUNT, "dueAmount.lessThanOrEqual=" + SMALLER_DUE_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByDueAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where dueAmount is less than
        defaultBillFiltering("dueAmount.lessThan=" + UPDATED_DUE_AMOUNT, "dueAmount.lessThan=" + DEFAULT_DUE_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByDueAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where dueAmount is greater than
        defaultBillFiltering("dueAmount.greaterThan=" + SMALLER_DUE_AMOUNT, "dueAmount.greaterThan=" + DEFAULT_DUE_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBillsByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where notes equals to
        defaultBillFiltering("notes.equals=" + DEFAULT_NOTES, "notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllBillsByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where notes in
        defaultBillFiltering("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES, "notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllBillsByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where notes is not null
        defaultBillFiltering("notes.specified=true", "notes.specified=false");
    }

    @Test
    @Transactional
    void getAllBillsByNotesContainsSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where notes contains
        defaultBillFiltering("notes.contains=" + DEFAULT_NOTES, "notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllBillsByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where notes does not contain
        defaultBillFiltering("notes.doesNotContain=" + UPDATED_NOTES, "notes.doesNotContain=" + DEFAULT_NOTES);
    }

    @Test
    @Transactional
    void getAllBillsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where active equals to
        defaultBillFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllBillsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where active in
        defaultBillFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllBillsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        // Get all the billList where active is not null
        defaultBillFiltering("active.specified=true", "active.specified=false");
    }

    private void defaultBillFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBillShouldBeFound(shouldBeFound);
        defaultBillShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBillShouldBeFound(String filter) throws Exception {
        restBillMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bill.getId().intValue())))
            .andExpect(jsonPath("$.[*].billNumber").value(hasItem(DEFAULT_BILL_NUMBER)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].taxAmount").value(hasItem(sameNumber(DEFAULT_TAX_AMOUNT))))
            .andExpect(jsonPath("$.[*].discountAmount").value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT))))
            .andExpect(jsonPath("$.[*].paidAmount").value(hasItem(sameNumber(DEFAULT_PAID_AMOUNT))))
            .andExpect(jsonPath("$.[*].dueAmount").value(hasItem(sameNumber(DEFAULT_DUE_AMOUNT))))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));

        // Check, that the count call also returns 1
        restBillMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBillShouldNotBeFound(String filter) throws Exception {
        restBillMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBillMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBill() throws Exception {
        // Get the bill
        restBillMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBill() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bill
        Bill updatedBill = billRepository.findById(bill.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBill are not directly saved in db
        em.detach(updatedBill);
        updatedBill
            .billNumber(UPDATED_BILL_NUMBER)
            .date(UPDATED_DATE)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .paidAmount(UPDATED_PAID_AMOUNT)
            .dueAmount(UPDATED_DUE_AMOUNT)
            .notes(UPDATED_NOTES)
            .active(UPDATED_ACTIVE);
        BillDTO billDTO = billMapper.toDto(updatedBill);

        restBillMockMvc
            .perform(put(ENTITY_API_URL_ID, billDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(billDTO)))
            .andExpect(status().isOk());

        // Validate the Bill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBillToMatchAllProperties(updatedBill);
    }

    @Test
    @Transactional
    void putNonExistingBill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bill.setId(longCount.incrementAndGet());

        // Create the Bill
        BillDTO billDTO = billMapper.toDto(bill);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBillMockMvc
            .perform(put(ENTITY_API_URL_ID, billDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(billDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Bill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bill.setId(longCount.incrementAndGet());

        // Create the Bill
        BillDTO billDTO = billMapper.toDto(bill);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBillMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(billDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bill.setId(longCount.incrementAndGet());

        // Create the Bill
        BillDTO billDTO = billMapper.toDto(bill);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBillMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(billDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBillWithPatch() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bill using partial update
        Bill partialUpdatedBill = new Bill();
        partialUpdatedBill.setId(bill.getId());

        partialUpdatedBill
            .billNumber(UPDATED_BILL_NUMBER)
            .date(UPDATED_DATE)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .active(UPDATED_ACTIVE);

        restBillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBill.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBill))
            )
            .andExpect(status().isOk());

        // Validate the Bill in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBillUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBill, bill), getPersistedBill(bill));
    }

    @Test
    @Transactional
    void fullUpdateBillWithPatch() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bill using partial update
        Bill partialUpdatedBill = new Bill();
        partialUpdatedBill.setId(bill.getId());

        partialUpdatedBill
            .billNumber(UPDATED_BILL_NUMBER)
            .date(UPDATED_DATE)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .paidAmount(UPDATED_PAID_AMOUNT)
            .dueAmount(UPDATED_DUE_AMOUNT)
            .notes(UPDATED_NOTES)
            .active(UPDATED_ACTIVE);

        restBillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBill.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBill))
            )
            .andExpect(status().isOk());

        // Validate the Bill in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBillUpdatableFieldsEquals(partialUpdatedBill, getPersistedBill(partialUpdatedBill));
    }

    @Test
    @Transactional
    void patchNonExistingBill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bill.setId(longCount.incrementAndGet());

        // Create the Bill
        BillDTO billDTO = billMapper.toDto(bill);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, billDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(billDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bill.setId(longCount.incrementAndGet());

        // Create the Bill
        BillDTO billDTO = billMapper.toDto(bill);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(billDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bill.setId(longCount.incrementAndGet());

        // Create the Bill
        BillDTO billDTO = billMapper.toDto(bill);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBillMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(billDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBill() throws Exception {
        // Initialize the database
        insertedBill = billRepository.saveAndFlush(bill);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the bill
        restBillMockMvc
            .perform(delete(ENTITY_API_URL_ID, bill.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return billRepository.count();
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

    protected Bill getPersistedBill(Bill bill) {
        return billRepository.findById(bill.getId()).orElseThrow();
    }

    protected void assertPersistedBillToMatchAllProperties(Bill expectedBill) {
        assertBillAllPropertiesEquals(expectedBill, getPersistedBill(expectedBill));
    }

    protected void assertPersistedBillToMatchUpdatableProperties(Bill expectedBill) {
        assertBillAllUpdatablePropertiesEquals(expectedBill, getPersistedBill(expectedBill));
    }
}
