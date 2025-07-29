package com.agamy.inventory.web.rest;

import static com.agamy.inventory.domain.DailyCashReconciliationAsserts.*;
import static com.agamy.inventory.web.rest.TestUtil.createUpdateProxyForBean;
import static com.agamy.inventory.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.agamy.inventory.IntegrationTest;
import com.agamy.inventory.domain.DailyCashReconciliation;
import com.agamy.inventory.repository.DailyCashReconciliationRepository;
import com.agamy.inventory.service.dto.DailyCashReconciliationDTO;
import com.agamy.inventory.service.mapper.DailyCashReconciliationMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link DailyCashReconciliationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DailyCashReconciliationResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE = LocalDate.ofEpochDay(-1L);

    private static final BigDecimal DEFAULT_OPENING_BALANCE = new BigDecimal(1);
    private static final BigDecimal UPDATED_OPENING_BALANCE = new BigDecimal(2);
    private static final BigDecimal SMALLER_OPENING_BALANCE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_TOTAL_SALES = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_SALES = new BigDecimal(2);
    private static final BigDecimal SMALLER_TOTAL_SALES = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_TOTAL_PURCHASES = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_PURCHASES = new BigDecimal(2);
    private static final BigDecimal SMALLER_TOTAL_PURCHASES = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_TOTAL_SALARY_PAID = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_SALARY_PAID = new BigDecimal(2);
    private static final BigDecimal SMALLER_TOTAL_SALARY_PAID = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_OWNER_DEPOSITS = new BigDecimal(1);
    private static final BigDecimal UPDATED_OWNER_DEPOSITS = new BigDecimal(2);
    private static final BigDecimal SMALLER_OWNER_DEPOSITS = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_WITHDRAWALS = new BigDecimal(1);
    private static final BigDecimal UPDATED_WITHDRAWALS = new BigDecimal(2);
    private static final BigDecimal SMALLER_WITHDRAWALS = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_CLOSING_BALANCE = new BigDecimal(1);
    private static final BigDecimal UPDATED_CLOSING_BALANCE = new BigDecimal(2);
    private static final BigDecimal SMALLER_CLOSING_BALANCE = new BigDecimal(1 - 1);

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/daily-cash-reconciliations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DailyCashReconciliationRepository dailyCashReconciliationRepository;

    @Autowired
    private DailyCashReconciliationMapper dailyCashReconciliationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDailyCashReconciliationMockMvc;

    private DailyCashReconciliation dailyCashReconciliation;

    private DailyCashReconciliation insertedDailyCashReconciliation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DailyCashReconciliation createEntity() {
        return new DailyCashReconciliation()
            .date(DEFAULT_DATE)
            .openingBalance(DEFAULT_OPENING_BALANCE)
            .totalSales(DEFAULT_TOTAL_SALES)
            .totalPurchases(DEFAULT_TOTAL_PURCHASES)
            .totalSalaryPaid(DEFAULT_TOTAL_SALARY_PAID)
            .ownerDeposits(DEFAULT_OWNER_DEPOSITS)
            .withdrawals(DEFAULT_WITHDRAWALS)
            .closingBalance(DEFAULT_CLOSING_BALANCE)
            .notes(DEFAULT_NOTES)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DailyCashReconciliation createUpdatedEntity() {
        return new DailyCashReconciliation()
            .date(UPDATED_DATE)
            .openingBalance(UPDATED_OPENING_BALANCE)
            .totalSales(UPDATED_TOTAL_SALES)
            .totalPurchases(UPDATED_TOTAL_PURCHASES)
            .totalSalaryPaid(UPDATED_TOTAL_SALARY_PAID)
            .ownerDeposits(UPDATED_OWNER_DEPOSITS)
            .withdrawals(UPDATED_WITHDRAWALS)
            .closingBalance(UPDATED_CLOSING_BALANCE)
            .notes(UPDATED_NOTES)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        dailyCashReconciliation = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDailyCashReconciliation != null) {
            dailyCashReconciliationRepository.delete(insertedDailyCashReconciliation);
            insertedDailyCashReconciliation = null;
        }
    }

    @Test
    @Transactional
    void createDailyCashReconciliation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DailyCashReconciliation
        DailyCashReconciliationDTO dailyCashReconciliationDTO = dailyCashReconciliationMapper.toDto(dailyCashReconciliation);
        var returnedDailyCashReconciliationDTO = om.readValue(
            restDailyCashReconciliationMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyCashReconciliationDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DailyCashReconciliationDTO.class
        );

        // Validate the DailyCashReconciliation in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDailyCashReconciliation = dailyCashReconciliationMapper.toEntity(returnedDailyCashReconciliationDTO);
        assertDailyCashReconciliationUpdatableFieldsEquals(
            returnedDailyCashReconciliation,
            getPersistedDailyCashReconciliation(returnedDailyCashReconciliation)
        );

        insertedDailyCashReconciliation = returnedDailyCashReconciliation;
    }

    @Test
    @Transactional
    void createDailyCashReconciliationWithExistingId() throws Exception {
        // Create the DailyCashReconciliation with an existing ID
        dailyCashReconciliation.setId(1L);
        DailyCashReconciliationDTO dailyCashReconciliationDTO = dailyCashReconciliationMapper.toDto(dailyCashReconciliation);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDailyCashReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyCashReconciliationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DailyCashReconciliation in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dailyCashReconciliation.setDate(null);

        // Create the DailyCashReconciliation, which fails.
        DailyCashReconciliationDTO dailyCashReconciliationDTO = dailyCashReconciliationMapper.toDto(dailyCashReconciliation);

        restDailyCashReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyCashReconciliationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliations() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList
        restDailyCashReconciliationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dailyCashReconciliation.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].openingBalance").value(hasItem(sameNumber(DEFAULT_OPENING_BALANCE))))
            .andExpect(jsonPath("$.[*].totalSales").value(hasItem(sameNumber(DEFAULT_TOTAL_SALES))))
            .andExpect(jsonPath("$.[*].totalPurchases").value(hasItem(sameNumber(DEFAULT_TOTAL_PURCHASES))))
            .andExpect(jsonPath("$.[*].totalSalaryPaid").value(hasItem(sameNumber(DEFAULT_TOTAL_SALARY_PAID))))
            .andExpect(jsonPath("$.[*].ownerDeposits").value(hasItem(sameNumber(DEFAULT_OWNER_DEPOSITS))))
            .andExpect(jsonPath("$.[*].withdrawals").value(hasItem(sameNumber(DEFAULT_WITHDRAWALS))))
            .andExpect(jsonPath("$.[*].closingBalance").value(hasItem(sameNumber(DEFAULT_CLOSING_BALANCE))))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getDailyCashReconciliation() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get the dailyCashReconciliation
        restDailyCashReconciliationMockMvc
            .perform(get(ENTITY_API_URL_ID, dailyCashReconciliation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dailyCashReconciliation.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.openingBalance").value(sameNumber(DEFAULT_OPENING_BALANCE)))
            .andExpect(jsonPath("$.totalSales").value(sameNumber(DEFAULT_TOTAL_SALES)))
            .andExpect(jsonPath("$.totalPurchases").value(sameNumber(DEFAULT_TOTAL_PURCHASES)))
            .andExpect(jsonPath("$.totalSalaryPaid").value(sameNumber(DEFAULT_TOTAL_SALARY_PAID)))
            .andExpect(jsonPath("$.ownerDeposits").value(sameNumber(DEFAULT_OWNER_DEPOSITS)))
            .andExpect(jsonPath("$.withdrawals").value(sameNumber(DEFAULT_WITHDRAWALS)))
            .andExpect(jsonPath("$.closingBalance").value(sameNumber(DEFAULT_CLOSING_BALANCE)))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getDailyCashReconciliationsByIdFiltering() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        Long id = dailyCashReconciliation.getId();

        defaultDailyCashReconciliationFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDailyCashReconciliationFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDailyCashReconciliationFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where date equals to
        defaultDailyCashReconciliationFiltering("date.equals=" + DEFAULT_DATE, "date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where date in
        defaultDailyCashReconciliationFiltering("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE, "date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where date is not null
        defaultDailyCashReconciliationFiltering("date.specified=true", "date.specified=false");
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where date is greater than or equal to
        defaultDailyCashReconciliationFiltering("date.greaterThanOrEqual=" + DEFAULT_DATE, "date.greaterThanOrEqual=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where date is less than or equal to
        defaultDailyCashReconciliationFiltering("date.lessThanOrEqual=" + DEFAULT_DATE, "date.lessThanOrEqual=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where date is less than
        defaultDailyCashReconciliationFiltering("date.lessThan=" + UPDATED_DATE, "date.lessThan=" + DEFAULT_DATE);
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where date is greater than
        defaultDailyCashReconciliationFiltering("date.greaterThan=" + SMALLER_DATE, "date.greaterThan=" + DEFAULT_DATE);
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByOpeningBalanceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where openingBalance equals to
        defaultDailyCashReconciliationFiltering(
            "openingBalance.equals=" + DEFAULT_OPENING_BALANCE,
            "openingBalance.equals=" + UPDATED_OPENING_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByOpeningBalanceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where openingBalance in
        defaultDailyCashReconciliationFiltering(
            "openingBalance.in=" + DEFAULT_OPENING_BALANCE + "," + UPDATED_OPENING_BALANCE,
            "openingBalance.in=" + UPDATED_OPENING_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByOpeningBalanceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where openingBalance is not null
        defaultDailyCashReconciliationFiltering("openingBalance.specified=true", "openingBalance.specified=false");
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByOpeningBalanceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where openingBalance is greater than or equal to
        defaultDailyCashReconciliationFiltering(
            "openingBalance.greaterThanOrEqual=" + DEFAULT_OPENING_BALANCE,
            "openingBalance.greaterThanOrEqual=" + UPDATED_OPENING_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByOpeningBalanceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where openingBalance is less than or equal to
        defaultDailyCashReconciliationFiltering(
            "openingBalance.lessThanOrEqual=" + DEFAULT_OPENING_BALANCE,
            "openingBalance.lessThanOrEqual=" + SMALLER_OPENING_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByOpeningBalanceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where openingBalance is less than
        defaultDailyCashReconciliationFiltering(
            "openingBalance.lessThan=" + UPDATED_OPENING_BALANCE,
            "openingBalance.lessThan=" + DEFAULT_OPENING_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByOpeningBalanceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where openingBalance is greater than
        defaultDailyCashReconciliationFiltering(
            "openingBalance.greaterThan=" + SMALLER_OPENING_BALANCE,
            "openingBalance.greaterThan=" + DEFAULT_OPENING_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByTotalSalesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where totalSales equals to
        defaultDailyCashReconciliationFiltering("totalSales.equals=" + DEFAULT_TOTAL_SALES, "totalSales.equals=" + UPDATED_TOTAL_SALES);
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByTotalSalesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where totalSales in
        defaultDailyCashReconciliationFiltering(
            "totalSales.in=" + DEFAULT_TOTAL_SALES + "," + UPDATED_TOTAL_SALES,
            "totalSales.in=" + UPDATED_TOTAL_SALES
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByTotalSalesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where totalSales is not null
        defaultDailyCashReconciliationFiltering("totalSales.specified=true", "totalSales.specified=false");
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByTotalSalesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where totalSales is greater than or equal to
        defaultDailyCashReconciliationFiltering(
            "totalSales.greaterThanOrEqual=" + DEFAULT_TOTAL_SALES,
            "totalSales.greaterThanOrEqual=" + UPDATED_TOTAL_SALES
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByTotalSalesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where totalSales is less than or equal to
        defaultDailyCashReconciliationFiltering(
            "totalSales.lessThanOrEqual=" + DEFAULT_TOTAL_SALES,
            "totalSales.lessThanOrEqual=" + SMALLER_TOTAL_SALES
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByTotalSalesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where totalSales is less than
        defaultDailyCashReconciliationFiltering("totalSales.lessThan=" + UPDATED_TOTAL_SALES, "totalSales.lessThan=" + DEFAULT_TOTAL_SALES);
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByTotalSalesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where totalSales is greater than
        defaultDailyCashReconciliationFiltering(
            "totalSales.greaterThan=" + SMALLER_TOTAL_SALES,
            "totalSales.greaterThan=" + DEFAULT_TOTAL_SALES
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByTotalPurchasesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where totalPurchases equals to
        defaultDailyCashReconciliationFiltering(
            "totalPurchases.equals=" + DEFAULT_TOTAL_PURCHASES,
            "totalPurchases.equals=" + UPDATED_TOTAL_PURCHASES
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByTotalPurchasesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where totalPurchases in
        defaultDailyCashReconciliationFiltering(
            "totalPurchases.in=" + DEFAULT_TOTAL_PURCHASES + "," + UPDATED_TOTAL_PURCHASES,
            "totalPurchases.in=" + UPDATED_TOTAL_PURCHASES
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByTotalPurchasesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where totalPurchases is not null
        defaultDailyCashReconciliationFiltering("totalPurchases.specified=true", "totalPurchases.specified=false");
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByTotalPurchasesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where totalPurchases is greater than or equal to
        defaultDailyCashReconciliationFiltering(
            "totalPurchases.greaterThanOrEqual=" + DEFAULT_TOTAL_PURCHASES,
            "totalPurchases.greaterThanOrEqual=" + UPDATED_TOTAL_PURCHASES
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByTotalPurchasesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where totalPurchases is less than or equal to
        defaultDailyCashReconciliationFiltering(
            "totalPurchases.lessThanOrEqual=" + DEFAULT_TOTAL_PURCHASES,
            "totalPurchases.lessThanOrEqual=" + SMALLER_TOTAL_PURCHASES
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByTotalPurchasesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where totalPurchases is less than
        defaultDailyCashReconciliationFiltering(
            "totalPurchases.lessThan=" + UPDATED_TOTAL_PURCHASES,
            "totalPurchases.lessThan=" + DEFAULT_TOTAL_PURCHASES
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByTotalPurchasesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where totalPurchases is greater than
        defaultDailyCashReconciliationFiltering(
            "totalPurchases.greaterThan=" + SMALLER_TOTAL_PURCHASES,
            "totalPurchases.greaterThan=" + DEFAULT_TOTAL_PURCHASES
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByTotalSalaryPaidIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where totalSalaryPaid equals to
        defaultDailyCashReconciliationFiltering(
            "totalSalaryPaid.equals=" + DEFAULT_TOTAL_SALARY_PAID,
            "totalSalaryPaid.equals=" + UPDATED_TOTAL_SALARY_PAID
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByTotalSalaryPaidIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where totalSalaryPaid in
        defaultDailyCashReconciliationFiltering(
            "totalSalaryPaid.in=" + DEFAULT_TOTAL_SALARY_PAID + "," + UPDATED_TOTAL_SALARY_PAID,
            "totalSalaryPaid.in=" + UPDATED_TOTAL_SALARY_PAID
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByTotalSalaryPaidIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where totalSalaryPaid is not null
        defaultDailyCashReconciliationFiltering("totalSalaryPaid.specified=true", "totalSalaryPaid.specified=false");
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByTotalSalaryPaidIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where totalSalaryPaid is greater than or equal to
        defaultDailyCashReconciliationFiltering(
            "totalSalaryPaid.greaterThanOrEqual=" + DEFAULT_TOTAL_SALARY_PAID,
            "totalSalaryPaid.greaterThanOrEqual=" + UPDATED_TOTAL_SALARY_PAID
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByTotalSalaryPaidIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where totalSalaryPaid is less than or equal to
        defaultDailyCashReconciliationFiltering(
            "totalSalaryPaid.lessThanOrEqual=" + DEFAULT_TOTAL_SALARY_PAID,
            "totalSalaryPaid.lessThanOrEqual=" + SMALLER_TOTAL_SALARY_PAID
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByTotalSalaryPaidIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where totalSalaryPaid is less than
        defaultDailyCashReconciliationFiltering(
            "totalSalaryPaid.lessThan=" + UPDATED_TOTAL_SALARY_PAID,
            "totalSalaryPaid.lessThan=" + DEFAULT_TOTAL_SALARY_PAID
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByTotalSalaryPaidIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where totalSalaryPaid is greater than
        defaultDailyCashReconciliationFiltering(
            "totalSalaryPaid.greaterThan=" + SMALLER_TOTAL_SALARY_PAID,
            "totalSalaryPaid.greaterThan=" + DEFAULT_TOTAL_SALARY_PAID
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByOwnerDepositsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where ownerDeposits equals to
        defaultDailyCashReconciliationFiltering(
            "ownerDeposits.equals=" + DEFAULT_OWNER_DEPOSITS,
            "ownerDeposits.equals=" + UPDATED_OWNER_DEPOSITS
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByOwnerDepositsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where ownerDeposits in
        defaultDailyCashReconciliationFiltering(
            "ownerDeposits.in=" + DEFAULT_OWNER_DEPOSITS + "," + UPDATED_OWNER_DEPOSITS,
            "ownerDeposits.in=" + UPDATED_OWNER_DEPOSITS
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByOwnerDepositsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where ownerDeposits is not null
        defaultDailyCashReconciliationFiltering("ownerDeposits.specified=true", "ownerDeposits.specified=false");
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByOwnerDepositsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where ownerDeposits is greater than or equal to
        defaultDailyCashReconciliationFiltering(
            "ownerDeposits.greaterThanOrEqual=" + DEFAULT_OWNER_DEPOSITS,
            "ownerDeposits.greaterThanOrEqual=" + UPDATED_OWNER_DEPOSITS
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByOwnerDepositsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where ownerDeposits is less than or equal to
        defaultDailyCashReconciliationFiltering(
            "ownerDeposits.lessThanOrEqual=" + DEFAULT_OWNER_DEPOSITS,
            "ownerDeposits.lessThanOrEqual=" + SMALLER_OWNER_DEPOSITS
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByOwnerDepositsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where ownerDeposits is less than
        defaultDailyCashReconciliationFiltering(
            "ownerDeposits.lessThan=" + UPDATED_OWNER_DEPOSITS,
            "ownerDeposits.lessThan=" + DEFAULT_OWNER_DEPOSITS
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByOwnerDepositsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where ownerDeposits is greater than
        defaultDailyCashReconciliationFiltering(
            "ownerDeposits.greaterThan=" + SMALLER_OWNER_DEPOSITS,
            "ownerDeposits.greaterThan=" + DEFAULT_OWNER_DEPOSITS
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByWithdrawalsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where withdrawals equals to
        defaultDailyCashReconciliationFiltering("withdrawals.equals=" + DEFAULT_WITHDRAWALS, "withdrawals.equals=" + UPDATED_WITHDRAWALS);
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByWithdrawalsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where withdrawals in
        defaultDailyCashReconciliationFiltering(
            "withdrawals.in=" + DEFAULT_WITHDRAWALS + "," + UPDATED_WITHDRAWALS,
            "withdrawals.in=" + UPDATED_WITHDRAWALS
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByWithdrawalsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where withdrawals is not null
        defaultDailyCashReconciliationFiltering("withdrawals.specified=true", "withdrawals.specified=false");
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByWithdrawalsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where withdrawals is greater than or equal to
        defaultDailyCashReconciliationFiltering(
            "withdrawals.greaterThanOrEqual=" + DEFAULT_WITHDRAWALS,
            "withdrawals.greaterThanOrEqual=" + UPDATED_WITHDRAWALS
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByWithdrawalsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where withdrawals is less than or equal to
        defaultDailyCashReconciliationFiltering(
            "withdrawals.lessThanOrEqual=" + DEFAULT_WITHDRAWALS,
            "withdrawals.lessThanOrEqual=" + SMALLER_WITHDRAWALS
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByWithdrawalsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where withdrawals is less than
        defaultDailyCashReconciliationFiltering(
            "withdrawals.lessThan=" + UPDATED_WITHDRAWALS,
            "withdrawals.lessThan=" + DEFAULT_WITHDRAWALS
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByWithdrawalsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where withdrawals is greater than
        defaultDailyCashReconciliationFiltering(
            "withdrawals.greaterThan=" + SMALLER_WITHDRAWALS,
            "withdrawals.greaterThan=" + DEFAULT_WITHDRAWALS
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByClosingBalanceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where closingBalance equals to
        defaultDailyCashReconciliationFiltering(
            "closingBalance.equals=" + DEFAULT_CLOSING_BALANCE,
            "closingBalance.equals=" + UPDATED_CLOSING_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByClosingBalanceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where closingBalance in
        defaultDailyCashReconciliationFiltering(
            "closingBalance.in=" + DEFAULT_CLOSING_BALANCE + "," + UPDATED_CLOSING_BALANCE,
            "closingBalance.in=" + UPDATED_CLOSING_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByClosingBalanceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where closingBalance is not null
        defaultDailyCashReconciliationFiltering("closingBalance.specified=true", "closingBalance.specified=false");
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByClosingBalanceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where closingBalance is greater than or equal to
        defaultDailyCashReconciliationFiltering(
            "closingBalance.greaterThanOrEqual=" + DEFAULT_CLOSING_BALANCE,
            "closingBalance.greaterThanOrEqual=" + UPDATED_CLOSING_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByClosingBalanceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where closingBalance is less than or equal to
        defaultDailyCashReconciliationFiltering(
            "closingBalance.lessThanOrEqual=" + DEFAULT_CLOSING_BALANCE,
            "closingBalance.lessThanOrEqual=" + SMALLER_CLOSING_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByClosingBalanceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where closingBalance is less than
        defaultDailyCashReconciliationFiltering(
            "closingBalance.lessThan=" + UPDATED_CLOSING_BALANCE,
            "closingBalance.lessThan=" + DEFAULT_CLOSING_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByClosingBalanceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where closingBalance is greater than
        defaultDailyCashReconciliationFiltering(
            "closingBalance.greaterThan=" + SMALLER_CLOSING_BALANCE,
            "closingBalance.greaterThan=" + DEFAULT_CLOSING_BALANCE
        );
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where notes equals to
        defaultDailyCashReconciliationFiltering("notes.equals=" + DEFAULT_NOTES, "notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where notes in
        defaultDailyCashReconciliationFiltering("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES, "notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where notes is not null
        defaultDailyCashReconciliationFiltering("notes.specified=true", "notes.specified=false");
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByNotesContainsSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where notes contains
        defaultDailyCashReconciliationFiltering("notes.contains=" + DEFAULT_NOTES, "notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where notes does not contain
        defaultDailyCashReconciliationFiltering("notes.doesNotContain=" + UPDATED_NOTES, "notes.doesNotContain=" + DEFAULT_NOTES);
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where active equals to
        defaultDailyCashReconciliationFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where active in
        defaultDailyCashReconciliationFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllDailyCashReconciliationsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        // Get all the dailyCashReconciliationList where active is not null
        defaultDailyCashReconciliationFiltering("active.specified=true", "active.specified=false");
    }

    private void defaultDailyCashReconciliationFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDailyCashReconciliationShouldBeFound(shouldBeFound);
        defaultDailyCashReconciliationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDailyCashReconciliationShouldBeFound(String filter) throws Exception {
        restDailyCashReconciliationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dailyCashReconciliation.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].openingBalance").value(hasItem(sameNumber(DEFAULT_OPENING_BALANCE))))
            .andExpect(jsonPath("$.[*].totalSales").value(hasItem(sameNumber(DEFAULT_TOTAL_SALES))))
            .andExpect(jsonPath("$.[*].totalPurchases").value(hasItem(sameNumber(DEFAULT_TOTAL_PURCHASES))))
            .andExpect(jsonPath("$.[*].totalSalaryPaid").value(hasItem(sameNumber(DEFAULT_TOTAL_SALARY_PAID))))
            .andExpect(jsonPath("$.[*].ownerDeposits").value(hasItem(sameNumber(DEFAULT_OWNER_DEPOSITS))))
            .andExpect(jsonPath("$.[*].withdrawals").value(hasItem(sameNumber(DEFAULT_WITHDRAWALS))))
            .andExpect(jsonPath("$.[*].closingBalance").value(hasItem(sameNumber(DEFAULT_CLOSING_BALANCE))))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));

        // Check, that the count call also returns 1
        restDailyCashReconciliationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDailyCashReconciliationShouldNotBeFound(String filter) throws Exception {
        restDailyCashReconciliationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDailyCashReconciliationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDailyCashReconciliation() throws Exception {
        // Get the dailyCashReconciliation
        restDailyCashReconciliationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDailyCashReconciliation() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dailyCashReconciliation
        DailyCashReconciliation updatedDailyCashReconciliation = dailyCashReconciliationRepository
            .findById(dailyCashReconciliation.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedDailyCashReconciliation are not directly saved in db
        em.detach(updatedDailyCashReconciliation);
        updatedDailyCashReconciliation
            .date(UPDATED_DATE)
            .openingBalance(UPDATED_OPENING_BALANCE)
            .totalSales(UPDATED_TOTAL_SALES)
            .totalPurchases(UPDATED_TOTAL_PURCHASES)
            .totalSalaryPaid(UPDATED_TOTAL_SALARY_PAID)
            .ownerDeposits(UPDATED_OWNER_DEPOSITS)
            .withdrawals(UPDATED_WITHDRAWALS)
            .closingBalance(UPDATED_CLOSING_BALANCE)
            .notes(UPDATED_NOTES)
            .active(UPDATED_ACTIVE);
        DailyCashReconciliationDTO dailyCashReconciliationDTO = dailyCashReconciliationMapper.toDto(updatedDailyCashReconciliation);

        restDailyCashReconciliationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dailyCashReconciliationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(dailyCashReconciliationDTO))
            )
            .andExpect(status().isOk());

        // Validate the DailyCashReconciliation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDailyCashReconciliationToMatchAllProperties(updatedDailyCashReconciliation);
    }

    @Test
    @Transactional
    void putNonExistingDailyCashReconciliation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyCashReconciliation.setId(longCount.incrementAndGet());

        // Create the DailyCashReconciliation
        DailyCashReconciliationDTO dailyCashReconciliationDTO = dailyCashReconciliationMapper.toDto(dailyCashReconciliation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDailyCashReconciliationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dailyCashReconciliationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(dailyCashReconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DailyCashReconciliation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDailyCashReconciliation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyCashReconciliation.setId(longCount.incrementAndGet());

        // Create the DailyCashReconciliation
        DailyCashReconciliationDTO dailyCashReconciliationDTO = dailyCashReconciliationMapper.toDto(dailyCashReconciliation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDailyCashReconciliationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(dailyCashReconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DailyCashReconciliation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDailyCashReconciliation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyCashReconciliation.setId(longCount.incrementAndGet());

        // Create the DailyCashReconciliation
        DailyCashReconciliationDTO dailyCashReconciliationDTO = dailyCashReconciliationMapper.toDto(dailyCashReconciliation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDailyCashReconciliationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyCashReconciliationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DailyCashReconciliation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDailyCashReconciliationWithPatch() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dailyCashReconciliation using partial update
        DailyCashReconciliation partialUpdatedDailyCashReconciliation = new DailyCashReconciliation();
        partialUpdatedDailyCashReconciliation.setId(dailyCashReconciliation.getId());

        partialUpdatedDailyCashReconciliation
            .openingBalance(UPDATED_OPENING_BALANCE)
            .totalSales(UPDATED_TOTAL_SALES)
            .totalSalaryPaid(UPDATED_TOTAL_SALARY_PAID)
            .ownerDeposits(UPDATED_OWNER_DEPOSITS)
            .withdrawals(UPDATED_WITHDRAWALS)
            .active(UPDATED_ACTIVE);

        restDailyCashReconciliationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDailyCashReconciliation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDailyCashReconciliation))
            )
            .andExpect(status().isOk());

        // Validate the DailyCashReconciliation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDailyCashReconciliationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDailyCashReconciliation, dailyCashReconciliation),
            getPersistedDailyCashReconciliation(dailyCashReconciliation)
        );
    }

    @Test
    @Transactional
    void fullUpdateDailyCashReconciliationWithPatch() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dailyCashReconciliation using partial update
        DailyCashReconciliation partialUpdatedDailyCashReconciliation = new DailyCashReconciliation();
        partialUpdatedDailyCashReconciliation.setId(dailyCashReconciliation.getId());

        partialUpdatedDailyCashReconciliation
            .date(UPDATED_DATE)
            .openingBalance(UPDATED_OPENING_BALANCE)
            .totalSales(UPDATED_TOTAL_SALES)
            .totalPurchases(UPDATED_TOTAL_PURCHASES)
            .totalSalaryPaid(UPDATED_TOTAL_SALARY_PAID)
            .ownerDeposits(UPDATED_OWNER_DEPOSITS)
            .withdrawals(UPDATED_WITHDRAWALS)
            .closingBalance(UPDATED_CLOSING_BALANCE)
            .notes(UPDATED_NOTES)
            .active(UPDATED_ACTIVE);

        restDailyCashReconciliationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDailyCashReconciliation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDailyCashReconciliation))
            )
            .andExpect(status().isOk());

        // Validate the DailyCashReconciliation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDailyCashReconciliationUpdatableFieldsEquals(
            partialUpdatedDailyCashReconciliation,
            getPersistedDailyCashReconciliation(partialUpdatedDailyCashReconciliation)
        );
    }

    @Test
    @Transactional
    void patchNonExistingDailyCashReconciliation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyCashReconciliation.setId(longCount.incrementAndGet());

        // Create the DailyCashReconciliation
        DailyCashReconciliationDTO dailyCashReconciliationDTO = dailyCashReconciliationMapper.toDto(dailyCashReconciliation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDailyCashReconciliationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dailyCashReconciliationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(dailyCashReconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DailyCashReconciliation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDailyCashReconciliation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyCashReconciliation.setId(longCount.incrementAndGet());

        // Create the DailyCashReconciliation
        DailyCashReconciliationDTO dailyCashReconciliationDTO = dailyCashReconciliationMapper.toDto(dailyCashReconciliation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDailyCashReconciliationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(dailyCashReconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DailyCashReconciliation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDailyCashReconciliation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyCashReconciliation.setId(longCount.incrementAndGet());

        // Create the DailyCashReconciliation
        DailyCashReconciliationDTO dailyCashReconciliationDTO = dailyCashReconciliationMapper.toDto(dailyCashReconciliation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDailyCashReconciliationMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(dailyCashReconciliationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DailyCashReconciliation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDailyCashReconciliation() throws Exception {
        // Initialize the database
        insertedDailyCashReconciliation = dailyCashReconciliationRepository.saveAndFlush(dailyCashReconciliation);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the dailyCashReconciliation
        restDailyCashReconciliationMockMvc
            .perform(delete(ENTITY_API_URL_ID, dailyCashReconciliation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return dailyCashReconciliationRepository.count();
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

    protected DailyCashReconciliation getPersistedDailyCashReconciliation(DailyCashReconciliation dailyCashReconciliation) {
        return dailyCashReconciliationRepository.findById(dailyCashReconciliation.getId()).orElseThrow();
    }

    protected void assertPersistedDailyCashReconciliationToMatchAllProperties(DailyCashReconciliation expectedDailyCashReconciliation) {
        assertDailyCashReconciliationAllPropertiesEquals(
            expectedDailyCashReconciliation,
            getPersistedDailyCashReconciliation(expectedDailyCashReconciliation)
        );
    }

    protected void assertPersistedDailyCashReconciliationToMatchUpdatableProperties(
        DailyCashReconciliation expectedDailyCashReconciliation
    ) {
        assertDailyCashReconciliationAllUpdatablePropertiesEquals(
            expectedDailyCashReconciliation,
            getPersistedDailyCashReconciliation(expectedDailyCashReconciliation)
        );
    }
}
