package com.agamy.inventory.web.rest;

import static com.agamy.inventory.domain.OutgoingPaymentAsserts.*;
import static com.agamy.inventory.web.rest.TestUtil.createUpdateProxyForBean;
import static com.agamy.inventory.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.agamy.inventory.IntegrationTest;
import com.agamy.inventory.domain.OutgoingPayment;
import com.agamy.inventory.repository.OutgoingPaymentRepository;
import com.agamy.inventory.service.dto.OutgoingPaymentDTO;
import com.agamy.inventory.service.mapper.OutgoingPaymentMapper;
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
 * Integration tests for the {@link OutgoingPaymentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OutgoingPaymentResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT = new BigDecimal(1 - 1);

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/outgoing-payments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OutgoingPaymentRepository outgoingPaymentRepository;

    @Autowired
    private OutgoingPaymentMapper outgoingPaymentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOutgoingPaymentMockMvc;

    private OutgoingPayment outgoingPayment;

    private OutgoingPayment insertedOutgoingPayment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OutgoingPayment createEntity() {
        return new OutgoingPayment()
            .date(DEFAULT_DATE)
            .amount(DEFAULT_AMOUNT)
            .reason(DEFAULT_REASON)
            .notes(DEFAULT_NOTES)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OutgoingPayment createUpdatedEntity() {
        return new OutgoingPayment()
            .date(UPDATED_DATE)
            .amount(UPDATED_AMOUNT)
            .reason(UPDATED_REASON)
            .notes(UPDATED_NOTES)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        outgoingPayment = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedOutgoingPayment != null) {
            outgoingPaymentRepository.delete(insertedOutgoingPayment);
            insertedOutgoingPayment = null;
        }
    }

    @Test
    @Transactional
    void createOutgoingPayment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the OutgoingPayment
        OutgoingPaymentDTO outgoingPaymentDTO = outgoingPaymentMapper.toDto(outgoingPayment);
        var returnedOutgoingPaymentDTO = om.readValue(
            restOutgoingPaymentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(outgoingPaymentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            OutgoingPaymentDTO.class
        );

        // Validate the OutgoingPayment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOutgoingPayment = outgoingPaymentMapper.toEntity(returnedOutgoingPaymentDTO);
        assertOutgoingPaymentUpdatableFieldsEquals(returnedOutgoingPayment, getPersistedOutgoingPayment(returnedOutgoingPayment));

        insertedOutgoingPayment = returnedOutgoingPayment;
    }

    @Test
    @Transactional
    void createOutgoingPaymentWithExistingId() throws Exception {
        // Create the OutgoingPayment with an existing ID
        outgoingPayment.setId(1L);
        OutgoingPaymentDTO outgoingPaymentDTO = outgoingPaymentMapper.toDto(outgoingPayment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOutgoingPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(outgoingPaymentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the OutgoingPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        outgoingPayment.setDate(null);

        // Create the OutgoingPayment, which fails.
        OutgoingPaymentDTO outgoingPaymentDTO = outgoingPaymentMapper.toDto(outgoingPayment);

        restOutgoingPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(outgoingPaymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        outgoingPayment.setAmount(null);

        // Create the OutgoingPayment, which fails.
        OutgoingPaymentDTO outgoingPaymentDTO = outgoingPaymentMapper.toDto(outgoingPayment);

        restOutgoingPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(outgoingPaymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOutgoingPayments() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList
        restOutgoingPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(outgoingPayment.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getOutgoingPayment() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get the outgoingPayment
        restOutgoingPaymentMockMvc
            .perform(get(ENTITY_API_URL_ID, outgoingPayment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(outgoingPayment.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getOutgoingPaymentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        Long id = outgoingPayment.getId();

        defaultOutgoingPaymentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultOutgoingPaymentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultOutgoingPaymentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where date equals to
        defaultOutgoingPaymentFiltering("date.equals=" + DEFAULT_DATE, "date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where date in
        defaultOutgoingPaymentFiltering("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE, "date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where date is not null
        defaultOutgoingPaymentFiltering("date.specified=true", "date.specified=false");
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where amount equals to
        defaultOutgoingPaymentFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where amount in
        defaultOutgoingPaymentFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where amount is not null
        defaultOutgoingPaymentFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where amount is greater than or equal to
        defaultOutgoingPaymentFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where amount is less than or equal to
        defaultOutgoingPaymentFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where amount is less than
        defaultOutgoingPaymentFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where amount is greater than
        defaultOutgoingPaymentFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where reason equals to
        defaultOutgoingPaymentFiltering("reason.equals=" + DEFAULT_REASON, "reason.equals=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByReasonIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where reason in
        defaultOutgoingPaymentFiltering("reason.in=" + DEFAULT_REASON + "," + UPDATED_REASON, "reason.in=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where reason is not null
        defaultOutgoingPaymentFiltering("reason.specified=true", "reason.specified=false");
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByReasonContainsSomething() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where reason contains
        defaultOutgoingPaymentFiltering("reason.contains=" + DEFAULT_REASON, "reason.contains=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByReasonNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where reason does not contain
        defaultOutgoingPaymentFiltering("reason.doesNotContain=" + UPDATED_REASON, "reason.doesNotContain=" + DEFAULT_REASON);
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where notes equals to
        defaultOutgoingPaymentFiltering("notes.equals=" + DEFAULT_NOTES, "notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where notes in
        defaultOutgoingPaymentFiltering("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES, "notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where notes is not null
        defaultOutgoingPaymentFiltering("notes.specified=true", "notes.specified=false");
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByNotesContainsSomething() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where notes contains
        defaultOutgoingPaymentFiltering("notes.contains=" + DEFAULT_NOTES, "notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where notes does not contain
        defaultOutgoingPaymentFiltering("notes.doesNotContain=" + UPDATED_NOTES, "notes.doesNotContain=" + DEFAULT_NOTES);
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where active equals to
        defaultOutgoingPaymentFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where active in
        defaultOutgoingPaymentFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllOutgoingPaymentsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        // Get all the outgoingPaymentList where active is not null
        defaultOutgoingPaymentFiltering("active.specified=true", "active.specified=false");
    }

    private void defaultOutgoingPaymentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultOutgoingPaymentShouldBeFound(shouldBeFound);
        defaultOutgoingPaymentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOutgoingPaymentShouldBeFound(String filter) throws Exception {
        restOutgoingPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(outgoingPayment.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));

        // Check, that the count call also returns 1
        restOutgoingPaymentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOutgoingPaymentShouldNotBeFound(String filter) throws Exception {
        restOutgoingPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOutgoingPaymentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOutgoingPayment() throws Exception {
        // Get the outgoingPayment
        restOutgoingPaymentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOutgoingPayment() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the outgoingPayment
        OutgoingPayment updatedOutgoingPayment = outgoingPaymentRepository.findById(outgoingPayment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOutgoingPayment are not directly saved in db
        em.detach(updatedOutgoingPayment);
        updatedOutgoingPayment.date(UPDATED_DATE).amount(UPDATED_AMOUNT).reason(UPDATED_REASON).notes(UPDATED_NOTES).active(UPDATED_ACTIVE);
        OutgoingPaymentDTO outgoingPaymentDTO = outgoingPaymentMapper.toDto(updatedOutgoingPayment);

        restOutgoingPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, outgoingPaymentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(outgoingPaymentDTO))
            )
            .andExpect(status().isOk());

        // Validate the OutgoingPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOutgoingPaymentToMatchAllProperties(updatedOutgoingPayment);
    }

    @Test
    @Transactional
    void putNonExistingOutgoingPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        outgoingPayment.setId(longCount.incrementAndGet());

        // Create the OutgoingPayment
        OutgoingPaymentDTO outgoingPaymentDTO = outgoingPaymentMapper.toDto(outgoingPayment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOutgoingPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, outgoingPaymentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(outgoingPaymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OutgoingPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOutgoingPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        outgoingPayment.setId(longCount.incrementAndGet());

        // Create the OutgoingPayment
        OutgoingPaymentDTO outgoingPaymentDTO = outgoingPaymentMapper.toDto(outgoingPayment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOutgoingPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(outgoingPaymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OutgoingPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOutgoingPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        outgoingPayment.setId(longCount.incrementAndGet());

        // Create the OutgoingPayment
        OutgoingPaymentDTO outgoingPaymentDTO = outgoingPaymentMapper.toDto(outgoingPayment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOutgoingPaymentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(outgoingPaymentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OutgoingPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOutgoingPaymentWithPatch() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the outgoingPayment using partial update
        OutgoingPayment partialUpdatedOutgoingPayment = new OutgoingPayment();
        partialUpdatedOutgoingPayment.setId(outgoingPayment.getId());

        partialUpdatedOutgoingPayment.amount(UPDATED_AMOUNT).notes(UPDATED_NOTES);

        restOutgoingPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOutgoingPayment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOutgoingPayment))
            )
            .andExpect(status().isOk());

        // Validate the OutgoingPayment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOutgoingPaymentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOutgoingPayment, outgoingPayment),
            getPersistedOutgoingPayment(outgoingPayment)
        );
    }

    @Test
    @Transactional
    void fullUpdateOutgoingPaymentWithPatch() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the outgoingPayment using partial update
        OutgoingPayment partialUpdatedOutgoingPayment = new OutgoingPayment();
        partialUpdatedOutgoingPayment.setId(outgoingPayment.getId());

        partialUpdatedOutgoingPayment
            .date(UPDATED_DATE)
            .amount(UPDATED_AMOUNT)
            .reason(UPDATED_REASON)
            .notes(UPDATED_NOTES)
            .active(UPDATED_ACTIVE);

        restOutgoingPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOutgoingPayment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOutgoingPayment))
            )
            .andExpect(status().isOk());

        // Validate the OutgoingPayment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOutgoingPaymentUpdatableFieldsEquals(
            partialUpdatedOutgoingPayment,
            getPersistedOutgoingPayment(partialUpdatedOutgoingPayment)
        );
    }

    @Test
    @Transactional
    void patchNonExistingOutgoingPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        outgoingPayment.setId(longCount.incrementAndGet());

        // Create the OutgoingPayment
        OutgoingPaymentDTO outgoingPaymentDTO = outgoingPaymentMapper.toDto(outgoingPayment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOutgoingPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, outgoingPaymentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(outgoingPaymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OutgoingPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOutgoingPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        outgoingPayment.setId(longCount.incrementAndGet());

        // Create the OutgoingPayment
        OutgoingPaymentDTO outgoingPaymentDTO = outgoingPaymentMapper.toDto(outgoingPayment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOutgoingPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(outgoingPaymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OutgoingPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOutgoingPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        outgoingPayment.setId(longCount.incrementAndGet());

        // Create the OutgoingPayment
        OutgoingPaymentDTO outgoingPaymentDTO = outgoingPaymentMapper.toDto(outgoingPayment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOutgoingPaymentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(outgoingPaymentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OutgoingPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOutgoingPayment() throws Exception {
        // Initialize the database
        insertedOutgoingPayment = outgoingPaymentRepository.saveAndFlush(outgoingPayment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the outgoingPayment
        restOutgoingPaymentMockMvc
            .perform(delete(ENTITY_API_URL_ID, outgoingPayment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return outgoingPaymentRepository.count();
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

    protected OutgoingPayment getPersistedOutgoingPayment(OutgoingPayment outgoingPayment) {
        return outgoingPaymentRepository.findById(outgoingPayment.getId()).orElseThrow();
    }

    protected void assertPersistedOutgoingPaymentToMatchAllProperties(OutgoingPayment expectedOutgoingPayment) {
        assertOutgoingPaymentAllPropertiesEquals(expectedOutgoingPayment, getPersistedOutgoingPayment(expectedOutgoingPayment));
    }

    protected void assertPersistedOutgoingPaymentToMatchUpdatableProperties(OutgoingPayment expectedOutgoingPayment) {
        assertOutgoingPaymentAllUpdatablePropertiesEquals(expectedOutgoingPayment, getPersistedOutgoingPayment(expectedOutgoingPayment));
    }
}
