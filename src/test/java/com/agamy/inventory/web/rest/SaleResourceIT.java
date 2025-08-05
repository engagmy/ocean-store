package com.agamy.inventory.web.rest;

import static com.agamy.inventory.domain.SaleAsserts.*;
import static com.agamy.inventory.web.rest.TestUtil.createUpdateProxyForBean;
import static com.agamy.inventory.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.agamy.inventory.IntegrationTest;
import com.agamy.inventory.domain.Product;
import com.agamy.inventory.domain.Sale;
import com.agamy.inventory.domain.SaleOperation;
import com.agamy.inventory.repository.SaleRepository;
import com.agamy.inventory.service.dto.SaleDTO;
import com.agamy.inventory.service.mapper.SaleMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link SaleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SaleResourceIT {

    private static final String DEFAULT_PRODUCT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PRODUCT_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_UNIT_PRICE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_DISCOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_DISCOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_DISCOUNT = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_LINE_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_LINE_TOTAL = new BigDecimal(2);
    private static final BigDecimal SMALLER_LINE_TOTAL = new BigDecimal(1 - 1);

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/sales";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleMapper saleMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSaleMockMvc;

    private Sale sale;

    private Sale insertedSale;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sale createEntity() {
        return new Sale()
            .productName(DEFAULT_PRODUCT_NAME)
            .quantity(DEFAULT_QUANTITY)
            .unitPrice(DEFAULT_UNIT_PRICE)
            .discount(DEFAULT_DISCOUNT)
            .lineTotal(DEFAULT_LINE_TOTAL)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sale createUpdatedEntity() {
        return new Sale()
            .productName(UPDATED_PRODUCT_NAME)
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .discount(UPDATED_DISCOUNT)
            .lineTotal(UPDATED_LINE_TOTAL)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        sale = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSale != null) {
            saleRepository.delete(insertedSale);
            insertedSale = null;
        }
    }

    @Test
    @Transactional
    void createSale() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);
        var returnedSaleDTO = om.readValue(
            restSaleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SaleDTO.class
        );

        // Validate the Sale in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSale = saleMapper.toEntity(returnedSaleDTO);
        assertSaleUpdatableFieldsEquals(returnedSale, getPersistedSale(returnedSale));

        insertedSale = returnedSale;
    }

    @Test
    @Transactional
    void createSaleWithExistingId() throws Exception {
        // Create the Sale with an existing ID
        sale.setId(1L);
        SaleDTO saleDTO = saleMapper.toDto(sale);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSaleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sale.setQuantity(null);

        // Create the Sale, which fails.
        SaleDTO saleDTO = saleMapper.toDto(sale);

        restSaleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUnitPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sale.setUnitPrice(null);

        // Create the Sale, which fails.
        SaleDTO saleDTO = saleMapper.toDto(sale);

        restSaleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLineTotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sale.setLineTotal(null);

        // Create the Sale, which fails.
        SaleDTO saleDTO = saleMapper.toDto(sale);

        restSaleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSales() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList
        restSaleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sale.getId().intValue())))
            .andExpect(jsonPath("$.[*].productName").value(hasItem(DEFAULT_PRODUCT_NAME)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].discount").value(hasItem(sameNumber(DEFAULT_DISCOUNT))))
            .andExpect(jsonPath("$.[*].lineTotal").value(hasItem(sameNumber(DEFAULT_LINE_TOTAL))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getSale() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get the sale
        restSaleMockMvc
            .perform(get(ENTITY_API_URL_ID, sale.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sale.getId().intValue()))
            .andExpect(jsonPath("$.productName").value(DEFAULT_PRODUCT_NAME))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.unitPrice").value(sameNumber(DEFAULT_UNIT_PRICE)))
            .andExpect(jsonPath("$.discount").value(sameNumber(DEFAULT_DISCOUNT)))
            .andExpect(jsonPath("$.lineTotal").value(sameNumber(DEFAULT_LINE_TOTAL)))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getSalesByIdFiltering() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        Long id = sale.getId();

        defaultSaleFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSaleFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSaleFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSalesByProductNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where productName equals to
        defaultSaleFiltering("productName.equals=" + DEFAULT_PRODUCT_NAME, "productName.equals=" + UPDATED_PRODUCT_NAME);
    }

    @Test
    @Transactional
    void getAllSalesByProductNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where productName in
        defaultSaleFiltering(
            "productName.in=" + DEFAULT_PRODUCT_NAME + "," + UPDATED_PRODUCT_NAME,
            "productName.in=" + UPDATED_PRODUCT_NAME
        );
    }

    @Test
    @Transactional
    void getAllSalesByProductNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where productName is not null
        defaultSaleFiltering("productName.specified=true", "productName.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesByProductNameContainsSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where productName contains
        defaultSaleFiltering("productName.contains=" + DEFAULT_PRODUCT_NAME, "productName.contains=" + UPDATED_PRODUCT_NAME);
    }

    @Test
    @Transactional
    void getAllSalesByProductNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where productName does not contain
        defaultSaleFiltering("productName.doesNotContain=" + UPDATED_PRODUCT_NAME, "productName.doesNotContain=" + DEFAULT_PRODUCT_NAME);
    }

    @Test
    @Transactional
    void getAllSalesByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where quantity equals to
        defaultSaleFiltering("quantity.equals=" + DEFAULT_QUANTITY, "quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSalesByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where quantity in
        defaultSaleFiltering("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY, "quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSalesByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where quantity is not null
        defaultSaleFiltering("quantity.specified=true", "quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where quantity is greater than or equal to
        defaultSaleFiltering("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY, "quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSalesByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where quantity is less than or equal to
        defaultSaleFiltering("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY, "quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSalesByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where quantity is less than
        defaultSaleFiltering("quantity.lessThan=" + UPDATED_QUANTITY, "quantity.lessThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSalesByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where quantity is greater than
        defaultSaleFiltering("quantity.greaterThan=" + SMALLER_QUANTITY, "quantity.greaterThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSalesByUnitPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where unitPrice equals to
        defaultSaleFiltering("unitPrice.equals=" + DEFAULT_UNIT_PRICE, "unitPrice.equals=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllSalesByUnitPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where unitPrice in
        defaultSaleFiltering("unitPrice.in=" + DEFAULT_UNIT_PRICE + "," + UPDATED_UNIT_PRICE, "unitPrice.in=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllSalesByUnitPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where unitPrice is not null
        defaultSaleFiltering("unitPrice.specified=true", "unitPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesByUnitPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where unitPrice is greater than or equal to
        defaultSaleFiltering("unitPrice.greaterThanOrEqual=" + DEFAULT_UNIT_PRICE, "unitPrice.greaterThanOrEqual=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllSalesByUnitPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where unitPrice is less than or equal to
        defaultSaleFiltering("unitPrice.lessThanOrEqual=" + DEFAULT_UNIT_PRICE, "unitPrice.lessThanOrEqual=" + SMALLER_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllSalesByUnitPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where unitPrice is less than
        defaultSaleFiltering("unitPrice.lessThan=" + UPDATED_UNIT_PRICE, "unitPrice.lessThan=" + DEFAULT_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllSalesByUnitPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where unitPrice is greater than
        defaultSaleFiltering("unitPrice.greaterThan=" + SMALLER_UNIT_PRICE, "unitPrice.greaterThan=" + DEFAULT_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllSalesByDiscountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where discount equals to
        defaultSaleFiltering("discount.equals=" + DEFAULT_DISCOUNT, "discount.equals=" + UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllSalesByDiscountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where discount in
        defaultSaleFiltering("discount.in=" + DEFAULT_DISCOUNT + "," + UPDATED_DISCOUNT, "discount.in=" + UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllSalesByDiscountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where discount is not null
        defaultSaleFiltering("discount.specified=true", "discount.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesByDiscountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where discount is greater than or equal to
        defaultSaleFiltering("discount.greaterThanOrEqual=" + DEFAULT_DISCOUNT, "discount.greaterThanOrEqual=" + UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllSalesByDiscountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where discount is less than or equal to
        defaultSaleFiltering("discount.lessThanOrEqual=" + DEFAULT_DISCOUNT, "discount.lessThanOrEqual=" + SMALLER_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllSalesByDiscountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where discount is less than
        defaultSaleFiltering("discount.lessThan=" + UPDATED_DISCOUNT, "discount.lessThan=" + DEFAULT_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllSalesByDiscountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where discount is greater than
        defaultSaleFiltering("discount.greaterThan=" + SMALLER_DISCOUNT, "discount.greaterThan=" + DEFAULT_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllSalesByLineTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where lineTotal equals to
        defaultSaleFiltering("lineTotal.equals=" + DEFAULT_LINE_TOTAL, "lineTotal.equals=" + UPDATED_LINE_TOTAL);
    }

    @Test
    @Transactional
    void getAllSalesByLineTotalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where lineTotal in
        defaultSaleFiltering("lineTotal.in=" + DEFAULT_LINE_TOTAL + "," + UPDATED_LINE_TOTAL, "lineTotal.in=" + UPDATED_LINE_TOTAL);
    }

    @Test
    @Transactional
    void getAllSalesByLineTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where lineTotal is not null
        defaultSaleFiltering("lineTotal.specified=true", "lineTotal.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesByLineTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where lineTotal is greater than or equal to
        defaultSaleFiltering("lineTotal.greaterThanOrEqual=" + DEFAULT_LINE_TOTAL, "lineTotal.greaterThanOrEqual=" + UPDATED_LINE_TOTAL);
    }

    @Test
    @Transactional
    void getAllSalesByLineTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where lineTotal is less than or equal to
        defaultSaleFiltering("lineTotal.lessThanOrEqual=" + DEFAULT_LINE_TOTAL, "lineTotal.lessThanOrEqual=" + SMALLER_LINE_TOTAL);
    }

    @Test
    @Transactional
    void getAllSalesByLineTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where lineTotal is less than
        defaultSaleFiltering("lineTotal.lessThan=" + UPDATED_LINE_TOTAL, "lineTotal.lessThan=" + DEFAULT_LINE_TOTAL);
    }

    @Test
    @Transactional
    void getAllSalesByLineTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where lineTotal is greater than
        defaultSaleFiltering("lineTotal.greaterThan=" + SMALLER_LINE_TOTAL, "lineTotal.greaterThan=" + DEFAULT_LINE_TOTAL);
    }

    @Test
    @Transactional
    void getAllSalesByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where active equals to
        defaultSaleFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllSalesByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where active in
        defaultSaleFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllSalesByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where active is not null
        defaultSaleFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesBySaleOperationIsEqualToSomething() throws Exception {
        SaleOperation saleOperation;
        if (TestUtil.findAll(em, SaleOperation.class).isEmpty()) {
            saleRepository.saveAndFlush(sale);
            saleOperation = SaleOperationResourceIT.createEntity();
        } else {
            saleOperation = TestUtil.findAll(em, SaleOperation.class).get(0);
        }
        em.persist(saleOperation);
        em.flush();
        sale.setSaleOperation(saleOperation);
        saleRepository.saveAndFlush(sale);
        Long saleOperationId = saleOperation.getId();
        // Get all the saleList where saleOperation equals to saleOperationId
        defaultSaleShouldBeFound("saleOperationId.equals=" + saleOperationId);

        // Get all the saleList where saleOperation equals to (saleOperationId + 1)
        defaultSaleShouldNotBeFound("saleOperationId.equals=" + (saleOperationId + 1));
    }

    @Test
    @Transactional
    void getAllSalesByProductIsEqualToSomething() throws Exception {
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            saleRepository.saveAndFlush(sale);
            product = ProductResourceIT.createEntity();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        em.persist(product);
        em.flush();
        sale.setProduct(product);
        saleRepository.saveAndFlush(sale);
        Long productId = product.getId();
        // Get all the saleList where product equals to productId
        defaultSaleShouldBeFound("productId.equals=" + productId);

        // Get all the saleList where product equals to (productId + 1)
        defaultSaleShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    private void defaultSaleFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSaleShouldBeFound(shouldBeFound);
        defaultSaleShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSaleShouldBeFound(String filter) throws Exception {
        restSaleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sale.getId().intValue())))
            .andExpect(jsonPath("$.[*].productName").value(hasItem(DEFAULT_PRODUCT_NAME)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].discount").value(hasItem(sameNumber(DEFAULT_DISCOUNT))))
            .andExpect(jsonPath("$.[*].lineTotal").value(hasItem(sameNumber(DEFAULT_LINE_TOTAL))))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));

        // Check, that the count call also returns 1
        restSaleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSaleShouldNotBeFound(String filter) throws Exception {
        restSaleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSaleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSale() throws Exception {
        // Get the sale
        restSaleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSale() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sale
        Sale updatedSale = saleRepository.findById(sale.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSale are not directly saved in db
        em.detach(updatedSale);
        updatedSale
            .productName(UPDATED_PRODUCT_NAME)
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .discount(UPDATED_DISCOUNT)
            .lineTotal(UPDATED_LINE_TOTAL)
            .active(UPDATED_ACTIVE);
        SaleDTO saleDTO = saleMapper.toDto(updatedSale);

        restSaleMockMvc
            .perform(put(ENTITY_API_URL_ID, saleDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isOk());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSaleToMatchAllProperties(updatedSale);
    }

    @Test
    @Transactional
    void putNonExistingSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sale.setId(longCount.incrementAndGet());

        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleMockMvc
            .perform(put(ENTITY_API_URL_ID, saleDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sale.setId(longCount.incrementAndGet());

        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(saleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sale.setId(longCount.incrementAndGet());

        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSaleWithPatch() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sale using partial update
        Sale partialUpdatedSale = new Sale();
        partialUpdatedSale.setId(sale.getId());

        partialUpdatedSale
            .productName(UPDATED_PRODUCT_NAME)
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .lineTotal(UPDATED_LINE_TOTAL);

        restSaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSale.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSale))
            )
            .andExpect(status().isOk());

        // Validate the Sale in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSaleUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSale, sale), getPersistedSale(sale));
    }

    @Test
    @Transactional
    void fullUpdateSaleWithPatch() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sale using partial update
        Sale partialUpdatedSale = new Sale();
        partialUpdatedSale.setId(sale.getId());

        partialUpdatedSale
            .productName(UPDATED_PRODUCT_NAME)
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .discount(UPDATED_DISCOUNT)
            .lineTotal(UPDATED_LINE_TOTAL)
            .active(UPDATED_ACTIVE);

        restSaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSale.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSale))
            )
            .andExpect(status().isOk());

        // Validate the Sale in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSaleUpdatableFieldsEquals(partialUpdatedSale, getPersistedSale(partialUpdatedSale));
    }

    @Test
    @Transactional
    void patchNonExistingSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sale.setId(longCount.incrementAndGet());

        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, saleDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(saleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sale.setId(longCount.incrementAndGet());

        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(saleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sale.setId(longCount.incrementAndGet());

        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSale() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the sale
        restSaleMockMvc
            .perform(delete(ENTITY_API_URL_ID, sale.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return saleRepository.count();
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

    protected Sale getPersistedSale(Sale sale) {
        return saleRepository.findById(sale.getId()).orElseThrow();
    }

    protected void assertPersistedSaleToMatchAllProperties(Sale expectedSale) {
        assertSaleAllPropertiesEquals(expectedSale, getPersistedSale(expectedSale));
    }

    protected void assertPersistedSaleToMatchUpdatableProperties(Sale expectedSale) {
        assertSaleAllUpdatablePropertiesEquals(expectedSale, getPersistedSale(expectedSale));
    }
}
