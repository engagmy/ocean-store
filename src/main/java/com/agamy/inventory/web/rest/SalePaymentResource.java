package com.agamy.inventory.web.rest;

import com.agamy.inventory.repository.SalePaymentRepository;
import com.agamy.inventory.service.SalePaymentQueryService;
import com.agamy.inventory.service.SalePaymentService;
import com.agamy.inventory.service.criteria.SalePaymentCriteria;
import com.agamy.inventory.service.dto.SalePaymentDTO;
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
 * REST controller for managing {@link com.agamy.inventory.domain.SalePayment}.
 */
@RestController
@RequestMapping("/api/sale-payments")
public class SalePaymentResource {

    private static final Logger LOG = LoggerFactory.getLogger(SalePaymentResource.class);

    private static final String ENTITY_NAME = "salePayment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SalePaymentService salePaymentService;

    private final SalePaymentRepository salePaymentRepository;

    private final SalePaymentQueryService salePaymentQueryService;

    public SalePaymentResource(
        SalePaymentService salePaymentService,
        SalePaymentRepository salePaymentRepository,
        SalePaymentQueryService salePaymentQueryService
    ) {
        this.salePaymentService = salePaymentService;
        this.salePaymentRepository = salePaymentRepository;
        this.salePaymentQueryService = salePaymentQueryService;
    }

    /**
     * {@code POST  /sale-payments} : Create a new salePayment.
     *
     * @param salePaymentDTO the salePaymentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new salePaymentDTO, or with status {@code 400 (Bad Request)} if the salePayment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SalePaymentDTO> createSalePayment(@Valid @RequestBody SalePaymentDTO salePaymentDTO) throws URISyntaxException {
        LOG.debug("REST request to save SalePayment : {}", salePaymentDTO);
        if (salePaymentDTO.getId() != null) {
            throw new BadRequestAlertException("A new salePayment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        salePaymentDTO = salePaymentService.save(salePaymentDTO);
        return ResponseEntity.created(new URI("/api/sale-payments/" + salePaymentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, salePaymentDTO.getId().toString()))
            .body(salePaymentDTO);
    }

    /**
     * {@code PUT  /sale-payments/:id} : Updates an existing salePayment.
     *
     * @param id the id of the salePaymentDTO to save.
     * @param salePaymentDTO the salePaymentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salePaymentDTO,
     * or with status {@code 400 (Bad Request)} if the salePaymentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the salePaymentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SalePaymentDTO> updateSalePayment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SalePaymentDTO salePaymentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SalePayment : {}, {}", id, salePaymentDTO);
        if (salePaymentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salePaymentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salePaymentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        salePaymentDTO = salePaymentService.update(salePaymentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, salePaymentDTO.getId().toString()))
            .body(salePaymentDTO);
    }

    /**
     * {@code PATCH  /sale-payments/:id} : Partial updates given fields of an existing salePayment, field will ignore if it is null
     *
     * @param id the id of the salePaymentDTO to save.
     * @param salePaymentDTO the salePaymentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salePaymentDTO,
     * or with status {@code 400 (Bad Request)} if the salePaymentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the salePaymentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the salePaymentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SalePaymentDTO> partialUpdateSalePayment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SalePaymentDTO salePaymentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SalePayment partially : {}, {}", id, salePaymentDTO);
        if (salePaymentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salePaymentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salePaymentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SalePaymentDTO> result = salePaymentService.partialUpdate(salePaymentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, salePaymentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sale-payments} : get all the salePayments.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of salePayments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SalePaymentDTO>> getAllSalePayments(
        SalePaymentCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get SalePayments by criteria: {}", criteria);

        Page<SalePaymentDTO> page = salePaymentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sale-payments/count} : count all the salePayments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSalePayments(SalePaymentCriteria criteria) {
        LOG.debug("REST request to count SalePayments by criteria: {}", criteria);
        return ResponseEntity.ok().body(salePaymentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /sale-payments/:id} : get the "id" salePayment.
     *
     * @param id the id of the salePaymentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the salePaymentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SalePaymentDTO> getSalePayment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SalePayment : {}", id);
        Optional<SalePaymentDTO> salePaymentDTO = salePaymentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(salePaymentDTO);
    }

    /**
     * {@code DELETE  /sale-payments/:id} : delete the "id" salePayment.
     *
     * @param id the id of the salePaymentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalePayment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SalePayment : {}", id);
        salePaymentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
