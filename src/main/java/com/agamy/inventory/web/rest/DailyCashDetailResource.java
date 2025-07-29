package com.agamy.inventory.web.rest;

import com.agamy.inventory.repository.DailyCashDetailRepository;
import com.agamy.inventory.service.DailyCashDetailQueryService;
import com.agamy.inventory.service.DailyCashDetailService;
import com.agamy.inventory.service.criteria.DailyCashDetailCriteria;
import com.agamy.inventory.service.dto.DailyCashDetailDTO;
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
 * REST controller for managing {@link com.agamy.inventory.domain.DailyCashDetail}.
 */
@RestController
@RequestMapping("/api/daily-cash-details")
public class DailyCashDetailResource {

    private static final Logger LOG = LoggerFactory.getLogger(DailyCashDetailResource.class);

    private static final String ENTITY_NAME = "dailyCashDetail";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DailyCashDetailService dailyCashDetailService;

    private final DailyCashDetailRepository dailyCashDetailRepository;

    private final DailyCashDetailQueryService dailyCashDetailQueryService;

    public DailyCashDetailResource(
        DailyCashDetailService dailyCashDetailService,
        DailyCashDetailRepository dailyCashDetailRepository,
        DailyCashDetailQueryService dailyCashDetailQueryService
    ) {
        this.dailyCashDetailService = dailyCashDetailService;
        this.dailyCashDetailRepository = dailyCashDetailRepository;
        this.dailyCashDetailQueryService = dailyCashDetailQueryService;
    }

    /**
     * {@code POST  /daily-cash-details} : Create a new dailyCashDetail.
     *
     * @param dailyCashDetailDTO the dailyCashDetailDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dailyCashDetailDTO, or with status {@code 400 (Bad Request)} if the dailyCashDetail has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DailyCashDetailDTO> createDailyCashDetail(@Valid @RequestBody DailyCashDetailDTO dailyCashDetailDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save DailyCashDetail : {}", dailyCashDetailDTO);
        if (dailyCashDetailDTO.getId() != null) {
            throw new BadRequestAlertException("A new dailyCashDetail cannot already have an ID", ENTITY_NAME, "idexists");
        }
        dailyCashDetailDTO = dailyCashDetailService.save(dailyCashDetailDTO);
        return ResponseEntity.created(new URI("/api/daily-cash-details/" + dailyCashDetailDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, dailyCashDetailDTO.getId().toString()))
            .body(dailyCashDetailDTO);
    }

    /**
     * {@code PUT  /daily-cash-details/:id} : Updates an existing dailyCashDetail.
     *
     * @param id the id of the dailyCashDetailDTO to save.
     * @param dailyCashDetailDTO the dailyCashDetailDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dailyCashDetailDTO,
     * or with status {@code 400 (Bad Request)} if the dailyCashDetailDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dailyCashDetailDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DailyCashDetailDTO> updateDailyCashDetail(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DailyCashDetailDTO dailyCashDetailDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DailyCashDetail : {}, {}", id, dailyCashDetailDTO);
        if (dailyCashDetailDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dailyCashDetailDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dailyCashDetailRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        dailyCashDetailDTO = dailyCashDetailService.update(dailyCashDetailDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dailyCashDetailDTO.getId().toString()))
            .body(dailyCashDetailDTO);
    }

    /**
     * {@code PATCH  /daily-cash-details/:id} : Partial updates given fields of an existing dailyCashDetail, field will ignore if it is null
     *
     * @param id the id of the dailyCashDetailDTO to save.
     * @param dailyCashDetailDTO the dailyCashDetailDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dailyCashDetailDTO,
     * or with status {@code 400 (Bad Request)} if the dailyCashDetailDTO is not valid,
     * or with status {@code 404 (Not Found)} if the dailyCashDetailDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the dailyCashDetailDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DailyCashDetailDTO> partialUpdateDailyCashDetail(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DailyCashDetailDTO dailyCashDetailDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DailyCashDetail partially : {}, {}", id, dailyCashDetailDTO);
        if (dailyCashDetailDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dailyCashDetailDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dailyCashDetailRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DailyCashDetailDTO> result = dailyCashDetailService.partialUpdate(dailyCashDetailDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dailyCashDetailDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /daily-cash-details} : get all the dailyCashDetails.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dailyCashDetails in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DailyCashDetailDTO>> getAllDailyCashDetails(
        DailyCashDetailCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get DailyCashDetails by criteria: {}", criteria);

        Page<DailyCashDetailDTO> page = dailyCashDetailQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /daily-cash-details/count} : count all the dailyCashDetails.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDailyCashDetails(DailyCashDetailCriteria criteria) {
        LOG.debug("REST request to count DailyCashDetails by criteria: {}", criteria);
        return ResponseEntity.ok().body(dailyCashDetailQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /daily-cash-details/:id} : get the "id" dailyCashDetail.
     *
     * @param id the id of the dailyCashDetailDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dailyCashDetailDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DailyCashDetailDTO> getDailyCashDetail(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DailyCashDetail : {}", id);
        Optional<DailyCashDetailDTO> dailyCashDetailDTO = dailyCashDetailService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dailyCashDetailDTO);
    }

    /**
     * {@code DELETE  /daily-cash-details/:id} : delete the "id" dailyCashDetail.
     *
     * @param id the id of the dailyCashDetailDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDailyCashDetail(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DailyCashDetail : {}", id);
        dailyCashDetailService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
