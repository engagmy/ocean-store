package com.agamy.inventory.web.rest;

import static com.agamy.inventory.domain.DailyCashDetailAsserts.*;
import static com.agamy.inventory.web.rest.TestUtil.createUpdateProxyForBean;
import static com.agamy.inventory.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.agamy.inventory.IntegrationTest;
import com.agamy.inventory.domain.DailyCashDetail;
import com.agamy.inventory.domain.DailyCashReconciliation;
import com.agamy.inventory.domain.enumeration.CashTransactionType;
import com.agamy.inventory.repository.DailyCashDetailRepository;
import com.agamy.inventory.service.dto.DailyCashDetailDTO;
import com.agamy.inventory.service.mapper.DailyCashDetailMapper;
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
 * Integration tests for the {@link DailyCashDetailResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DailyCashDetailResourceIT {

    private static final CashTransactionType DEFAULT_TYPE = CashTransactionType.OWNER_DEPOSIT;
    private static final CashTransactionType UPDATED_TYPE = CashTransactionType.OWNER_WITHDRAWAL;

    private static final Long DEFAULT_REFERENCE_ID = 1L;
    private static final Long UPDATED_REFERENCE_ID = 2L;
    private static final Long SMALLER_REFERENCE_ID = 1L - 1L;

    private static final String DEFAULT_REFERENCE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE_TYPE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT = new BigDecimal(1 - 1);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_TIMESTAMP = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/daily-cash-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DailyCashDetailRepository dailyCashDetailRepository;

    @Autowired
    private DailyCashDetailMapper dailyCashDetailMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDailyCashDetailMockMvc;

    private DailyCashDetail dailyCashDetail;

    private DailyCashDetail insertedDailyCashDetail;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DailyCashDetail createEntity() {
        return new DailyCashDetail()
            .type(DEFAULT_TYPE)
            .referenceId(DEFAULT_REFERENCE_ID)
            .referenceType(DEFAULT_REFERENCE_TYPE)
            .amount(DEFAULT_AMOUNT)
            .description(DEFAULT_DESCRIPTION)
            .timestamp(DEFAULT_TIMESTAMP)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DailyCashDetail createUpdatedEntity() {
        return new DailyCashDetail()
            .type(UPDATED_TYPE)
            .referenceId(UPDATED_REFERENCE_ID)
            .referenceType(UPDATED_REFERENCE_TYPE)
            .amount(UPDATED_AMOUNT)
            .description(UPDATED_DESCRIPTION)
            .timestamp(UPDATED_TIMESTAMP)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        dailyCashDetail = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDailyCashDetail != null) {
            dailyCashDetailRepository.delete(insertedDailyCashDetail);
            insertedDailyCashDetail = null;
        }
    }

    @Test
    @Transactional
    void createDailyCashDetail() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DailyCashDetail
        DailyCashDetailDTO dailyCashDetailDTO = dailyCashDetailMapper.toDto(dailyCashDetail);
        var returnedDailyCashDetailDTO = om.readValue(
            restDailyCashDetailMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyCashDetailDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DailyCashDetailDTO.class
        );

        // Validate the DailyCashDetail in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDailyCashDetail = dailyCashDetailMapper.toEntity(returnedDailyCashDetailDTO);
        assertDailyCashDetailUpdatableFieldsEquals(returnedDailyCashDetail, getPersistedDailyCashDetail(returnedDailyCashDetail));

        insertedDailyCashDetail = returnedDailyCashDetail;
    }

    @Test
    @Transactional
    void createDailyCashDetailWithExistingId() throws Exception {
        // Create the DailyCashDetail with an existing ID
        dailyCashDetail.setId(1L);
        DailyCashDetailDTO dailyCashDetailDTO = dailyCashDetailMapper.toDto(dailyCashDetail);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDailyCashDetailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyCashDetailDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DailyCashDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dailyCashDetail.setType(null);

        // Create the DailyCashDetail, which fails.
        DailyCashDetailDTO dailyCashDetailDTO = dailyCashDetailMapper.toDto(dailyCashDetail);

        restDailyCashDetailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyCashDetailDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReferenceIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dailyCashDetail.setReferenceId(null);

        // Create the DailyCashDetail, which fails.
        DailyCashDetailDTO dailyCashDetailDTO = dailyCashDetailMapper.toDto(dailyCashDetail);

        restDailyCashDetailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyCashDetailDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReferenceTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dailyCashDetail.setReferenceType(null);

        // Create the DailyCashDetail, which fails.
        DailyCashDetailDTO dailyCashDetailDTO = dailyCashDetailMapper.toDto(dailyCashDetail);

        restDailyCashDetailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyCashDetailDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dailyCashDetail.setAmount(null);

        // Create the DailyCashDetail, which fails.
        DailyCashDetailDTO dailyCashDetailDTO = dailyCashDetailMapper.toDto(dailyCashDetail);

        restDailyCashDetailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyCashDetailDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTimestampIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dailyCashDetail.setTimestamp(null);

        // Create the DailyCashDetail, which fails.
        DailyCashDetailDTO dailyCashDetailDTO = dailyCashDetailMapper.toDto(dailyCashDetail);

        restDailyCashDetailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyCashDetailDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDailyCashDetails() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList
        restDailyCashDetailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dailyCashDetail.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].referenceId").value(hasItem(DEFAULT_REFERENCE_ID.intValue())))
            .andExpect(jsonPath("$.[*].referenceType").value(hasItem(DEFAULT_REFERENCE_TYPE)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getDailyCashDetail() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get the dailyCashDetail
        restDailyCashDetailMockMvc
            .perform(get(ENTITY_API_URL_ID, dailyCashDetail.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dailyCashDetail.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.referenceId").value(DEFAULT_REFERENCE_ID.intValue()))
            .andExpect(jsonPath("$.referenceType").value(DEFAULT_REFERENCE_TYPE))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP.toString()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getDailyCashDetailsByIdFiltering() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        Long id = dailyCashDetail.getId();

        defaultDailyCashDetailFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDailyCashDetailFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDailyCashDetailFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where type equals to
        defaultDailyCashDetailFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where type in
        defaultDailyCashDetailFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where type is not null
        defaultDailyCashDetailFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByReferenceIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where referenceId equals to
        defaultDailyCashDetailFiltering("referenceId.equals=" + DEFAULT_REFERENCE_ID, "referenceId.equals=" + UPDATED_REFERENCE_ID);
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByReferenceIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where referenceId in
        defaultDailyCashDetailFiltering(
            "referenceId.in=" + DEFAULT_REFERENCE_ID + "," + UPDATED_REFERENCE_ID,
            "referenceId.in=" + UPDATED_REFERENCE_ID
        );
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByReferenceIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where referenceId is not null
        defaultDailyCashDetailFiltering("referenceId.specified=true", "referenceId.specified=false");
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByReferenceIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where referenceId is greater than or equal to
        defaultDailyCashDetailFiltering(
            "referenceId.greaterThanOrEqual=" + DEFAULT_REFERENCE_ID,
            "referenceId.greaterThanOrEqual=" + UPDATED_REFERENCE_ID
        );
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByReferenceIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where referenceId is less than or equal to
        defaultDailyCashDetailFiltering(
            "referenceId.lessThanOrEqual=" + DEFAULT_REFERENCE_ID,
            "referenceId.lessThanOrEqual=" + SMALLER_REFERENCE_ID
        );
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByReferenceIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where referenceId is less than
        defaultDailyCashDetailFiltering("referenceId.lessThan=" + UPDATED_REFERENCE_ID, "referenceId.lessThan=" + DEFAULT_REFERENCE_ID);
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByReferenceIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where referenceId is greater than
        defaultDailyCashDetailFiltering(
            "referenceId.greaterThan=" + SMALLER_REFERENCE_ID,
            "referenceId.greaterThan=" + DEFAULT_REFERENCE_ID
        );
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByReferenceTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where referenceType equals to
        defaultDailyCashDetailFiltering("referenceType.equals=" + DEFAULT_REFERENCE_TYPE, "referenceType.equals=" + UPDATED_REFERENCE_TYPE);
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByReferenceTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where referenceType in
        defaultDailyCashDetailFiltering(
            "referenceType.in=" + DEFAULT_REFERENCE_TYPE + "," + UPDATED_REFERENCE_TYPE,
            "referenceType.in=" + UPDATED_REFERENCE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByReferenceTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where referenceType is not null
        defaultDailyCashDetailFiltering("referenceType.specified=true", "referenceType.specified=false");
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByReferenceTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where referenceType contains
        defaultDailyCashDetailFiltering(
            "referenceType.contains=" + DEFAULT_REFERENCE_TYPE,
            "referenceType.contains=" + UPDATED_REFERENCE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByReferenceTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where referenceType does not contain
        defaultDailyCashDetailFiltering(
            "referenceType.doesNotContain=" + UPDATED_REFERENCE_TYPE,
            "referenceType.doesNotContain=" + DEFAULT_REFERENCE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where amount equals to
        defaultDailyCashDetailFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where amount in
        defaultDailyCashDetailFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where amount is not null
        defaultDailyCashDetailFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where amount is greater than or equal to
        defaultDailyCashDetailFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where amount is less than or equal to
        defaultDailyCashDetailFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where amount is less than
        defaultDailyCashDetailFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where amount is greater than
        defaultDailyCashDetailFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where description equals to
        defaultDailyCashDetailFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where description in
        defaultDailyCashDetailFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where description is not null
        defaultDailyCashDetailFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where description contains
        defaultDailyCashDetailFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where description does not contain
        defaultDailyCashDetailFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where timestamp equals to
        defaultDailyCashDetailFiltering("timestamp.equals=" + DEFAULT_TIMESTAMP, "timestamp.equals=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where timestamp in
        defaultDailyCashDetailFiltering("timestamp.in=" + DEFAULT_TIMESTAMP + "," + UPDATED_TIMESTAMP, "timestamp.in=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where timestamp is not null
        defaultDailyCashDetailFiltering("timestamp.specified=true", "timestamp.specified=false");
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where active equals to
        defaultDailyCashDetailFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where active in
        defaultDailyCashDetailFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        // Get all the dailyCashDetailList where active is not null
        defaultDailyCashDetailFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllDailyCashDetailsByDailyCashReconciliationIsEqualToSomething() throws Exception {
        DailyCashReconciliation dailyCashReconciliation;
        if (TestUtil.findAll(em, DailyCashReconciliation.class).isEmpty()) {
            dailyCashDetailRepository.saveAndFlush(dailyCashDetail);
            dailyCashReconciliation = DailyCashReconciliationResourceIT.createEntity();
        } else {
            dailyCashReconciliation = TestUtil.findAll(em, DailyCashReconciliation.class).get(0);
        }
        em.persist(dailyCashReconciliation);
        em.flush();
        dailyCashDetail.setDailyCashReconciliation(dailyCashReconciliation);
        dailyCashDetailRepository.saveAndFlush(dailyCashDetail);
        Long dailyCashReconciliationId = dailyCashReconciliation.getId();
        // Get all the dailyCashDetailList where dailyCashReconciliation equals to dailyCashReconciliationId
        defaultDailyCashDetailShouldBeFound("dailyCashReconciliationId.equals=" + dailyCashReconciliationId);

        // Get all the dailyCashDetailList where dailyCashReconciliation equals to (dailyCashReconciliationId + 1)
        defaultDailyCashDetailShouldNotBeFound("dailyCashReconciliationId.equals=" + (dailyCashReconciliationId + 1));
    }

    private void defaultDailyCashDetailFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDailyCashDetailShouldBeFound(shouldBeFound);
        defaultDailyCashDetailShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDailyCashDetailShouldBeFound(String filter) throws Exception {
        restDailyCashDetailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dailyCashDetail.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].referenceId").value(hasItem(DEFAULT_REFERENCE_ID.intValue())))
            .andExpect(jsonPath("$.[*].referenceType").value(hasItem(DEFAULT_REFERENCE_TYPE)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));

        // Check, that the count call also returns 1
        restDailyCashDetailMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDailyCashDetailShouldNotBeFound(String filter) throws Exception {
        restDailyCashDetailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDailyCashDetailMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDailyCashDetail() throws Exception {
        // Get the dailyCashDetail
        restDailyCashDetailMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDailyCashDetail() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dailyCashDetail
        DailyCashDetail updatedDailyCashDetail = dailyCashDetailRepository.findById(dailyCashDetail.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDailyCashDetail are not directly saved in db
        em.detach(updatedDailyCashDetail);
        updatedDailyCashDetail
            .type(UPDATED_TYPE)
            .referenceId(UPDATED_REFERENCE_ID)
            .referenceType(UPDATED_REFERENCE_TYPE)
            .amount(UPDATED_AMOUNT)
            .description(UPDATED_DESCRIPTION)
            .timestamp(UPDATED_TIMESTAMP)
            .active(UPDATED_ACTIVE);
        DailyCashDetailDTO dailyCashDetailDTO = dailyCashDetailMapper.toDto(updatedDailyCashDetail);

        restDailyCashDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dailyCashDetailDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(dailyCashDetailDTO))
            )
            .andExpect(status().isOk());

        // Validate the DailyCashDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDailyCashDetailToMatchAllProperties(updatedDailyCashDetail);
    }

    @Test
    @Transactional
    void putNonExistingDailyCashDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyCashDetail.setId(longCount.incrementAndGet());

        // Create the DailyCashDetail
        DailyCashDetailDTO dailyCashDetailDTO = dailyCashDetailMapper.toDto(dailyCashDetail);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDailyCashDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dailyCashDetailDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(dailyCashDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DailyCashDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDailyCashDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyCashDetail.setId(longCount.incrementAndGet());

        // Create the DailyCashDetail
        DailyCashDetailDTO dailyCashDetailDTO = dailyCashDetailMapper.toDto(dailyCashDetail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDailyCashDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(dailyCashDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DailyCashDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDailyCashDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyCashDetail.setId(longCount.incrementAndGet());

        // Create the DailyCashDetail
        DailyCashDetailDTO dailyCashDetailDTO = dailyCashDetailMapper.toDto(dailyCashDetail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDailyCashDetailMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyCashDetailDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DailyCashDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDailyCashDetailWithPatch() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dailyCashDetail using partial update
        DailyCashDetail partialUpdatedDailyCashDetail = new DailyCashDetail();
        partialUpdatedDailyCashDetail.setId(dailyCashDetail.getId());

        partialUpdatedDailyCashDetail.type(UPDATED_TYPE).amount(UPDATED_AMOUNT).active(UPDATED_ACTIVE);

        restDailyCashDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDailyCashDetail.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDailyCashDetail))
            )
            .andExpect(status().isOk());

        // Validate the DailyCashDetail in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDailyCashDetailUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDailyCashDetail, dailyCashDetail),
            getPersistedDailyCashDetail(dailyCashDetail)
        );
    }

    @Test
    @Transactional
    void fullUpdateDailyCashDetailWithPatch() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dailyCashDetail using partial update
        DailyCashDetail partialUpdatedDailyCashDetail = new DailyCashDetail();
        partialUpdatedDailyCashDetail.setId(dailyCashDetail.getId());

        partialUpdatedDailyCashDetail
            .type(UPDATED_TYPE)
            .referenceId(UPDATED_REFERENCE_ID)
            .referenceType(UPDATED_REFERENCE_TYPE)
            .amount(UPDATED_AMOUNT)
            .description(UPDATED_DESCRIPTION)
            .timestamp(UPDATED_TIMESTAMP)
            .active(UPDATED_ACTIVE);

        restDailyCashDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDailyCashDetail.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDailyCashDetail))
            )
            .andExpect(status().isOk());

        // Validate the DailyCashDetail in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDailyCashDetailUpdatableFieldsEquals(
            partialUpdatedDailyCashDetail,
            getPersistedDailyCashDetail(partialUpdatedDailyCashDetail)
        );
    }

    @Test
    @Transactional
    void patchNonExistingDailyCashDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyCashDetail.setId(longCount.incrementAndGet());

        // Create the DailyCashDetail
        DailyCashDetailDTO dailyCashDetailDTO = dailyCashDetailMapper.toDto(dailyCashDetail);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDailyCashDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dailyCashDetailDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(dailyCashDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DailyCashDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDailyCashDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyCashDetail.setId(longCount.incrementAndGet());

        // Create the DailyCashDetail
        DailyCashDetailDTO dailyCashDetailDTO = dailyCashDetailMapper.toDto(dailyCashDetail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDailyCashDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(dailyCashDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DailyCashDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDailyCashDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyCashDetail.setId(longCount.incrementAndGet());

        // Create the DailyCashDetail
        DailyCashDetailDTO dailyCashDetailDTO = dailyCashDetailMapper.toDto(dailyCashDetail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDailyCashDetailMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(dailyCashDetailDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DailyCashDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDailyCashDetail() throws Exception {
        // Initialize the database
        insertedDailyCashDetail = dailyCashDetailRepository.saveAndFlush(dailyCashDetail);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the dailyCashDetail
        restDailyCashDetailMockMvc
            .perform(delete(ENTITY_API_URL_ID, dailyCashDetail.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return dailyCashDetailRepository.count();
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

    protected DailyCashDetail getPersistedDailyCashDetail(DailyCashDetail dailyCashDetail) {
        return dailyCashDetailRepository.findById(dailyCashDetail.getId()).orElseThrow();
    }

    protected void assertPersistedDailyCashDetailToMatchAllProperties(DailyCashDetail expectedDailyCashDetail) {
        assertDailyCashDetailAllPropertiesEquals(expectedDailyCashDetail, getPersistedDailyCashDetail(expectedDailyCashDetail));
    }

    protected void assertPersistedDailyCashDetailToMatchUpdatableProperties(DailyCashDetail expectedDailyCashDetail) {
        assertDailyCashDetailAllUpdatablePropertiesEquals(expectedDailyCashDetail, getPersistedDailyCashDetail(expectedDailyCashDetail));
    }
}
