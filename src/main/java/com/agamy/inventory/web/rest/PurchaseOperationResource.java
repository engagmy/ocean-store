package com.agamy.inventory.web.rest;

import com.agamy.inventory.repository.PurchaseOperationRepository;
import com.agamy.inventory.service.PurchaseOperationQueryService;
import com.agamy.inventory.service.PurchaseOperationService;
import com.agamy.inventory.service.criteria.PurchaseOperationCriteria;
import com.agamy.inventory.service.dto.PurchaseOperationDTO;
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
 * REST controller for managing {@link com.agamy.inventory.domain.PurchaseOperation}.
 */
@RestController
@RequestMapping("/api/purchase-operations")
public class PurchaseOperationResource {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOperationResource.class);

    private static final String ENTITY_NAME = "purchaseOperation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PurchaseOperationService purchaseOperationService;

    private final PurchaseOperationRepository purchaseOperationRepository;

    private final PurchaseOperationQueryService purchaseOperationQueryService;

    public PurchaseOperationResource(
        PurchaseOperationService purchaseOperationService,
        PurchaseOperationRepository purchaseOperationRepository,
        PurchaseOperationQueryService purchaseOperationQueryService
    ) {
        this.purchaseOperationService = purchaseOperationService;
        this.purchaseOperationRepository = purchaseOperationRepository;
        this.purchaseOperationQueryService = purchaseOperationQueryService;
    }

    /**
     * {@code POST  /purchase-operations} : Create a new purchaseOperation.
     *
     * @param purchaseOperationDTO the purchaseOperationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new purchaseOperationDTO, or with status {@code 400 (Bad Request)} if the purchaseOperation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PurchaseOperationDTO> createPurchaseOperation(@Valid @RequestBody PurchaseOperationDTO purchaseOperationDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PurchaseOperation : {}", purchaseOperationDTO);
        if (purchaseOperationDTO.getId() != null) {
            throw new BadRequestAlertException("A new purchaseOperation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        purchaseOperationDTO = purchaseOperationService.save(purchaseOperationDTO);
        return ResponseEntity.created(new URI("/api/purchase-operations/" + purchaseOperationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, purchaseOperationDTO.getId().toString()))
            .body(purchaseOperationDTO);
    }

    /**
     * {@code PUT  /purchase-operations/:id} : Updates an existing purchaseOperation.
     *
     * @param id the id of the purchaseOperationDTO to save.
     * @param purchaseOperationDTO the purchaseOperationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated purchaseOperationDTO,
     * or with status {@code 400 (Bad Request)} if the purchaseOperationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the purchaseOperationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PurchaseOperationDTO> updatePurchaseOperation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PurchaseOperationDTO purchaseOperationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PurchaseOperation : {}, {}", id, purchaseOperationDTO);
        if (purchaseOperationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchaseOperationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!purchaseOperationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        purchaseOperationDTO = purchaseOperationService.update(purchaseOperationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, purchaseOperationDTO.getId().toString()))
            .body(purchaseOperationDTO);
    }

    /**
     * {@code PATCH  /purchase-operations/:id} : Partial updates given fields of an existing purchaseOperation, field will ignore if it is null
     *
     * @param id the id of the purchaseOperationDTO to save.
     * @param purchaseOperationDTO the purchaseOperationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated purchaseOperationDTO,
     * or with status {@code 400 (Bad Request)} if the purchaseOperationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the purchaseOperationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the purchaseOperationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PurchaseOperationDTO> partialUpdatePurchaseOperation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PurchaseOperationDTO purchaseOperationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PurchaseOperation partially : {}, {}", id, purchaseOperationDTO);
        if (purchaseOperationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchaseOperationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!purchaseOperationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PurchaseOperationDTO> result = purchaseOperationService.partialUpdate(purchaseOperationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, purchaseOperationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /purchase-operations} : get all the purchaseOperations.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of purchaseOperations in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PurchaseOperationDTO>> getAllPurchaseOperations(
        PurchaseOperationCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get PurchaseOperations by criteria: {}", criteria);

        Page<PurchaseOperationDTO> page = purchaseOperationQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /purchase-operations/count} : count all the purchaseOperations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPurchaseOperations(PurchaseOperationCriteria criteria) {
        LOG.debug("REST request to count PurchaseOperations by criteria: {}", criteria);
        return ResponseEntity.ok().body(purchaseOperationQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /purchase-operations/:id} : get the "id" purchaseOperation.
     *
     * @param id the id of the purchaseOperationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the purchaseOperationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOperationDTO> getPurchaseOperation(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PurchaseOperation : {}", id);
        Optional<PurchaseOperationDTO> purchaseOperationDTO = purchaseOperationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(purchaseOperationDTO);
    }

    /**
     * {@code DELETE  /purchase-operations/:id} : delete the "id" purchaseOperation.
     *
     * @param id the id of the purchaseOperationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchaseOperation(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PurchaseOperation : {}", id);
        purchaseOperationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
