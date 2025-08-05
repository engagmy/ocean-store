package com.agamy.inventory.web.rest;

import com.agamy.inventory.repository.SalaryPaymentRepository;
import com.agamy.inventory.service.SalaryPaymentQueryService;
import com.agamy.inventory.service.SalaryPaymentService;
import com.agamy.inventory.service.criteria.SalaryPaymentCriteria;
import com.agamy.inventory.service.dto.SalaryPaymentDTO;
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
 * REST controller for managing {@link com.agamy.inventory.domain.SalaryPayment}.
 */
@RestController
@RequestMapping("/api/salary-payments")
public class SalaryPaymentResource {

    private static final Logger LOG = LoggerFactory.getLogger(SalaryPaymentResource.class);

    private static final String ENTITY_NAME = "salaryPayment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SalaryPaymentService salaryPaymentService;

    private final SalaryPaymentRepository salaryPaymentRepository;

    private final SalaryPaymentQueryService salaryPaymentQueryService;

    public SalaryPaymentResource(
        SalaryPaymentService salaryPaymentService,
        SalaryPaymentRepository salaryPaymentRepository,
        SalaryPaymentQueryService salaryPaymentQueryService
    ) {
        this.salaryPaymentService = salaryPaymentService;
        this.salaryPaymentRepository = salaryPaymentRepository;
        this.salaryPaymentQueryService = salaryPaymentQueryService;
    }

    /**
     * {@code POST  /salary-payments} : Create a new salaryPayment.
     *
     * @param salaryPaymentDTO the salaryPaymentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new salaryPaymentDTO, or with status {@code 400 (Bad Request)} if the salaryPayment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SalaryPaymentDTO> createSalaryPayment(@Valid @RequestBody SalaryPaymentDTO salaryPaymentDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save SalaryPayment : {}", salaryPaymentDTO);
        if (salaryPaymentDTO.getId() != null) {
            throw new BadRequestAlertException("A new salaryPayment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        salaryPaymentDTO = salaryPaymentService.save(salaryPaymentDTO);
        return ResponseEntity.created(new URI("/api/salary-payments/" + salaryPaymentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, salaryPaymentDTO.getId().toString()))
            .body(salaryPaymentDTO);
    }

    /**
     * {@code PUT  /salary-payments/:id} : Updates an existing salaryPayment.
     *
     * @param id the id of the salaryPaymentDTO to save.
     * @param salaryPaymentDTO the salaryPaymentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salaryPaymentDTO,
     * or with status {@code 400 (Bad Request)} if the salaryPaymentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the salaryPaymentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SalaryPaymentDTO> updateSalaryPayment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SalaryPaymentDTO salaryPaymentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SalaryPayment : {}, {}", id, salaryPaymentDTO);
        if (salaryPaymentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salaryPaymentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salaryPaymentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        salaryPaymentDTO = salaryPaymentService.update(salaryPaymentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, salaryPaymentDTO.getId().toString()))
            .body(salaryPaymentDTO);
    }

    /**
     * {@code PATCH  /salary-payments/:id} : Partial updates given fields of an existing salaryPayment, field will ignore if it is null
     *
     * @param id the id of the salaryPaymentDTO to save.
     * @param salaryPaymentDTO the salaryPaymentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salaryPaymentDTO,
     * or with status {@code 400 (Bad Request)} if the salaryPaymentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the salaryPaymentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the salaryPaymentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SalaryPaymentDTO> partialUpdateSalaryPayment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SalaryPaymentDTO salaryPaymentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SalaryPayment partially : {}, {}", id, salaryPaymentDTO);
        if (salaryPaymentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salaryPaymentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salaryPaymentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SalaryPaymentDTO> result = salaryPaymentService.partialUpdate(salaryPaymentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, salaryPaymentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /salary-payments} : get all the salaryPayments.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of salaryPayments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SalaryPaymentDTO>> getAllSalaryPayments(
        SalaryPaymentCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get SalaryPayments by criteria: {}", criteria);

        Page<SalaryPaymentDTO> page = salaryPaymentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /salary-payments/count} : count all the salaryPayments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSalaryPayments(SalaryPaymentCriteria criteria) {
        LOG.debug("REST request to count SalaryPayments by criteria: {}", criteria);
        return ResponseEntity.ok().body(salaryPaymentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /salary-payments/:id} : get the "id" salaryPayment.
     *
     * @param id the id of the salaryPaymentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the salaryPaymentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SalaryPaymentDTO> getSalaryPayment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SalaryPayment : {}", id);
        Optional<SalaryPaymentDTO> salaryPaymentDTO = salaryPaymentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(salaryPaymentDTO);
    }

    /**
     * {@code DELETE  /salary-payments/:id} : delete the "id" salaryPayment.
     *
     * @param id the id of the salaryPaymentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalaryPayment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SalaryPayment : {}", id);
        salaryPaymentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
