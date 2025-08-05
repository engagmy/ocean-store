package com.agamy.inventory.web.rest;

import static com.agamy.inventory.domain.SalePaymentAsserts.*;
import static com.agamy.inventory.web.rest.TestUtil.createUpdateProxyForBean;
import static com.agamy.inventory.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.agamy.inventory.IntegrationTest;
import com.agamy.inventory.domain.Sale;
import com.agamy.inventory.domain.SalePayment;
import com.agamy.inventory.repository.SalePaymentRepository;
import com.agamy.inventory.service.dto.SalePaymentDTO;
import com.agamy.inventory.service.mapper.SalePaymentMapper;
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
 * Integration tests for the {@link SalePaymentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SalePaymentResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT = new BigDecimal(1 - 1);

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/sale-payments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SalePaymentRepository salePaymentRepository;

    @Autowired
    private SalePaymentMapper salePaymentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSalePaymentMockMvc;

    private SalePayment salePayment;

    private SalePayment insertedSalePayment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SalePayment createEntity() {
        return new SalePayment().date(DEFAULT_DATE).amount(DEFAULT_AMOUNT).active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SalePayment createUpdatedEntity() {
        return new SalePayment().date(UPDATED_DATE).amount(UPDATED_AMOUNT).active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        salePayment = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSalePayment != null) {
            salePaymentRepository.delete(insertedSalePayment);
            insertedSalePayment = null;
        }
    }

    @Test
    @Transactional
    void createSalePayment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SalePayment
        SalePaymentDTO salePaymentDTO = salePaymentMapper.toDto(salePayment);
        var returnedSalePaymentDTO = om.readValue(
            restSalePaymentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salePaymentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SalePaymentDTO.class
        );

        // Validate the SalePayment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSalePayment = salePaymentMapper.toEntity(returnedSalePaymentDTO);
        assertSalePaymentUpdatableFieldsEquals(returnedSalePayment, getPersistedSalePayment(returnedSalePayment));

        insertedSalePayment = returnedSalePayment;
    }

    @Test
    @Transactional
    void createSalePaymentWithExistingId() throws Exception {
        // Create the SalePayment with an existing ID
        salePayment.setId(1L);
        SalePaymentDTO salePaymentDTO = salePaymentMapper.toDto(salePayment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSalePaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salePaymentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SalePayment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        salePayment.setDate(null);

        // Create the SalePayment, which fails.
        SalePaymentDTO salePaymentDTO = salePaymentMapper.toDto(salePayment);

        restSalePaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salePaymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        salePayment.setAmount(null);

        // Create the SalePayment, which fails.
        SalePaymentDTO salePaymentDTO = salePaymentMapper.toDto(salePayment);

        restSalePaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salePaymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSalePayments() throws Exception {
        // Initialize the database
        insertedSalePayment = salePaymentRepository.saveAndFlush(salePayment);

        // Get all the salePaymentList
        restSalePaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salePayment.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getSalePayment() throws Exception {
        // Initialize the database
        insertedSalePayment = salePaymentRepository.saveAndFlush(salePayment);

        // Get the salePayment
        restSalePaymentMockMvc
            .perform(get(ENTITY_API_URL_ID, salePayment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(salePayment.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getSalePaymentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedSalePayment = salePaymentRepository.saveAndFlush(salePayment);

        Long id = salePayment.getId();

        defaultSalePaymentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSalePaymentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSalePaymentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSalePaymentsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSalePayment = salePaymentRepository.saveAndFlush(salePayment);

        // Get all the salePaymentList where date equals to
        defaultSalePaymentFiltering("date.equals=" + DEFAULT_DATE, "date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllSalePaymentsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSalePayment = salePaymentRepository.saveAndFlush(salePayment);

        // Get all the salePaymentList where date in
        defaultSalePaymentFiltering("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE, "date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllSalePaymentsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSalePayment = salePaymentRepository.saveAndFlush(salePayment);

        // Get all the salePaymentList where date is not null
        defaultSalePaymentFiltering("date.specified=true", "date.specified=false");
    }

    @Test
    @Transactional
    void getAllSalePaymentsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSalePayment = salePaymentRepository.saveAndFlush(salePayment);

        // Get all the salePaymentList where amount equals to
        defaultSalePaymentFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSalePaymentsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSalePayment = salePaymentRepository.saveAndFlush(salePayment);

        // Get all the salePaymentList where amount in
        defaultSalePaymentFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSalePaymentsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSalePayment = salePaymentRepository.saveAndFlush(salePayment);

        // Get all the salePaymentList where amount is not null
        defaultSalePaymentFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllSalePaymentsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSalePayment = salePaymentRepository.saveAndFlush(salePayment);

        // Get all the salePaymentList where amount is greater than or equal to
        defaultSalePaymentFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSalePaymentsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSalePayment = salePaymentRepository.saveAndFlush(salePayment);

        // Get all the salePaymentList where amount is less than or equal to
        defaultSalePaymentFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSalePaymentsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSalePayment = salePaymentRepository.saveAndFlush(salePayment);

        // Get all the salePaymentList where amount is less than
        defaultSalePaymentFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSalePaymentsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSalePayment = salePaymentRepository.saveAndFlush(salePayment);

        // Get all the salePaymentList where amount is greater than
        defaultSalePaymentFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSalePaymentsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSalePayment = salePaymentRepository.saveAndFlush(salePayment);

        // Get all the salePaymentList where active equals to
        defaultSalePaymentFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllSalePaymentsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSalePayment = salePaymentRepository.saveAndFlush(salePayment);

        // Get all the salePaymentList where active in
        defaultSalePaymentFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllSalePaymentsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSalePayment = salePaymentRepository.saveAndFlush(salePayment);

        // Get all the salePaymentList where active is not null
        defaultSalePaymentFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllSalePaymentsBySaleIsEqualToSomething() throws Exception {
        Sale sale;
        if (TestUtil.findAll(em, Sale.class).isEmpty()) {
            salePaymentRepository.saveAndFlush(salePayment);
            sale = SaleResourceIT.createEntity();
        } else {
            sale = TestUtil.findAll(em, Sale.class).get(0);
        }
        em.persist(sale);
        em.flush();
        salePayment.setSale(sale);
        salePaymentRepository.saveAndFlush(salePayment);
        Long saleId = sale.getId();
        // Get all the salePaymentList where sale equals to saleId
        defaultSalePaymentShouldBeFound("saleId.equals=" + saleId);

        // Get all the salePaymentList where sale equals to (saleId + 1)
        defaultSalePaymentShouldNotBeFound("saleId.equals=" + (saleId + 1));
    }

    private void defaultSalePaymentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSalePaymentShouldBeFound(shouldBeFound);
        defaultSalePaymentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSalePaymentShouldBeFound(String filter) throws Exception {
        restSalePaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salePayment.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));

        // Check, that the count call also returns 1
        restSalePaymentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSalePaymentShouldNotBeFound(String filter) throws Exception {
        restSalePaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSalePaymentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSalePayment() throws Exception {
        // Get the salePayment
        restSalePaymentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSalePayment() throws Exception {
        // Initialize the database
        insertedSalePayment = salePaymentRepository.saveAndFlush(salePayment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the salePayment
        SalePayment updatedSalePayment = salePaymentRepository.findById(salePayment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSalePayment are not directly saved in db
        em.detach(updatedSalePayment);
        updatedSalePayment.date(UPDATED_DATE).amount(UPDATED_AMOUNT).active(UPDATED_ACTIVE);
        SalePaymentDTO salePaymentDTO = salePaymentMapper.toDto(updatedSalePayment);

        restSalePaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salePaymentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(salePaymentDTO))
            )
            .andExpect(status().isOk());

        // Validate the SalePayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSalePaymentToMatchAllProperties(updatedSalePayment);
    }

    @Test
    @Transactional
    void putNonExistingSalePayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salePayment.setId(longCount.incrementAndGet());

        // Create the SalePayment
        SalePaymentDTO salePaymentDTO = salePaymentMapper.toDto(salePayment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalePaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salePaymentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(salePaymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalePayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSalePayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salePayment.setId(longCount.incrementAndGet());

        // Create the SalePayment
        SalePaymentDTO salePaymentDTO = salePaymentMapper.toDto(salePayment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalePaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(salePaymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalePayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSalePayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salePayment.setId(longCount.incrementAndGet());

        // Create the SalePayment
        SalePaymentDTO salePaymentDTO = salePaymentMapper.toDto(salePayment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalePaymentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salePaymentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SalePayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSalePaymentWithPatch() throws Exception {
        // Initialize the database
        insertedSalePayment = salePaymentRepository.saveAndFlush(salePayment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the salePayment using partial update
        SalePayment partialUpdatedSalePayment = new SalePayment();
        partialUpdatedSalePayment.setId(salePayment.getId());

        partialUpdatedSalePayment.active(UPDATED_ACTIVE);

        restSalePaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalePayment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSalePayment))
            )
            .andExpect(status().isOk());

        // Validate the SalePayment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSalePaymentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSalePayment, salePayment),
            getPersistedSalePayment(salePayment)
        );
    }

    @Test
    @Transactional
    void fullUpdateSalePaymentWithPatch() throws Exception {
        // Initialize the database
        insertedSalePayment = salePaymentRepository.saveAndFlush(salePayment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the salePayment using partial update
        SalePayment partialUpdatedSalePayment = new SalePayment();
        partialUpdatedSalePayment.setId(salePayment.getId());

        partialUpdatedSalePayment.date(UPDATED_DATE).amount(UPDATED_AMOUNT).active(UPDATED_ACTIVE);

        restSalePaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalePayment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSalePayment))
            )
            .andExpect(status().isOk());

        // Validate the SalePayment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSalePaymentUpdatableFieldsEquals(partialUpdatedSalePayment, getPersistedSalePayment(partialUpdatedSalePayment));
    }

    @Test
    @Transactional
    void patchNonExistingSalePayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salePayment.setId(longCount.incrementAndGet());

        // Create the SalePayment
        SalePaymentDTO salePaymentDTO = salePaymentMapper.toDto(salePayment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalePaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, salePaymentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(salePaymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalePayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSalePayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salePayment.setId(longCount.incrementAndGet());

        // Create the SalePayment
        SalePaymentDTO salePaymentDTO = salePaymentMapper.toDto(salePayment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalePaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(salePaymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalePayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSalePayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salePayment.setId(longCount.incrementAndGet());

        // Create the SalePayment
        SalePaymentDTO salePaymentDTO = salePaymentMapper.toDto(salePayment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalePaymentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(salePaymentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SalePayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSalePayment() throws Exception {
        // Initialize the database
        insertedSalePayment = salePaymentRepository.saveAndFlush(salePayment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the salePayment
        restSalePaymentMockMvc
            .perform(delete(ENTITY_API_URL_ID, salePayment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return salePaymentRepository.count();
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

    protected SalePayment getPersistedSalePayment(SalePayment salePayment) {
        return salePaymentRepository.findById(salePayment.getId()).orElseThrow();
    }

    protected void assertPersistedSalePaymentToMatchAllProperties(SalePayment expectedSalePayment) {
        assertSalePaymentAllPropertiesEquals(expectedSalePayment, getPersistedSalePayment(expectedSalePayment));
    }

    protected void assertPersistedSalePaymentToMatchUpdatableProperties(SalePayment expectedSalePayment) {
        assertSalePaymentAllUpdatablePropertiesEquals(expectedSalePayment, getPersistedSalePayment(expectedSalePayment));
    }
}
