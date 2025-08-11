package com.agamy.inventory.web.rest;

import com.agamy.inventory.repository.CashTransactionRepository;
import com.agamy.inventory.service.CashTransactionQueryService;
import com.agamy.inventory.service.CashTransactionService;
import com.agamy.inventory.service.criteria.CashTransactionCriteria;
import com.agamy.inventory.service.dto.CashTransactionDTO;
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
 * REST controller for managing {@link com.agamy.inventory.domain.CashTransaction}.
 */
@RestController
@RequestMapping("/api/cash-transactions")
public class CashTransactionResource {

    private static final Logger LOG = LoggerFactory.getLogger(CashTransactionResource.class);

    private static final String ENTITY_NAME = "cashTransaction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CashTransactionService cashTransactionService;

    private final CashTransactionRepository cashTransactionRepository;

    private final CashTransactionQueryService cashTransactionQueryService;

    public CashTransactionResource(
        CashTransactionService cashTransactionService,
        CashTransactionRepository cashTransactionRepository,
        CashTransactionQueryService cashTransactionQueryService
    ) {
        this.cashTransactionService = cashTransactionService;
        this.cashTransactionRepository = cashTransactionRepository;
        this.cashTransactionQueryService = cashTransactionQueryService;
    }

    /**
     * {@code POST  /cash-transactions} : Create a new cashTransaction.
     *
     * @param cashTransactionDTO the cashTransactionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cashTransactionDTO, or with status {@code 400 (Bad Request)} if the cashTransaction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CashTransactionDTO> createCashTransaction(@Valid @RequestBody CashTransactionDTO cashTransactionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CashTransaction : {}", cashTransactionDTO);
        if (cashTransactionDTO.getId() != null) {
            throw new BadRequestAlertException("A new cashTransaction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        cashTransactionDTO = cashTransactionService.save(cashTransactionDTO);
        return ResponseEntity.created(new URI("/api/cash-transactions/" + cashTransactionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, cashTransactionDTO.getId().toString()))
            .body(cashTransactionDTO);
    }

    /**
     * {@code PUT  /cash-transactions/:id} : Updates an existing cashTransaction.
     *
     * @param id the id of the cashTransactionDTO to save.
     * @param cashTransactionDTO the cashTransactionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cashTransactionDTO,
     * or with status {@code 400 (Bad Request)} if the cashTransactionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cashTransactionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CashTransactionDTO> updateCashTransaction(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CashTransactionDTO cashTransactionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CashTransaction : {}, {}", id, cashTransactionDTO);
        if (cashTransactionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cashTransactionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cashTransactionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        cashTransactionDTO = cashTransactionService.update(cashTransactionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cashTransactionDTO.getId().toString()))
            .body(cashTransactionDTO);
    }

    /**
     * {@code PATCH  /cash-transactions/:id} : Partial updates given fields of an existing cashTransaction, field will ignore if it is null
     *
     * @param id the id of the cashTransactionDTO to save.
     * @param cashTransactionDTO the cashTransactionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cashTransactionDTO,
     * or with status {@code 400 (Bad Request)} if the cashTransactionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cashTransactionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cashTransactionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CashTransactionDTO> partialUpdateCashTransaction(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CashTransactionDTO cashTransactionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CashTransaction partially : {}, {}", id, cashTransactionDTO);
        if (cashTransactionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cashTransactionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cashTransactionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CashTransactionDTO> result = cashTransactionService.partialUpdate(cashTransactionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cashTransactionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cash-transactions} : get all the cashTransactions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cashTransactions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CashTransactionDTO>> getAllCashTransactions(
        CashTransactionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get CashTransactions by criteria: {}", criteria);

        Page<CashTransactionDTO> page = cashTransactionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cash-transactions/count} : count all the cashTransactions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCashTransactions(CashTransactionCriteria criteria) {
        LOG.debug("REST request to count CashTransactions by criteria: {}", criteria);
        return ResponseEntity.ok().body(cashTransactionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /cash-transactions/:id} : get the "id" cashTransaction.
     *
     * @param id the id of the cashTransactionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cashTransactionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CashTransactionDTO> getCashTransaction(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CashTransaction : {}", id);
        Optional<CashTransactionDTO> cashTransactionDTO = cashTransactionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cashTransactionDTO);
    }

    /**
     * {@code DELETE  /cash-transactions/:id} : delete the "id" cashTransaction.
     *
     * @param id the id of the cashTransactionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCashTransaction(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CashTransaction : {}", id);
        cashTransactionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
