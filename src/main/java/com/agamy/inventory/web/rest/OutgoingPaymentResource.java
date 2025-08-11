package com.agamy.inventory.web.rest;

import com.agamy.inventory.repository.OutgoingPaymentRepository;
import com.agamy.inventory.service.OutgoingPaymentQueryService;
import com.agamy.inventory.service.OutgoingPaymentService;
import com.agamy.inventory.service.criteria.OutgoingPaymentCriteria;
import com.agamy.inventory.service.dto.OutgoingPaymentDTO;
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
 * REST controller for managing {@link com.agamy.inventory.domain.OutgoingPayment}.
 */
@RestController
@RequestMapping("/api/outgoing-payments")
public class OutgoingPaymentResource {

    private static final Logger LOG = LoggerFactory.getLogger(OutgoingPaymentResource.class);

    private static final String ENTITY_NAME = "outgoingPayment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OutgoingPaymentService outgoingPaymentService;

    private final OutgoingPaymentRepository outgoingPaymentRepository;

    private final OutgoingPaymentQueryService outgoingPaymentQueryService;

    public OutgoingPaymentResource(
        OutgoingPaymentService outgoingPaymentService,
        OutgoingPaymentRepository outgoingPaymentRepository,
        OutgoingPaymentQueryService outgoingPaymentQueryService
    ) {
        this.outgoingPaymentService = outgoingPaymentService;
        this.outgoingPaymentRepository = outgoingPaymentRepository;
        this.outgoingPaymentQueryService = outgoingPaymentQueryService;
    }

    /**
     * {@code POST  /outgoing-payments} : Create a new outgoingPayment.
     *
     * @param outgoingPaymentDTO the outgoingPaymentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new outgoingPaymentDTO, or with status {@code 400 (Bad Request)} if the outgoingPayment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<OutgoingPaymentDTO> createOutgoingPayment(@Valid @RequestBody OutgoingPaymentDTO outgoingPaymentDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save OutgoingPayment : {}", outgoingPaymentDTO);
        if (outgoingPaymentDTO.getId() != null) {
            throw new BadRequestAlertException("A new outgoingPayment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        outgoingPaymentDTO = outgoingPaymentService.save(outgoingPaymentDTO);
        return ResponseEntity.created(new URI("/api/outgoing-payments/" + outgoingPaymentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, outgoingPaymentDTO.getId().toString()))
            .body(outgoingPaymentDTO);
    }

    /**
     * {@code PUT  /outgoing-payments/:id} : Updates an existing outgoingPayment.
     *
     * @param id the id of the outgoingPaymentDTO to save.
     * @param outgoingPaymentDTO the outgoingPaymentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated outgoingPaymentDTO,
     * or with status {@code 400 (Bad Request)} if the outgoingPaymentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the outgoingPaymentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OutgoingPaymentDTO> updateOutgoingPayment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OutgoingPaymentDTO outgoingPaymentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update OutgoingPayment : {}, {}", id, outgoingPaymentDTO);
        if (outgoingPaymentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, outgoingPaymentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!outgoingPaymentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        outgoingPaymentDTO = outgoingPaymentService.update(outgoingPaymentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, outgoingPaymentDTO.getId().toString()))
            .body(outgoingPaymentDTO);
    }

    /**
     * {@code PATCH  /outgoing-payments/:id} : Partial updates given fields of an existing outgoingPayment, field will ignore if it is null
     *
     * @param id the id of the outgoingPaymentDTO to save.
     * @param outgoingPaymentDTO the outgoingPaymentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated outgoingPaymentDTO,
     * or with status {@code 400 (Bad Request)} if the outgoingPaymentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the outgoingPaymentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the outgoingPaymentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OutgoingPaymentDTO> partialUpdateOutgoingPayment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OutgoingPaymentDTO outgoingPaymentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update OutgoingPayment partially : {}, {}", id, outgoingPaymentDTO);
        if (outgoingPaymentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, outgoingPaymentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!outgoingPaymentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OutgoingPaymentDTO> result = outgoingPaymentService.partialUpdate(outgoingPaymentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, outgoingPaymentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /outgoing-payments} : get all the outgoingPayments.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of outgoingPayments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<OutgoingPaymentDTO>> getAllOutgoingPayments(
        OutgoingPaymentCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get OutgoingPayments by criteria: {}", criteria);

        Page<OutgoingPaymentDTO> page = outgoingPaymentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /outgoing-payments/count} : count all the outgoingPayments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countOutgoingPayments(OutgoingPaymentCriteria criteria) {
        LOG.debug("REST request to count OutgoingPayments by criteria: {}", criteria);
        return ResponseEntity.ok().body(outgoingPaymentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /outgoing-payments/:id} : get the "id" outgoingPayment.
     *
     * @param id the id of the outgoingPaymentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the outgoingPaymentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OutgoingPaymentDTO> getOutgoingPayment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get OutgoingPayment : {}", id);
        Optional<OutgoingPaymentDTO> outgoingPaymentDTO = outgoingPaymentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(outgoingPaymentDTO);
    }

    /**
     * {@code DELETE  /outgoing-payments/:id} : delete the "id" outgoingPayment.
     *
     * @param id the id of the outgoingPaymentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOutgoingPayment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete OutgoingPayment : {}", id);
        outgoingPaymentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
