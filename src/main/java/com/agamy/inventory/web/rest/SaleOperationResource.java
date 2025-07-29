package com.agamy.inventory.web.rest;

import com.agamy.inventory.repository.SaleOperationRepository;
import com.agamy.inventory.service.SaleOperationQueryService;
import com.agamy.inventory.service.SaleOperationService;
import com.agamy.inventory.service.criteria.SaleOperationCriteria;
import com.agamy.inventory.service.dto.SaleOperationDTO;
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
 * REST controller for managing {@link com.agamy.inventory.domain.SaleOperation}.
 */
@RestController
@RequestMapping("/api/sale-operations")
public class SaleOperationResource {

    private static final Logger LOG = LoggerFactory.getLogger(SaleOperationResource.class);

    private static final String ENTITY_NAME = "saleOperation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SaleOperationService saleOperationService;

    private final SaleOperationRepository saleOperationRepository;

    private final SaleOperationQueryService saleOperationQueryService;

    public SaleOperationResource(
        SaleOperationService saleOperationService,
        SaleOperationRepository saleOperationRepository,
        SaleOperationQueryService saleOperationQueryService
    ) {
        this.saleOperationService = saleOperationService;
        this.saleOperationRepository = saleOperationRepository;
        this.saleOperationQueryService = saleOperationQueryService;
    }

    /**
     * {@code POST  /sale-operations} : Create a new saleOperation.
     *
     * @param saleOperationDTO the saleOperationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new saleOperationDTO, or with status {@code 400 (Bad Request)} if the saleOperation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SaleOperationDTO> createSaleOperation(@Valid @RequestBody SaleOperationDTO saleOperationDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save SaleOperation : {}", saleOperationDTO);
        if (saleOperationDTO.getId() != null) {
            throw new BadRequestAlertException("A new saleOperation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        saleOperationDTO = saleOperationService.save(saleOperationDTO);
        return ResponseEntity.created(new URI("/api/sale-operations/" + saleOperationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, saleOperationDTO.getId().toString()))
            .body(saleOperationDTO);
    }

    /**
     * {@code PUT  /sale-operations/:id} : Updates an existing saleOperation.
     *
     * @param id the id of the saleOperationDTO to save.
     * @param saleOperationDTO the saleOperationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated saleOperationDTO,
     * or with status {@code 400 (Bad Request)} if the saleOperationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the saleOperationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SaleOperationDTO> updateSaleOperation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SaleOperationDTO saleOperationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SaleOperation : {}, {}", id, saleOperationDTO);
        if (saleOperationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, saleOperationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!saleOperationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        saleOperationDTO = saleOperationService.update(saleOperationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, saleOperationDTO.getId().toString()))
            .body(saleOperationDTO);
    }

    /**
     * {@code PATCH  /sale-operations/:id} : Partial updates given fields of an existing saleOperation, field will ignore if it is null
     *
     * @param id the id of the saleOperationDTO to save.
     * @param saleOperationDTO the saleOperationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated saleOperationDTO,
     * or with status {@code 400 (Bad Request)} if the saleOperationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the saleOperationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the saleOperationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SaleOperationDTO> partialUpdateSaleOperation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SaleOperationDTO saleOperationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SaleOperation partially : {}, {}", id, saleOperationDTO);
        if (saleOperationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, saleOperationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!saleOperationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SaleOperationDTO> result = saleOperationService.partialUpdate(saleOperationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, saleOperationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sale-operations} : get all the saleOperations.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of saleOperations in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SaleOperationDTO>> getAllSaleOperations(
        SaleOperationCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get SaleOperations by criteria: {}", criteria);

        Page<SaleOperationDTO> page = saleOperationQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sale-operations/count} : count all the saleOperations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSaleOperations(SaleOperationCriteria criteria) {
        LOG.debug("REST request to count SaleOperations by criteria: {}", criteria);
        return ResponseEntity.ok().body(saleOperationQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /sale-operations/:id} : get the "id" saleOperation.
     *
     * @param id the id of the saleOperationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the saleOperationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SaleOperationDTO> getSaleOperation(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SaleOperation : {}", id);
        Optional<SaleOperationDTO> saleOperationDTO = saleOperationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(saleOperationDTO);
    }

    /**
     * {@code DELETE  /sale-operations/:id} : delete the "id" saleOperation.
     *
     * @param id the id of the saleOperationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSaleOperation(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SaleOperation : {}", id);
        saleOperationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
