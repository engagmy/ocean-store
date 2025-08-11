package com.agamy.inventory.web.rest;

import static com.agamy.inventory.domain.InventoryTransactionAsserts.*;
import static com.agamy.inventory.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.agamy.inventory.IntegrationTest;
import com.agamy.inventory.domain.InventoryTransaction;
import com.agamy.inventory.domain.Product;
import com.agamy.inventory.domain.enumeration.InventoryActionType;
import com.agamy.inventory.repository.InventoryTransactionRepository;
import com.agamy.inventory.service.dto.InventoryTransactionDTO;
import com.agamy.inventory.service.mapper.InventoryTransactionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link InventoryTransactionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InventoryTransactionResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final InventoryActionType DEFAULT_TYPE = InventoryActionType.STOCK_IN;
    private static final InventoryActionType UPDATED_TYPE = InventoryActionType.STOCK_OUT;

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/inventory-transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InventoryTransactionRepository inventoryTransactionRepository;

    @Autowired
    private InventoryTransactionMapper inventoryTransactionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInventoryTransactionMockMvc;

    private InventoryTransaction inventoryTransaction;

    private InventoryTransaction insertedInventoryTransaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InventoryTransaction createEntity() {
        return new InventoryTransaction().date(DEFAULT_DATE).type(DEFAULT_TYPE).quantity(DEFAULT_QUANTITY).active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InventoryTransaction createUpdatedEntity() {
        return new InventoryTransaction().date(UPDATED_DATE).type(UPDATED_TYPE).quantity(UPDATED_QUANTITY).active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        inventoryTransaction = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedInventoryTransaction != null) {
            inventoryTransactionRepository.delete(insertedInventoryTransaction);
            insertedInventoryTransaction = null;
        }
    }

    @Test
    @Transactional
    void createInventoryTransaction() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the InventoryTransaction
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);
        var returnedInventoryTransactionDTO = om.readValue(
            restInventoryTransactionMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryTransactionDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InventoryTransactionDTO.class
        );

        // Validate the InventoryTransaction in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInventoryTransaction = inventoryTransactionMapper.toEntity(returnedInventoryTransactionDTO);
        assertInventoryTransactionUpdatableFieldsEquals(
            returnedInventoryTransaction,
            getPersistedInventoryTransaction(returnedInventoryTransaction)
        );

        insertedInventoryTransaction = returnedInventoryTransaction;
    }

    @Test
    @Transactional
    void createInventoryTransactionWithExistingId() throws Exception {
        // Create the InventoryTransaction with an existing ID
        inventoryTransaction.setId(1L);
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInventoryTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryTransactionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the InventoryTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventoryTransaction.setDate(null);

        // Create the InventoryTransaction, which fails.
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        restInventoryTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryTransactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventoryTransaction.setType(null);

        // Create the InventoryTransaction, which fails.
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        restInventoryTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryTransactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventoryTransaction.setQuantity(null);

        // Create the InventoryTransaction, which fails.
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        restInventoryTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryTransactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInventoryTransactions() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList
        restInventoryTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inventoryTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getInventoryTransaction() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get the inventoryTransaction
        restInventoryTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, inventoryTransaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(inventoryTransaction.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getInventoryTransactionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        Long id = inventoryTransaction.getId();

        defaultInventoryTransactionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultInventoryTransactionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultInventoryTransactionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where date equals to
        defaultInventoryTransactionFiltering("date.equals=" + DEFAULT_DATE, "date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where date in
        defaultInventoryTransactionFiltering("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE, "date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where date is not null
        defaultInventoryTransactionFiltering("date.specified=true", "date.specified=false");
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where type equals to
        defaultInventoryTransactionFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where type in
        defaultInventoryTransactionFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where type is not null
        defaultInventoryTransactionFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where quantity equals to
        defaultInventoryTransactionFiltering("quantity.equals=" + DEFAULT_QUANTITY, "quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where quantity in
        defaultInventoryTransactionFiltering("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY, "quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where quantity is not null
        defaultInventoryTransactionFiltering("quantity.specified=true", "quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where quantity is greater than or equal to
        defaultInventoryTransactionFiltering(
            "quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY,
            "quantity.greaterThanOrEqual=" + UPDATED_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where quantity is less than or equal to
        defaultInventoryTransactionFiltering(
            "quantity.lessThanOrEqual=" + DEFAULT_QUANTITY,
            "quantity.lessThanOrEqual=" + SMALLER_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where quantity is less than
        defaultInventoryTransactionFiltering("quantity.lessThan=" + UPDATED_QUANTITY, "quantity.lessThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where quantity is greater than
        defaultInventoryTransactionFiltering("quantity.greaterThan=" + SMALLER_QUANTITY, "quantity.greaterThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where active equals to
        defaultInventoryTransactionFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where active in
        defaultInventoryTransactionFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList where active is not null
        defaultInventoryTransactionFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllInventoryTransactionsByProductIsEqualToSomething() throws Exception {
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            inventoryTransactionRepository.saveAndFlush(inventoryTransaction);
            product = ProductResourceIT.createEntity();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        em.persist(product);
        em.flush();
        inventoryTransaction.setProduct(product);
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);
        Long productId = product.getId();
        // Get all the inventoryTransactionList where product equals to productId
        defaultInventoryTransactionShouldBeFound("productId.equals=" + productId);

        // Get all the inventoryTransactionList where product equals to (productId + 1)
        defaultInventoryTransactionShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    private void defaultInventoryTransactionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultInventoryTransactionShouldBeFound(shouldBeFound);
        defaultInventoryTransactionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInventoryTransactionShouldBeFound(String filter) throws Exception {
        restInventoryTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inventoryTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));

        // Check, that the count call also returns 1
        restInventoryTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInventoryTransactionShouldNotBeFound(String filter) throws Exception {
        restInventoryTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInventoryTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInventoryTransaction() throws Exception {
        // Get the inventoryTransaction
        restInventoryTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInventoryTransaction() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventoryTransaction
        InventoryTransaction updatedInventoryTransaction = inventoryTransactionRepository
            .findById(inventoryTransaction.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedInventoryTransaction are not directly saved in db
        em.detach(updatedInventoryTransaction);
        updatedInventoryTransaction.date(UPDATED_DATE).type(UPDATED_TYPE).quantity(UPDATED_QUANTITY).active(UPDATED_ACTIVE);
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(updatedInventoryTransaction);

        restInventoryTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inventoryTransactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isOk());

        // Validate the InventoryTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInventoryTransactionToMatchAllProperties(updatedInventoryTransaction);
    }

    @Test
    @Transactional
    void putNonExistingInventoryTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryTransaction.setId(longCount.incrementAndGet());

        // Create the InventoryTransaction
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventoryTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inventoryTransactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInventoryTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryTransaction.setId(longCount.incrementAndGet());

        // Create the InventoryTransaction
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInventoryTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryTransaction.setId(longCount.incrementAndGet());

        // Create the InventoryTransaction
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryTransactionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryTransactionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InventoryTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInventoryTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventoryTransaction using partial update
        InventoryTransaction partialUpdatedInventoryTransaction = new InventoryTransaction();
        partialUpdatedInventoryTransaction.setId(inventoryTransaction.getId());

        partialUpdatedInventoryTransaction.quantity(UPDATED_QUANTITY);

        restInventoryTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInventoryTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInventoryTransaction))
            )
            .andExpect(status().isOk());

        // Validate the InventoryTransaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInventoryTransactionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInventoryTransaction, inventoryTransaction),
            getPersistedInventoryTransaction(inventoryTransaction)
        );
    }

    @Test
    @Transactional
    void fullUpdateInventoryTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventoryTransaction using partial update
        InventoryTransaction partialUpdatedInventoryTransaction = new InventoryTransaction();
        partialUpdatedInventoryTransaction.setId(inventoryTransaction.getId());

        partialUpdatedInventoryTransaction.date(UPDATED_DATE).type(UPDATED_TYPE).quantity(UPDATED_QUANTITY).active(UPDATED_ACTIVE);

        restInventoryTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInventoryTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInventoryTransaction))
            )
            .andExpect(status().isOk());

        // Validate the InventoryTransaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInventoryTransactionUpdatableFieldsEquals(
            partialUpdatedInventoryTransaction,
            getPersistedInventoryTransaction(partialUpdatedInventoryTransaction)
        );
    }

    @Test
    @Transactional
    void patchNonExistingInventoryTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryTransaction.setId(longCount.incrementAndGet());

        // Create the InventoryTransaction
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventoryTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, inventoryTransactionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInventoryTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryTransaction.setId(longCount.incrementAndGet());

        // Create the InventoryTransaction
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInventoryTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryTransaction.setId(longCount.incrementAndGet());

        // Create the InventoryTransaction
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InventoryTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInventoryTransaction() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the inventoryTransaction
        restInventoryTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, inventoryTransaction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return inventoryTransactionRepository.count();
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

    protected InventoryTransaction getPersistedInventoryTransaction(InventoryTransaction inventoryTransaction) {
        return inventoryTransactionRepository.findById(inventoryTransaction.getId()).orElseThrow();
    }

    protected void assertPersistedInventoryTransactionToMatchAllProperties(InventoryTransaction expectedInventoryTransaction) {
        assertInventoryTransactionAllPropertiesEquals(
            expectedInventoryTransaction,
            getPersistedInventoryTransaction(expectedInventoryTransaction)
        );
    }

    protected void assertPersistedInventoryTransactionToMatchUpdatableProperties(InventoryTransaction expectedInventoryTransaction) {
        assertInventoryTransactionAllUpdatablePropertiesEquals(
            expectedInventoryTransaction,
            getPersistedInventoryTransaction(expectedInventoryTransaction)
        );
    }
}
