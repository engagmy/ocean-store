package com.agamy.inventory.web.rest;

import static com.agamy.inventory.domain.CashTransactionAsserts.*;
import static com.agamy.inventory.web.rest.TestUtil.createUpdateProxyForBean;
import static com.agamy.inventory.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.agamy.inventory.IntegrationTest;
import com.agamy.inventory.domain.CashTransaction;
import com.agamy.inventory.domain.enumeration.CashTransactionType;
import com.agamy.inventory.repository.CashTransactionRepository;
import com.agamy.inventory.service.dto.CashTransactionDTO;
import com.agamy.inventory.service.mapper.CashTransactionMapper;
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
 * Integration tests for the {@link CashTransactionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CashTransactionResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT = new BigDecimal(1 - 1);

    private static final CashTransactionType DEFAULT_TYPE = CashTransactionType.OWNER_DEPOSIT;
    private static final CashTransactionType UPDATED_TYPE = CashTransactionType.OWNER_WITHDRAWAL;

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/cash-transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CashTransactionRepository cashTransactionRepository;

    @Autowired
    private CashTransactionMapper cashTransactionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCashTransactionMockMvc;

    private CashTransaction cashTransaction;

    private CashTransaction insertedCashTransaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CashTransaction createEntity() {
        return new CashTransaction()
            .date(DEFAULT_DATE)
            .amount(DEFAULT_AMOUNT)
            .type(DEFAULT_TYPE)
            .reason(DEFAULT_REASON)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CashTransaction createUpdatedEntity() {
        return new CashTransaction()
            .date(UPDATED_DATE)
            .amount(UPDATED_AMOUNT)
            .type(UPDATED_TYPE)
            .reason(UPDATED_REASON)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        cashTransaction = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCashTransaction != null) {
            cashTransactionRepository.delete(insertedCashTransaction);
            insertedCashTransaction = null;
        }
    }

    @Test
    @Transactional
    void createCashTransaction() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CashTransaction
        CashTransactionDTO cashTransactionDTO = cashTransactionMapper.toDto(cashTransaction);
        var returnedCashTransactionDTO = om.readValue(
            restCashTransactionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashTransactionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CashTransactionDTO.class
        );

        // Validate the CashTransaction in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCashTransaction = cashTransactionMapper.toEntity(returnedCashTransactionDTO);
        assertCashTransactionUpdatableFieldsEquals(returnedCashTransaction, getPersistedCashTransaction(returnedCashTransaction));

        insertedCashTransaction = returnedCashTransaction;
    }

    @Test
    @Transactional
    void createCashTransactionWithExistingId() throws Exception {
        // Create the CashTransaction with an existing ID
        cashTransaction.setId(1L);
        CashTransactionDTO cashTransactionDTO = cashTransactionMapper.toDto(cashTransaction);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCashTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashTransactionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CashTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cashTransaction.setDate(null);

        // Create the CashTransaction, which fails.
        CashTransactionDTO cashTransactionDTO = cashTransactionMapper.toDto(cashTransaction);

        restCashTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashTransactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cashTransaction.setAmount(null);

        // Create the CashTransaction, which fails.
        CashTransactionDTO cashTransactionDTO = cashTransactionMapper.toDto(cashTransaction);

        restCashTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashTransactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cashTransaction.setType(null);

        // Create the CashTransaction, which fails.
        CashTransactionDTO cashTransactionDTO = cashTransactionMapper.toDto(cashTransaction);

        restCashTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashTransactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCashTransactions() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get all the cashTransactionList
        restCashTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cashTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getCashTransaction() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get the cashTransaction
        restCashTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, cashTransaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cashTransaction.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getCashTransactionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        Long id = cashTransaction.getId();

        defaultCashTransactionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCashTransactionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCashTransactionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCashTransactionsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get all the cashTransactionList where date equals to
        defaultCashTransactionFiltering("date.equals=" + DEFAULT_DATE, "date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllCashTransactionsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get all the cashTransactionList where date in
        defaultCashTransactionFiltering("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE, "date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllCashTransactionsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get all the cashTransactionList where date is not null
        defaultCashTransactionFiltering("date.specified=true", "date.specified=false");
    }

    @Test
    @Transactional
    void getAllCashTransactionsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get all the cashTransactionList where amount equals to
        defaultCashTransactionFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCashTransactionsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get all the cashTransactionList where amount in
        defaultCashTransactionFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCashTransactionsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get all the cashTransactionList where amount is not null
        defaultCashTransactionFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllCashTransactionsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get all the cashTransactionList where amount is greater than or equal to
        defaultCashTransactionFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCashTransactionsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get all the cashTransactionList where amount is less than or equal to
        defaultCashTransactionFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCashTransactionsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get all the cashTransactionList where amount is less than
        defaultCashTransactionFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCashTransactionsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get all the cashTransactionList where amount is greater than
        defaultCashTransactionFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCashTransactionsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get all the cashTransactionList where type equals to
        defaultCashTransactionFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllCashTransactionsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get all the cashTransactionList where type in
        defaultCashTransactionFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllCashTransactionsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get all the cashTransactionList where type is not null
        defaultCashTransactionFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllCashTransactionsByReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get all the cashTransactionList where reason equals to
        defaultCashTransactionFiltering("reason.equals=" + DEFAULT_REASON, "reason.equals=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllCashTransactionsByReasonIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get all the cashTransactionList where reason in
        defaultCashTransactionFiltering("reason.in=" + DEFAULT_REASON + "," + UPDATED_REASON, "reason.in=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllCashTransactionsByReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get all the cashTransactionList where reason is not null
        defaultCashTransactionFiltering("reason.specified=true", "reason.specified=false");
    }

    @Test
    @Transactional
    void getAllCashTransactionsByReasonContainsSomething() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get all the cashTransactionList where reason contains
        defaultCashTransactionFiltering("reason.contains=" + DEFAULT_REASON, "reason.contains=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllCashTransactionsByReasonNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get all the cashTransactionList where reason does not contain
        defaultCashTransactionFiltering("reason.doesNotContain=" + UPDATED_REASON, "reason.doesNotContain=" + DEFAULT_REASON);
    }

    @Test
    @Transactional
    void getAllCashTransactionsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get all the cashTransactionList where active equals to
        defaultCashTransactionFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllCashTransactionsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get all the cashTransactionList where active in
        defaultCashTransactionFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllCashTransactionsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        // Get all the cashTransactionList where active is not null
        defaultCashTransactionFiltering("active.specified=true", "active.specified=false");
    }

    private void defaultCashTransactionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCashTransactionShouldBeFound(shouldBeFound);
        defaultCashTransactionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCashTransactionShouldBeFound(String filter) throws Exception {
        restCashTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cashTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));

        // Check, that the count call also returns 1
        restCashTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCashTransactionShouldNotBeFound(String filter) throws Exception {
        restCashTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCashTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCashTransaction() throws Exception {
        // Get the cashTransaction
        restCashTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCashTransaction() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cashTransaction
        CashTransaction updatedCashTransaction = cashTransactionRepository.findById(cashTransaction.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCashTransaction are not directly saved in db
        em.detach(updatedCashTransaction);
        updatedCashTransaction.date(UPDATED_DATE).amount(UPDATED_AMOUNT).type(UPDATED_TYPE).reason(UPDATED_REASON).active(UPDATED_ACTIVE);
        CashTransactionDTO cashTransactionDTO = cashTransactionMapper.toDto(updatedCashTransaction);

        restCashTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cashTransactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cashTransactionDTO))
            )
            .andExpect(status().isOk());

        // Validate the CashTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCashTransactionToMatchAllProperties(updatedCashTransaction);
    }

    @Test
    @Transactional
    void putNonExistingCashTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashTransaction.setId(longCount.incrementAndGet());

        // Create the CashTransaction
        CashTransactionDTO cashTransactionDTO = cashTransactionMapper.toDto(cashTransaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCashTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cashTransactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cashTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CashTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCashTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashTransaction.setId(longCount.incrementAndGet());

        // Create the CashTransaction
        CashTransactionDTO cashTransactionDTO = cashTransactionMapper.toDto(cashTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCashTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cashTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CashTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCashTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashTransaction.setId(longCount.incrementAndGet());

        // Create the CashTransaction
        CashTransactionDTO cashTransactionDTO = cashTransactionMapper.toDto(cashTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCashTransactionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashTransactionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CashTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCashTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cashTransaction using partial update
        CashTransaction partialUpdatedCashTransaction = new CashTransaction();
        partialUpdatedCashTransaction.setId(cashTransaction.getId());

        partialUpdatedCashTransaction.date(UPDATED_DATE).active(UPDATED_ACTIVE);

        restCashTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCashTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCashTransaction))
            )
            .andExpect(status().isOk());

        // Validate the CashTransaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCashTransactionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCashTransaction, cashTransaction),
            getPersistedCashTransaction(cashTransaction)
        );
    }

    @Test
    @Transactional
    void fullUpdateCashTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cashTransaction using partial update
        CashTransaction partialUpdatedCashTransaction = new CashTransaction();
        partialUpdatedCashTransaction.setId(cashTransaction.getId());

        partialUpdatedCashTransaction
            .date(UPDATED_DATE)
            .amount(UPDATED_AMOUNT)
            .type(UPDATED_TYPE)
            .reason(UPDATED_REASON)
            .active(UPDATED_ACTIVE);

        restCashTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCashTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCashTransaction))
            )
            .andExpect(status().isOk());

        // Validate the CashTransaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCashTransactionUpdatableFieldsEquals(
            partialUpdatedCashTransaction,
            getPersistedCashTransaction(partialUpdatedCashTransaction)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCashTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashTransaction.setId(longCount.incrementAndGet());

        // Create the CashTransaction
        CashTransactionDTO cashTransactionDTO = cashTransactionMapper.toDto(cashTransaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCashTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cashTransactionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cashTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CashTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCashTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashTransaction.setId(longCount.incrementAndGet());

        // Create the CashTransaction
        CashTransactionDTO cashTransactionDTO = cashTransactionMapper.toDto(cashTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCashTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cashTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CashTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCashTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashTransaction.setId(longCount.incrementAndGet());

        // Create the CashTransaction
        CashTransactionDTO cashTransactionDTO = cashTransactionMapper.toDto(cashTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCashTransactionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(cashTransactionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CashTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCashTransaction() throws Exception {
        // Initialize the database
        insertedCashTransaction = cashTransactionRepository.saveAndFlush(cashTransaction);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the cashTransaction
        restCashTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, cashTransaction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return cashTransactionRepository.count();
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

    protected CashTransaction getPersistedCashTransaction(CashTransaction cashTransaction) {
        return cashTransactionRepository.findById(cashTransaction.getId()).orElseThrow();
    }

    protected void assertPersistedCashTransactionToMatchAllProperties(CashTransaction expectedCashTransaction) {
        assertCashTransactionAllPropertiesEquals(expectedCashTransaction, getPersistedCashTransaction(expectedCashTransaction));
    }

    protected void assertPersistedCashTransactionToMatchUpdatableProperties(CashTransaction expectedCashTransaction) {
        assertCashTransactionAllUpdatablePropertiesEquals(expectedCashTransaction, getPersistedCashTransaction(expectedCashTransaction));
    }
}
