package com.agamy.inventory.web.rest;

import com.agamy.inventory.repository.PurchasePaymentRepository;
import com.agamy.inventory.service.PurchasePaymentQueryService;
import com.agamy.inventory.service.PurchasePaymentService;
import com.agamy.inventory.service.criteria.PurchasePaymentCriteria;
import com.agamy.inventory.service.dto.PurchasePaymentDTO;
import com.agamy.inventory.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.agamy.inventory.domain.PurchasePayment}.
 */
@RestController
@RequestMapping("/api/purchase-payments")
public class PurchasePaymentResource {

    private static final Logger LOG = LoggerFactory.getLogger(PurchasePaymentResource.class);

    private static final String ENTITY_NAME = "purchasePayment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PurchasePaymentService purchasePaymentService;

    private final PurchasePaymentRepository purchasePaymentRepository;

    private final PurchasePaymentQueryService purchasePaymentQueryService;

    public PurchasePaymentResource(
        PurchasePaymentService purchasePaymentService,
        PurchasePaymentRepository purchasePaymentRepository,
        PurchasePaymentQueryService purchasePaymentQueryService
    ) {
        this.purchasePaymentService = purchasePaymentService;
        this.purchasePaymentRepository = purchasePaymentRepository;
        this.purchasePaymentQueryService = purchasePaymentQueryService;
    }

    /**
     * {@code POST  /purchase-payments} : Create a new purchasePayment.
     *
     * @param purchasePaymentDTO the purchasePaymentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new purchasePaymentDTO, or with status {@code 400 (Bad Request)} if the purchasePayment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PurchasePaymentDTO> createPurchasePayment(@Valid @RequestBody PurchasePaymentDTO purchasePaymentDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PurchasePayment : {}", purchasePaymentDTO);
        if (purchasePaymentDTO.getId() != null) {
            throw new BadRequestAlertException("A new purchasePayment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        purchasePaymentDTO = purchasePaymentService.save(purchasePaymentDTO);
        return ResponseEntity.created(new URI("/api/purchase-payments/" + purchasePaymentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, purchasePaymentDTO.getId().toString()))
            .body(purchasePaymentDTO);
    }

    /**
     * {@code PUT  /purchase-payments/:id} : Updates an existing purchasePayment.
     *
     * @param id the id of the purchasePaymentDTO to save.
     * @param purchasePaymentDTO the purchasePaymentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated purchasePaymentDTO,
     * or with status {@code 400 (Bad Request)} if the purchasePaymentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the purchasePaymentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PurchasePaymentDTO> updatePurchasePayment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PurchasePaymentDTO purchasePaymentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PurchasePayment : {}, {}", id, purchasePaymentDTO);
        if (purchasePaymentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchasePaymentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!purchasePaymentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        purchasePaymentDTO = purchasePaymentService.update(purchasePaymentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, purchasePaymentDTO.getId().toString()))
            .body(purchasePaymentDTO);
    }

    /**
     * {@code PATCH  /purchase-payments/:id} : Partial updates given fields of an existing purchasePayment, field will ignore if it is null
     *
     * @param id the id of the purchasePaymentDTO to save.
     * @param purchasePaymentDTO the purchasePaymentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated purchasePaymentDTO,
     * or with status {@code 400 (Bad Request)} if the purchasePaymentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the purchasePaymentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the purchasePaymentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PurchasePaymentDTO> partialUpdatePurchasePayment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PurchasePaymentDTO purchasePaymentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PurchasePayment partially : {}, {}", id, purchasePaymentDTO);
        if (purchasePaymentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchasePaymentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!purchasePaymentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PurchasePaymentDTO> result = purchasePaymentService.partialUpdate(purchasePaymentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, purchasePaymentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /purchase-payments} : get all the purchasePayments.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of purchasePayments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PurchasePaymentDTO>> getAllPurchasePayments(
        PurchasePaymentCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get PurchasePayments by criteria: {}", criteria);

        Page<PurchasePaymentDTO> page = purchasePaymentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /purchase-payments/count} : count all the purchasePayments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPurchasePayments(PurchasePaymentCriteria criteria) {
        LOG.debug("REST request to count PurchasePayments by criteria: {}", criteria);
        return ResponseEntity.ok().body(purchasePaymentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /purchase-payments/:id} : get the "id" purchasePayment.
     *
     * @param id the id of the purchasePaymentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the purchasePaymentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PurchasePaymentDTO> getPurchasePayment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PurchasePayment : {}", id);
        Optional<PurchasePaymentDTO> purchasePaymentDTO = purchasePaymentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(purchasePaymentDTO);
    }

    /**
     * {@code DELETE  /purchase-payments/:id} : delete the "id" purchasePayment.
     *
     * @param id the id of the purchasePaymentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchasePayment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PurchasePayment : {}", id);
        purchasePaymentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
