package com.agamy.inventory.web.rest;

import static com.agamy.inventory.domain.CashBalanceAsserts.*;
import static com.agamy.inventory.web.rest.TestUtil.createUpdateProxyForBean;
import static com.agamy.inventory.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.agamy.inventory.IntegrationTest;
import com.agamy.inventory.domain.CashBalance;
import com.agamy.inventory.repository.CashBalanceRepository;
import com.agamy.inventory.service.dto.CashBalanceDTO;
import com.agamy.inventory.service.mapper.CashBalanceMapper;
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
 * Integration tests for the {@link CashBalanceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CashBalanceResourceIT {

    private static final BigDecimal DEFAULT_AVAILABLE = new BigDecimal(1);
    private static final BigDecimal UPDATED_AVAILABLE = new BigDecimal(2);
    private static final BigDecimal SMALLER_AVAILABLE = new BigDecimal(1 - 1);

    private static final Instant DEFAULT_LAST_UPDATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_UPDATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/cash-balances";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CashBalanceRepository cashBalanceRepository;

    @Autowired
    private CashBalanceMapper cashBalanceMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCashBalanceMockMvc;

    private CashBalance cashBalance;

    private CashBalance insertedCashBalance;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CashBalance createEntity() {
        return new CashBalance().available(DEFAULT_AVAILABLE).lastUpdated(DEFAULT_LAST_UPDATED).notes(DEFAULT_NOTES).active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CashBalance createUpdatedEntity() {
        return new CashBalance().available(UPDATED_AVAILABLE).lastUpdated(UPDATED_LAST_UPDATED).notes(UPDATED_NOTES).active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        cashBalance = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCashBalance != null) {
            cashBalanceRepository.delete(insertedCashBalance);
            insertedCashBalance = null;
        }
    }

    @Test
    @Transactional
    void createCashBalance() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CashBalance
        CashBalanceDTO cashBalanceDTO = cashBalanceMapper.toDto(cashBalance);
        var returnedCashBalanceDTO = om.readValue(
            restCashBalanceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashBalanceDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CashBalanceDTO.class
        );

        // Validate the CashBalance in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCashBalance = cashBalanceMapper.toEntity(returnedCashBalanceDTO);
        assertCashBalanceUpdatableFieldsEquals(returnedCashBalance, getPersistedCashBalance(returnedCashBalance));

        insertedCashBalance = returnedCashBalance;
    }

    @Test
    @Transactional
    void createCashBalanceWithExistingId() throws Exception {
        // Create the CashBalance with an existing ID
        cashBalance.setId(1L);
        CashBalanceDTO cashBalanceDTO = cashBalanceMapper.toDto(cashBalance);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCashBalanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashBalanceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CashBalance in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAvailableIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cashBalance.setAvailable(null);

        // Create the CashBalance, which fails.
        CashBalanceDTO cashBalanceDTO = cashBalanceMapper.toDto(cashBalance);

        restCashBalanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashBalanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCashBalances() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        // Get all the cashBalanceList
        restCashBalanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cashBalance.getId().intValue())))
            .andExpect(jsonPath("$.[*].available").value(hasItem(sameNumber(DEFAULT_AVAILABLE))))
            .andExpect(jsonPath("$.[*].lastUpdated").value(hasItem(DEFAULT_LAST_UPDATED.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getCashBalance() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        // Get the cashBalance
        restCashBalanceMockMvc
            .perform(get(ENTITY_API_URL_ID, cashBalance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cashBalance.getId().intValue()))
            .andExpect(jsonPath("$.available").value(sameNumber(DEFAULT_AVAILABLE)))
            .andExpect(jsonPath("$.lastUpdated").value(DEFAULT_LAST_UPDATED.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getCashBalancesByIdFiltering() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        Long id = cashBalance.getId();

        defaultCashBalanceFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCashBalanceFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCashBalanceFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCashBalancesByAvailableIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        // Get all the cashBalanceList where available equals to
        defaultCashBalanceFiltering("available.equals=" + DEFAULT_AVAILABLE, "available.equals=" + UPDATED_AVAILABLE);
    }

    @Test
    @Transactional
    void getAllCashBalancesByAvailableIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        // Get all the cashBalanceList where available in
        defaultCashBalanceFiltering("available.in=" + DEFAULT_AVAILABLE + "," + UPDATED_AVAILABLE, "available.in=" + UPDATED_AVAILABLE);
    }

    @Test
    @Transactional
    void getAllCashBalancesByAvailableIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        // Get all the cashBalanceList where available is not null
        defaultCashBalanceFiltering("available.specified=true", "available.specified=false");
    }

    @Test
    @Transactional
    void getAllCashBalancesByAvailableIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        // Get all the cashBalanceList where available is greater than or equal to
        defaultCashBalanceFiltering(
            "available.greaterThanOrEqual=" + DEFAULT_AVAILABLE,
            "available.greaterThanOrEqual=" + UPDATED_AVAILABLE
        );
    }

    @Test
    @Transactional
    void getAllCashBalancesByAvailableIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        // Get all the cashBalanceList where available is less than or equal to
        defaultCashBalanceFiltering("available.lessThanOrEqual=" + DEFAULT_AVAILABLE, "available.lessThanOrEqual=" + SMALLER_AVAILABLE);
    }

    @Test
    @Transactional
    void getAllCashBalancesByAvailableIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        // Get all the cashBalanceList where available is less than
        defaultCashBalanceFiltering("available.lessThan=" + UPDATED_AVAILABLE, "available.lessThan=" + DEFAULT_AVAILABLE);
    }

    @Test
    @Transactional
    void getAllCashBalancesByAvailableIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        // Get all the cashBalanceList where available is greater than
        defaultCashBalanceFiltering("available.greaterThan=" + SMALLER_AVAILABLE, "available.greaterThan=" + DEFAULT_AVAILABLE);
    }

    @Test
    @Transactional
    void getAllCashBalancesByLastUpdatedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        // Get all the cashBalanceList where lastUpdated equals to
        defaultCashBalanceFiltering("lastUpdated.equals=" + DEFAULT_LAST_UPDATED, "lastUpdated.equals=" + UPDATED_LAST_UPDATED);
    }

    @Test
    @Transactional
    void getAllCashBalancesByLastUpdatedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        // Get all the cashBalanceList where lastUpdated in
        defaultCashBalanceFiltering(
            "lastUpdated.in=" + DEFAULT_LAST_UPDATED + "," + UPDATED_LAST_UPDATED,
            "lastUpdated.in=" + UPDATED_LAST_UPDATED
        );
    }

    @Test
    @Transactional
    void getAllCashBalancesByLastUpdatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        // Get all the cashBalanceList where lastUpdated is not null
        defaultCashBalanceFiltering("lastUpdated.specified=true", "lastUpdated.specified=false");
    }

    @Test
    @Transactional
    void getAllCashBalancesByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        // Get all the cashBalanceList where notes equals to
        defaultCashBalanceFiltering("notes.equals=" + DEFAULT_NOTES, "notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllCashBalancesByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        // Get all the cashBalanceList where notes in
        defaultCashBalanceFiltering("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES, "notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllCashBalancesByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        // Get all the cashBalanceList where notes is not null
        defaultCashBalanceFiltering("notes.specified=true", "notes.specified=false");
    }

    @Test
    @Transactional
    void getAllCashBalancesByNotesContainsSomething() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        // Get all the cashBalanceList where notes contains
        defaultCashBalanceFiltering("notes.contains=" + DEFAULT_NOTES, "notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllCashBalancesByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        // Get all the cashBalanceList where notes does not contain
        defaultCashBalanceFiltering("notes.doesNotContain=" + UPDATED_NOTES, "notes.doesNotContain=" + DEFAULT_NOTES);
    }

    @Test
    @Transactional
    void getAllCashBalancesByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        // Get all the cashBalanceList where active equals to
        defaultCashBalanceFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllCashBalancesByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        // Get all the cashBalanceList where active in
        defaultCashBalanceFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllCashBalancesByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        // Get all the cashBalanceList where active is not null
        defaultCashBalanceFiltering("active.specified=true", "active.specified=false");
    }

    private void defaultCashBalanceFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCashBalanceShouldBeFound(shouldBeFound);
        defaultCashBalanceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCashBalanceShouldBeFound(String filter) throws Exception {
        restCashBalanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cashBalance.getId().intValue())))
            .andExpect(jsonPath("$.[*].available").value(hasItem(sameNumber(DEFAULT_AVAILABLE))))
            .andExpect(jsonPath("$.[*].lastUpdated").value(hasItem(DEFAULT_LAST_UPDATED.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));

        // Check, that the count call also returns 1
        restCashBalanceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCashBalanceShouldNotBeFound(String filter) throws Exception {
        restCashBalanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCashBalanceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCashBalance() throws Exception {
        // Get the cashBalance
        restCashBalanceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCashBalance() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cashBalance
        CashBalance updatedCashBalance = cashBalanceRepository.findById(cashBalance.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCashBalance are not directly saved in db
        em.detach(updatedCashBalance);
        updatedCashBalance.available(UPDATED_AVAILABLE).lastUpdated(UPDATED_LAST_UPDATED).notes(UPDATED_NOTES).active(UPDATED_ACTIVE);
        CashBalanceDTO cashBalanceDTO = cashBalanceMapper.toDto(updatedCashBalance);

        restCashBalanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cashBalanceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cashBalanceDTO))
            )
            .andExpect(status().isOk());

        // Validate the CashBalance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCashBalanceToMatchAllProperties(updatedCashBalance);
    }

    @Test
    @Transactional
    void putNonExistingCashBalance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashBalance.setId(longCount.incrementAndGet());

        // Create the CashBalance
        CashBalanceDTO cashBalanceDTO = cashBalanceMapper.toDto(cashBalance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCashBalanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cashBalanceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cashBalanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CashBalance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCashBalance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashBalance.setId(longCount.incrementAndGet());

        // Create the CashBalance
        CashBalanceDTO cashBalanceDTO = cashBalanceMapper.toDto(cashBalance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCashBalanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cashBalanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CashBalance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCashBalance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashBalance.setId(longCount.incrementAndGet());

        // Create the CashBalance
        CashBalanceDTO cashBalanceDTO = cashBalanceMapper.toDto(cashBalance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCashBalanceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashBalanceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CashBalance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCashBalanceWithPatch() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cashBalance using partial update
        CashBalance partialUpdatedCashBalance = new CashBalance();
        partialUpdatedCashBalance.setId(cashBalance.getId());

        partialUpdatedCashBalance.available(UPDATED_AVAILABLE);

        restCashBalanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCashBalance.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCashBalance))
            )
            .andExpect(status().isOk());

        // Validate the CashBalance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCashBalanceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCashBalance, cashBalance),
            getPersistedCashBalance(cashBalance)
        );
    }

    @Test
    @Transactional
    void fullUpdateCashBalanceWithPatch() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cashBalance using partial update
        CashBalance partialUpdatedCashBalance = new CashBalance();
        partialUpdatedCashBalance.setId(cashBalance.getId());

        partialUpdatedCashBalance
            .available(UPDATED_AVAILABLE)
            .lastUpdated(UPDATED_LAST_UPDATED)
            .notes(UPDATED_NOTES)
            .active(UPDATED_ACTIVE);

        restCashBalanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCashBalance.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCashBalance))
            )
            .andExpect(status().isOk());

        // Validate the CashBalance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCashBalanceUpdatableFieldsEquals(partialUpdatedCashBalance, getPersistedCashBalance(partialUpdatedCashBalance));
    }

    @Test
    @Transactional
    void patchNonExistingCashBalance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashBalance.setId(longCount.incrementAndGet());

        // Create the CashBalance
        CashBalanceDTO cashBalanceDTO = cashBalanceMapper.toDto(cashBalance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCashBalanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cashBalanceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cashBalanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CashBalance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCashBalance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashBalance.setId(longCount.incrementAndGet());

        // Create the CashBalance
        CashBalanceDTO cashBalanceDTO = cashBalanceMapper.toDto(cashBalance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCashBalanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cashBalanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CashBalance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCashBalance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashBalance.setId(longCount.incrementAndGet());

        // Create the CashBalance
        CashBalanceDTO cashBalanceDTO = cashBalanceMapper.toDto(cashBalance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCashBalanceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(cashBalanceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CashBalance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCashBalance() throws Exception {
        // Initialize the database
        insertedCashBalance = cashBalanceRepository.saveAndFlush(cashBalance);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the cashBalance
        restCashBalanceMockMvc
            .perform(delete(ENTITY_API_URL_ID, cashBalance.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return cashBalanceRepository.count();
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

    protected CashBalance getPersistedCashBalance(CashBalance cashBalance) {
        return cashBalanceRepository.findById(cashBalance.getId()).orElseThrow();
    }

    protected void assertPersistedCashBalanceToMatchAllProperties(CashBalance expectedCashBalance) {
        assertCashBalanceAllPropertiesEquals(expectedCashBalance, getPersistedCashBalance(expectedCashBalance));
    }

    protected void assertPersistedCashBalanceToMatchUpdatableProperties(CashBalance expectedCashBalance) {
        assertCashBalanceAllUpdatablePropertiesEquals(expectedCashBalance, getPersistedCashBalance(expectedCashBalance));
    }
}
