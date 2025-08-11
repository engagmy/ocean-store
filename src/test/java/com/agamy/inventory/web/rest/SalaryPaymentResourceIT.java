package com.agamy.inventory.web.rest;

import static com.agamy.inventory.domain.SalaryPaymentAsserts.*;
import static com.agamy.inventory.web.rest.TestUtil.createUpdateProxyForBean;
import static com.agamy.inventory.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.agamy.inventory.IntegrationTest;
import com.agamy.inventory.domain.Employee;
import com.agamy.inventory.domain.SalaryPayment;
import com.agamy.inventory.repository.SalaryPaymentRepository;
import com.agamy.inventory.service.dto.SalaryPaymentDTO;
import com.agamy.inventory.service.mapper.SalaryPaymentMapper;
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
 * Integration tests for the {@link SalaryPaymentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SalaryPaymentResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT = new BigDecimal(1 - 1);

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/salary-payments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SalaryPaymentRepository salaryPaymentRepository;

    @Autowired
    private SalaryPaymentMapper salaryPaymentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSalaryPaymentMockMvc;

    private SalaryPayment salaryPayment;

    private SalaryPayment insertedSalaryPayment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SalaryPayment createEntity() {
        return new SalaryPayment().date(DEFAULT_DATE).amount(DEFAULT_AMOUNT).active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SalaryPayment createUpdatedEntity() {
        return new SalaryPayment().date(UPDATED_DATE).amount(UPDATED_AMOUNT).active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        salaryPayment = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSalaryPayment != null) {
            salaryPaymentRepository.delete(insertedSalaryPayment);
            insertedSalaryPayment = null;
        }
    }

    @Test
    @Transactional
    void createSalaryPayment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SalaryPayment
        SalaryPaymentDTO salaryPaymentDTO = salaryPaymentMapper.toDto(salaryPayment);
        var returnedSalaryPaymentDTO = om.readValue(
            restSalaryPaymentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salaryPaymentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SalaryPaymentDTO.class
        );

        // Validate the SalaryPayment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSalaryPayment = salaryPaymentMapper.toEntity(returnedSalaryPaymentDTO);
        assertSalaryPaymentUpdatableFieldsEquals(returnedSalaryPayment, getPersistedSalaryPayment(returnedSalaryPayment));

        insertedSalaryPayment = returnedSalaryPayment;
    }

    @Test
    @Transactional
    void createSalaryPaymentWithExistingId() throws Exception {
        // Create the SalaryPayment with an existing ID
        salaryPayment.setId(1L);
        SalaryPaymentDTO salaryPaymentDTO = salaryPaymentMapper.toDto(salaryPayment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSalaryPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salaryPaymentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SalaryPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        salaryPayment.setDate(null);

        // Create the SalaryPayment, which fails.
        SalaryPaymentDTO salaryPaymentDTO = salaryPaymentMapper.toDto(salaryPayment);

        restSalaryPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salaryPaymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        salaryPayment.setAmount(null);

        // Create the SalaryPayment, which fails.
        SalaryPaymentDTO salaryPaymentDTO = salaryPaymentMapper.toDto(salaryPayment);

        restSalaryPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salaryPaymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSalaryPayments() throws Exception {
        // Initialize the database
        insertedSalaryPayment = salaryPaymentRepository.saveAndFlush(salaryPayment);

        // Get all the salaryPaymentList
        restSalaryPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salaryPayment.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getSalaryPayment() throws Exception {
        // Initialize the database
        insertedSalaryPayment = salaryPaymentRepository.saveAndFlush(salaryPayment);

        // Get the salaryPayment
        restSalaryPaymentMockMvc
            .perform(get(ENTITY_API_URL_ID, salaryPayment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(salaryPayment.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getSalaryPaymentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedSalaryPayment = salaryPaymentRepository.saveAndFlush(salaryPayment);

        Long id = salaryPayment.getId();

        defaultSalaryPaymentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSalaryPaymentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSalaryPaymentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSalaryPaymentsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSalaryPayment = salaryPaymentRepository.saveAndFlush(salaryPayment);

        // Get all the salaryPaymentList where date equals to
        defaultSalaryPaymentFiltering("date.equals=" + DEFAULT_DATE, "date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllSalaryPaymentsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSalaryPayment = salaryPaymentRepository.saveAndFlush(salaryPayment);

        // Get all the salaryPaymentList where date in
        defaultSalaryPaymentFiltering("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE, "date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllSalaryPaymentsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSalaryPayment = salaryPaymentRepository.saveAndFlush(salaryPayment);

        // Get all the salaryPaymentList where date is not null
        defaultSalaryPaymentFiltering("date.specified=true", "date.specified=false");
    }

    @Test
    @Transactional
    void getAllSalaryPaymentsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSalaryPayment = salaryPaymentRepository.saveAndFlush(salaryPayment);

        // Get all the salaryPaymentList where amount equals to
        defaultSalaryPaymentFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSalaryPaymentsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSalaryPayment = salaryPaymentRepository.saveAndFlush(salaryPayment);

        // Get all the salaryPaymentList where amount in
        defaultSalaryPaymentFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSalaryPaymentsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSalaryPayment = salaryPaymentRepository.saveAndFlush(salaryPayment);

        // Get all the salaryPaymentList where amount is not null
        defaultSalaryPaymentFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllSalaryPaymentsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSalaryPayment = salaryPaymentRepository.saveAndFlush(salaryPayment);

        // Get all the salaryPaymentList where amount is greater than or equal to
        defaultSalaryPaymentFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSalaryPaymentsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSalaryPayment = salaryPaymentRepository.saveAndFlush(salaryPayment);

        // Get all the salaryPaymentList where amount is less than or equal to
        defaultSalaryPaymentFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSalaryPaymentsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSalaryPayment = salaryPaymentRepository.saveAndFlush(salaryPayment);

        // Get all the salaryPaymentList where amount is less than
        defaultSalaryPaymentFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSalaryPaymentsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSalaryPayment = salaryPaymentRepository.saveAndFlush(salaryPayment);

        // Get all the salaryPaymentList where amount is greater than
        defaultSalaryPaymentFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSalaryPaymentsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSalaryPayment = salaryPaymentRepository.saveAndFlush(salaryPayment);

        // Get all the salaryPaymentList where active equals to
        defaultSalaryPaymentFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllSalaryPaymentsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSalaryPayment = salaryPaymentRepository.saveAndFlush(salaryPayment);

        // Get all the salaryPaymentList where active in
        defaultSalaryPaymentFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllSalaryPaymentsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSalaryPayment = salaryPaymentRepository.saveAndFlush(salaryPayment);

        // Get all the salaryPaymentList where active is not null
        defaultSalaryPaymentFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllSalaryPaymentsByEmployeeIsEqualToSomething() throws Exception {
        Employee employee;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            salaryPaymentRepository.saveAndFlush(salaryPayment);
            employee = EmployeeResourceIT.createEntity();
        } else {
            employee = TestUtil.findAll(em, Employee.class).get(0);
        }
        em.persist(employee);
        em.flush();
        salaryPayment.setEmployee(employee);
        salaryPaymentRepository.saveAndFlush(salaryPayment);
        Long employeeId = employee.getId();
        // Get all the salaryPaymentList where employee equals to employeeId
        defaultSalaryPaymentShouldBeFound("employeeId.equals=" + employeeId);

        // Get all the salaryPaymentList where employee equals to (employeeId + 1)
        defaultSalaryPaymentShouldNotBeFound("employeeId.equals=" + (employeeId + 1));
    }

    private void defaultSalaryPaymentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSalaryPaymentShouldBeFound(shouldBeFound);
        defaultSalaryPaymentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSalaryPaymentShouldBeFound(String filter) throws Exception {
        restSalaryPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salaryPayment.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));

        // Check, that the count call also returns 1
        restSalaryPaymentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSalaryPaymentShouldNotBeFound(String filter) throws Exception {
        restSalaryPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSalaryPaymentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSalaryPayment() throws Exception {
        // Get the salaryPayment
        restSalaryPaymentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSalaryPayment() throws Exception {
        // Initialize the database
        insertedSalaryPayment = salaryPaymentRepository.saveAndFlush(salaryPayment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the salaryPayment
        SalaryPayment updatedSalaryPayment = salaryPaymentRepository.findById(salaryPayment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSalaryPayment are not directly saved in db
        em.detach(updatedSalaryPayment);
        updatedSalaryPayment.date(UPDATED_DATE).amount(UPDATED_AMOUNT).active(UPDATED_ACTIVE);
        SalaryPaymentDTO salaryPaymentDTO = salaryPaymentMapper.toDto(updatedSalaryPayment);

        restSalaryPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salaryPaymentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(salaryPaymentDTO))
            )
            .andExpect(status().isOk());

        // Validate the SalaryPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSalaryPaymentToMatchAllProperties(updatedSalaryPayment);
    }

    @Test
    @Transactional
    void putNonExistingSalaryPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salaryPayment.setId(longCount.incrementAndGet());

        // Create the SalaryPayment
        SalaryPaymentDTO salaryPaymentDTO = salaryPaymentMapper.toDto(salaryPayment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalaryPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salaryPaymentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(salaryPaymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalaryPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSalaryPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salaryPayment.setId(longCount.incrementAndGet());

        // Create the SalaryPayment
        SalaryPaymentDTO salaryPaymentDTO = salaryPaymentMapper.toDto(salaryPayment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalaryPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(salaryPaymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalaryPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSalaryPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salaryPayment.setId(longCount.incrementAndGet());

        // Create the SalaryPayment
        SalaryPaymentDTO salaryPaymentDTO = salaryPaymentMapper.toDto(salaryPayment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalaryPaymentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salaryPaymentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SalaryPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSalaryPaymentWithPatch() throws Exception {
        // Initialize the database
        insertedSalaryPayment = salaryPaymentRepository.saveAndFlush(salaryPayment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the salaryPayment using partial update
        SalaryPayment partialUpdatedSalaryPayment = new SalaryPayment();
        partialUpdatedSalaryPayment.setId(salaryPayment.getId());

        partialUpdatedSalaryPayment.date(UPDATED_DATE).active(UPDATED_ACTIVE);

        restSalaryPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalaryPayment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSalaryPayment))
            )
            .andExpect(status().isOk());

        // Validate the SalaryPayment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSalaryPaymentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSalaryPayment, salaryPayment),
            getPersistedSalaryPayment(salaryPayment)
        );
    }

    @Test
    @Transactional
    void fullUpdateSalaryPaymentWithPatch() throws Exception {
        // Initialize the database
        insertedSalaryPayment = salaryPaymentRepository.saveAndFlush(salaryPayment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the salaryPayment using partial update
        SalaryPayment partialUpdatedSalaryPayment = new SalaryPayment();
        partialUpdatedSalaryPayment.setId(salaryPayment.getId());

        partialUpdatedSalaryPayment.date(UPDATED_DATE).amount(UPDATED_AMOUNT).active(UPDATED_ACTIVE);

        restSalaryPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalaryPayment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSalaryPayment))
            )
            .andExpect(status().isOk());

        // Validate the SalaryPayment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSalaryPaymentUpdatableFieldsEquals(partialUpdatedSalaryPayment, getPersistedSalaryPayment(partialUpdatedSalaryPayment));
    }

    @Test
    @Transactional
    void patchNonExistingSalaryPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salaryPayment.setId(longCount.incrementAndGet());

        // Create the SalaryPayment
        SalaryPaymentDTO salaryPaymentDTO = salaryPaymentMapper.toDto(salaryPayment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalaryPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, salaryPaymentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(salaryPaymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalaryPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSalaryPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salaryPayment.setId(longCount.incrementAndGet());

        // Create the SalaryPayment
        SalaryPaymentDTO salaryPaymentDTO = salaryPaymentMapper.toDto(salaryPayment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalaryPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(salaryPaymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalaryPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSalaryPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salaryPayment.setId(longCount.incrementAndGet());

        // Create the SalaryPayment
        SalaryPaymentDTO salaryPaymentDTO = salaryPaymentMapper.toDto(salaryPayment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalaryPaymentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(salaryPaymentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SalaryPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSalaryPayment() throws Exception {
        // Initialize the database
        insertedSalaryPayment = salaryPaymentRepository.saveAndFlush(salaryPayment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the salaryPayment
        restSalaryPaymentMockMvc
            .perform(delete(ENTITY_API_URL_ID, salaryPayment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return salaryPaymentRepository.count();
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

    protected SalaryPayment getPersistedSalaryPayment(SalaryPayment salaryPayment) {
        return salaryPaymentRepository.findById(salaryPayment.getId()).orElseThrow();
    }

    protected void assertPersistedSalaryPaymentToMatchAllProperties(SalaryPayment expectedSalaryPayment) {
        assertSalaryPaymentAllPropertiesEquals(expectedSalaryPayment, getPersistedSalaryPayment(expectedSalaryPayment));
    }

    protected void assertPersistedSalaryPaymentToMatchUpdatableProperties(SalaryPayment expectedSalaryPayment) {
        assertSalaryPaymentAllUpdatablePropertiesEquals(expectedSalaryPayment, getPersistedSalaryPayment(expectedSalaryPayment));
    }
}
