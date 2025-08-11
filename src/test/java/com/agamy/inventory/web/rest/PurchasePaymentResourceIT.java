package com.agamy.inventory.web.rest;

import static com.agamy.inventory.domain.PurchasePaymentAsserts.*;
import static com.agamy.inventory.web.rest.TestUtil.createUpdateProxyForBean;
import static com.agamy.inventory.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.agamy.inventory.IntegrationTest;
import com.agamy.inventory.domain.Purchase;
import com.agamy.inventory.domain.PurchasePayment;
import com.agamy.inventory.repository.PurchasePaymentRepository;
import com.agamy.inventory.service.dto.PurchasePaymentDTO;
import com.agamy.inventory.service.mapper.PurchasePaymentMapper;
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
 * Integration tests for the {@link PurchasePaymentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PurchasePaymentResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT = new BigDecimal(1 - 1);

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/purchase-payments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PurchasePaymentRepository purchasePaymentRepository;

    @Autowired
    private PurchasePaymentMapper purchasePaymentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPurchasePaymentMockMvc;

    private PurchasePayment purchasePayment;

    private PurchasePayment insertedPurchasePayment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PurchasePayment createEntity() {
        return new PurchasePayment().date(DEFAULT_DATE).amount(DEFAULT_AMOUNT).active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PurchasePayment createUpdatedEntity() {
        return new PurchasePayment().date(UPDATED_DATE).amount(UPDATED_AMOUNT).active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        purchasePayment = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPurchasePayment != null) {
            purchasePaymentRepository.delete(insertedPurchasePayment);
            insertedPurchasePayment = null;
        }
    }

    @Test
    @Transactional
    void createPurchasePayment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PurchasePayment
        PurchasePaymentDTO purchasePaymentDTO = purchasePaymentMapper.toDto(purchasePayment);
        var returnedPurchasePaymentDTO = om.readValue(
            restPurchasePaymentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchasePaymentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PurchasePaymentDTO.class
        );

        // Validate the PurchasePayment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPurchasePayment = purchasePaymentMapper.toEntity(returnedPurchasePaymentDTO);
        assertPurchasePaymentUpdatableFieldsEquals(returnedPurchasePayment, getPersistedPurchasePayment(returnedPurchasePayment));

        insertedPurchasePayment = returnedPurchasePayment;
    }

    @Test
    @Transactional
    void createPurchasePaymentWithExistingId() throws Exception {
        // Create the PurchasePayment with an existing ID
        purchasePayment.setId(1L);
        PurchasePaymentDTO purchasePaymentDTO = purchasePaymentMapper.toDto(purchasePayment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPurchasePaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchasePaymentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PurchasePayment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        purchasePayment.setDate(null);

        // Create the PurchasePayment, which fails.
        PurchasePaymentDTO purchasePaymentDTO = purchasePaymentMapper.toDto(purchasePayment);

        restPurchasePaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchasePaymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        purchasePayment.setAmount(null);

        // Create the PurchasePayment, which fails.
        PurchasePaymentDTO purchasePaymentDTO = purchasePaymentMapper.toDto(purchasePayment);

        restPurchasePaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchasePaymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPurchasePayments() throws Exception {
        // Initialize the database
        insertedPurchasePayment = purchasePaymentRepository.saveAndFlush(purchasePayment);

        // Get all the purchasePaymentList
        restPurchasePaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(purchasePayment.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getPurchasePayment() throws Exception {
        // Initialize the database
        insertedPurchasePayment = purchasePaymentRepository.saveAndFlush(purchasePayment);

        // Get the purchasePayment
        restPurchasePaymentMockMvc
            .perform(get(ENTITY_API_URL_ID, purchasePayment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(purchasePayment.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getPurchasePaymentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedPurchasePayment = purchasePaymentRepository.saveAndFlush(purchasePayment);

        Long id = purchasePayment.getId();

        defaultPurchasePaymentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPurchasePaymentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPurchasePaymentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPurchasePaymentsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchasePayment = purchasePaymentRepository.saveAndFlush(purchasePayment);

        // Get all the purchasePaymentList where date equals to
        defaultPurchasePaymentFiltering("date.equals=" + DEFAULT_DATE, "date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllPurchasePaymentsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPurchasePayment = purchasePaymentRepository.saveAndFlush(purchasePayment);

        // Get all the purchasePaymentList where date in
        defaultPurchasePaymentFiltering("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE, "date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllPurchasePaymentsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPurchasePayment = purchasePaymentRepository.saveAndFlush(purchasePayment);

        // Get all the purchasePaymentList where date is not null
        defaultPurchasePaymentFiltering("date.specified=true", "date.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchasePaymentsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchasePayment = purchasePaymentRepository.saveAndFlush(purchasePayment);

        // Get all the purchasePaymentList where amount equals to
        defaultPurchasePaymentFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchasePaymentsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPurchasePayment = purchasePaymentRepository.saveAndFlush(purchasePayment);

        // Get all the purchasePaymentList where amount in
        defaultPurchasePaymentFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchasePaymentsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPurchasePayment = purchasePaymentRepository.saveAndFlush(purchasePayment);

        // Get all the purchasePaymentList where amount is not null
        defaultPurchasePaymentFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchasePaymentsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchasePayment = purchasePaymentRepository.saveAndFlush(purchasePayment);

        // Get all the purchasePaymentList where amount is greater than or equal to
        defaultPurchasePaymentFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchasePaymentsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchasePayment = purchasePaymentRepository.saveAndFlush(purchasePayment);

        // Get all the purchasePaymentList where amount is less than or equal to
        defaultPurchasePaymentFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchasePaymentsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPurchasePayment = purchasePaymentRepository.saveAndFlush(purchasePayment);

        // Get all the purchasePaymentList where amount is less than
        defaultPurchasePaymentFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchasePaymentsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPurchasePayment = purchasePaymentRepository.saveAndFlush(purchasePayment);

        // Get all the purchasePaymentList where amount is greater than
        defaultPurchasePaymentFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchasePaymentsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPurchasePayment = purchasePaymentRepository.saveAndFlush(purchasePayment);

        // Get all the purchasePaymentList where active equals to
        defaultPurchasePaymentFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPurchasePaymentsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPurchasePayment = purchasePaymentRepository.saveAndFlush(purchasePayment);

        // Get all the purchasePaymentList where active in
        defaultPurchasePaymentFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPurchasePaymentsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPurchasePayment = purchasePaymentRepository.saveAndFlush(purchasePayment);

        // Get all the purchasePaymentList where active is not null
        defaultPurchasePaymentFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchasePaymentsByPurchaseIsEqualToSomething() throws Exception {
        Purchase purchase;
        if (TestUtil.findAll(em, Purchase.class).isEmpty()) {
            purchasePaymentRepository.saveAndFlush(purchasePayment);
            purchase = PurchaseResourceIT.createEntity();
        } else {
            purchase = TestUtil.findAll(em, Purchase.class).get(0);
        }
        em.persist(purchase);
        em.flush();
        purchasePayment.setPurchase(purchase);
        purchasePaymentRepository.saveAndFlush(purchasePayment);
        Long purchaseId = purchase.getId();
        // Get all the purchasePaymentList where purchase equals to purchaseId
        defaultPurchasePaymentShouldBeFound("purchaseId.equals=" + purchaseId);

        // Get all the purchasePaymentList where purchase equals to (purchaseId + 1)
        defaultPurchasePaymentShouldNotBeFound("purchaseId.equals=" + (purchaseId + 1));
    }

    private void defaultPurchasePaymentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPurchasePaymentShouldBeFound(shouldBeFound);
        defaultPurchasePaymentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPurchasePaymentShouldBeFound(String filter) throws Exception {
        restPurchasePaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(purchasePayment.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));

        // Check, that the count call also returns 1
        restPurchasePaymentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPurchasePaymentShouldNotBeFound(String filter) throws Exception {
        restPurchasePaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPurchasePaymentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPurchasePayment() throws Exception {
        // Get the purchasePayment
        restPurchasePaymentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPurchasePayment() throws Exception {
        // Initialize the database
        insertedPurchasePayment = purchasePaymentRepository.saveAndFlush(purchasePayment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the purchasePayment
        PurchasePayment updatedPurchasePayment = purchasePaymentRepository.findById(purchasePayment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPurchasePayment are not directly saved in db
        em.detach(updatedPurchasePayment);
        updatedPurchasePayment.date(UPDATED_DATE).amount(UPDATED_AMOUNT).active(UPDATED_ACTIVE);
        PurchasePaymentDTO purchasePaymentDTO = purchasePaymentMapper.toDto(updatedPurchasePayment);

        restPurchasePaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchasePaymentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(purchasePaymentDTO))
            )
            .andExpect(status().isOk());

        // Validate the PurchasePayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPurchasePaymentToMatchAllProperties(updatedPurchasePayment);
    }

    @Test
    @Transactional
    void putNonExistingPurchasePayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchasePayment.setId(longCount.incrementAndGet());

        // Create the PurchasePayment
        PurchasePaymentDTO purchasePaymentDTO = purchasePaymentMapper.toDto(purchasePayment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchasePaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchasePaymentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(purchasePaymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchasePayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPurchasePayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchasePayment.setId(longCount.incrementAndGet());

        // Create the PurchasePayment
        PurchasePaymentDTO purchasePaymentDTO = purchasePaymentMapper.toDto(purchasePayment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchasePaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(purchasePaymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchasePayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPurchasePayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchasePayment.setId(longCount.incrementAndGet());

        // Create the PurchasePayment
        PurchasePaymentDTO purchasePaymentDTO = purchasePaymentMapper.toDto(purchasePayment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchasePaymentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchasePaymentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PurchasePayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePurchasePaymentWithPatch() throws Exception {
        // Initialize the database
        insertedPurchasePayment = purchasePaymentRepository.saveAndFlush(purchasePayment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the purchasePayment using partial update
        PurchasePayment partialUpdatedPurchasePayment = new PurchasePayment();
        partialUpdatedPurchasePayment.setId(purchasePayment.getId());

        restPurchasePaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchasePayment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPurchasePayment))
            )
            .andExpect(status().isOk());

        // Validate the PurchasePayment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPurchasePaymentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPurchasePayment, purchasePayment),
            getPersistedPurchasePayment(purchasePayment)
        );
    }

    @Test
    @Transactional
    void fullUpdatePurchasePaymentWithPatch() throws Exception {
        // Initialize the database
        insertedPurchasePayment = purchasePaymentRepository.saveAndFlush(purchasePayment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the purchasePayment using partial update
        PurchasePayment partialUpdatedPurchasePayment = new PurchasePayment();
        partialUpdatedPurchasePayment.setId(purchasePayment.getId());

        partialUpdatedPurchasePayment.date(UPDATED_DATE).amount(UPDATED_AMOUNT).active(UPDATED_ACTIVE);

        restPurchasePaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchasePayment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPurchasePayment))
            )
            .andExpect(status().isOk());

        // Validate the PurchasePayment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPurchasePaymentUpdatableFieldsEquals(
            partialUpdatedPurchasePayment,
            getPersistedPurchasePayment(partialUpdatedPurchasePayment)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPurchasePayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchasePayment.setId(longCount.incrementAndGet());

        // Create the PurchasePayment
        PurchasePaymentDTO purchasePaymentDTO = purchasePaymentMapper.toDto(purchasePayment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchasePaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, purchasePaymentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(purchasePaymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchasePayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPurchasePayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchasePayment.setId(longCount.incrementAndGet());

        // Create the PurchasePayment
        PurchasePaymentDTO purchasePaymentDTO = purchasePaymentMapper.toDto(purchasePayment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchasePaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(purchasePaymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchasePayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPurchasePayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchasePayment.setId(longCount.incrementAndGet());

        // Create the PurchasePayment
        PurchasePaymentDTO purchasePaymentDTO = purchasePaymentMapper.toDto(purchasePayment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchasePaymentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(purchasePaymentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PurchasePayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePurchasePayment() throws Exception {
        // Initialize the database
        insertedPurchasePayment = purchasePaymentRepository.saveAndFlush(purchasePayment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the purchasePayment
        restPurchasePaymentMockMvc
            .perform(delete(ENTITY_API_URL_ID, purchasePayment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return purchasePaymentRepository.count();
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

    protected PurchasePayment getPersistedPurchasePayment(PurchasePayment purchasePayment) {
        return purchasePaymentRepository.findById(purchasePayment.getId()).orElseThrow();
    }

    protected void assertPersistedPurchasePaymentToMatchAllProperties(PurchasePayment expectedPurchasePayment) {
        assertPurchasePaymentAllPropertiesEquals(expectedPurchasePayment, getPersistedPurchasePayment(expectedPurchasePayment));
    }

    protected void assertPersistedPurchasePaymentToMatchUpdatableProperties(PurchasePayment expectedPurchasePayment) {
        assertPurchasePaymentAllUpdatablePropertiesEquals(expectedPurchasePayment, getPersistedPurchasePayment(expectedPurchasePayment));
    }
}
