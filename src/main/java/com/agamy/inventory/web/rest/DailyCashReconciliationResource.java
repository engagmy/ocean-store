package com.agamy.inventory.web.rest;

import com.agamy.inventory.repository.DailyCashReconciliationRepository;
import com.agamy.inventory.service.DailyCashReconciliationQueryService;
import com.agamy.inventory.service.DailyCashReconciliationService;
import com.agamy.inventory.service.criteria.DailyCashReconciliationCriteria;
import com.agamy.inventory.service.dto.DailyCashReconciliationDTO;
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
 * REST controller for managing {@link com.agamy.inventory.domain.DailyCashReconciliation}.
 */
@RestController
@RequestMapping("/api/daily-cash-reconciliations")
public class DailyCashReconciliationResource {

    private static final Logger LOG = LoggerFactory.getLogger(DailyCashReconciliationResource.class);

    private static final String ENTITY_NAME = "dailyCashReconciliation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DailyCashReconciliationService dailyCashReconciliationService;

    private final DailyCashReconciliationRepository dailyCashReconciliationRepository;

    private final DailyCashReconciliationQueryService dailyCashReconciliationQueryService;

    public DailyCashReconciliationResource(
        DailyCashReconciliationService dailyCashReconciliationService,
        DailyCashReconciliationRepository dailyCashReconciliationRepository,
        DailyCashReconciliationQueryService dailyCashReconciliationQueryService
    ) {
        this.dailyCashReconciliationService = dailyCashReconciliationService;
        this.dailyCashReconciliationRepository = dailyCashReconciliationRepository;
        this.dailyCashReconciliationQueryService = dailyCashReconciliationQueryService;
    }

    /**
     * {@code POST  /daily-cash-reconciliations} : Create a new dailyCashReconciliation.
     *
     * @param dailyCashReconciliationDTO the dailyCashReconciliationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dailyCashReconciliationDTO, or with status {@code 400 (Bad Request)} if the dailyCashReconciliation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DailyCashReconciliationDTO> createDailyCashReconciliation(
        @Valid @RequestBody DailyCashReconciliationDTO dailyCashReconciliationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save DailyCashReconciliation : {}", dailyCashReconciliationDTO);
        if (dailyCashReconciliationDTO.getId() != null) {
            throw new BadRequestAlertException("A new dailyCashReconciliation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        dailyCashReconciliationDTO = dailyCashReconciliationService.save(dailyCashReconciliationDTO);
        return ResponseEntity.created(new URI("/api/daily-cash-reconciliations/" + dailyCashReconciliationDTO.getId()))
            .headers(
                HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, dailyCashReconciliationDTO.getId().toString())
            )
            .body(dailyCashReconciliationDTO);
    }

    /**
     * {@code PUT  /daily-cash-reconciliations/:id} : Updates an existing dailyCashReconciliation.
     *
     * @param id the id of the dailyCashReconciliationDTO to save.
     * @param dailyCashReconciliationDTO the dailyCashReconciliationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dailyCashReconciliationDTO,
     * or with status {@code 400 (Bad Request)} if the dailyCashReconciliationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dailyCashReconciliationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DailyCashReconciliationDTO> updateDailyCashReconciliation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DailyCashReconciliationDTO dailyCashReconciliationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DailyCashReconciliation : {}, {}", id, dailyCashReconciliationDTO);
        if (dailyCashReconciliationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dailyCashReconciliationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dailyCashReconciliationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        dailyCashReconciliationDTO = dailyCashReconciliationService.update(dailyCashReconciliationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dailyCashReconciliationDTO.getId().toString()))
            .body(dailyCashReconciliationDTO);
    }

    /**
     * {@code PATCH  /daily-cash-reconciliations/:id} : Partial updates given fields of an existing dailyCashReconciliation, field will ignore if it is null
     *
     * @param id the id of the dailyCashReconciliationDTO to save.
     * @param dailyCashReconciliationDTO the dailyCashReconciliationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dailyCashReconciliationDTO,
     * or with status {@code 400 (Bad Request)} if the dailyCashReconciliationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the dailyCashReconciliationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the dailyCashReconciliationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DailyCashReconciliationDTO> partialUpdateDailyCashReconciliation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DailyCashReconciliationDTO dailyCashReconciliationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DailyCashReconciliation partially : {}, {}", id, dailyCashReconciliationDTO);
        if (dailyCashReconciliationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dailyCashReconciliationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dailyCashReconciliationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DailyCashReconciliationDTO> result = dailyCashReconciliationService.partialUpdate(dailyCashReconciliationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dailyCashReconciliationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /daily-cash-reconciliations} : get all the dailyCashReconciliations.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dailyCashReconciliations in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DailyCashReconciliationDTO>> getAllDailyCashReconciliations(
        DailyCashReconciliationCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get DailyCashReconciliations by criteria: {}", criteria);

        Page<DailyCashReconciliationDTO> page = dailyCashReconciliationQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /daily-cash-reconciliations/count} : count all the dailyCashReconciliations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDailyCashReconciliations(DailyCashReconciliationCriteria criteria) {
        LOG.debug("REST request to count DailyCashReconciliations by criteria: {}", criteria);
        return ResponseEntity.ok().body(dailyCashReconciliationQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /daily-cash-reconciliations/:id} : get the "id" dailyCashReconciliation.
     *
     * @param id the id of the dailyCashReconciliationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dailyCashReconciliationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DailyCashReconciliationDTO> getDailyCashReconciliation(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DailyCashReconciliation : {}", id);
        Optional<DailyCashReconciliationDTO> dailyCashReconciliationDTO = dailyCashReconciliationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dailyCashReconciliationDTO);
    }

    /**
     * {@code DELETE  /daily-cash-reconciliations/:id} : delete the "id" dailyCashReconciliation.
     *
     * @param id the id of the dailyCashReconciliationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDailyCashReconciliation(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DailyCashReconciliation : {}", id);
        dailyCashReconciliationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
