package com.agamy.inventory.web.rest;

import com.agamy.inventory.repository.CashBalanceRepository;
import com.agamy.inventory.service.CashBalanceQueryService;
import com.agamy.inventory.service.CashBalanceService;
import com.agamy.inventory.service.criteria.CashBalanceCriteria;
import com.agamy.inventory.service.dto.CashBalanceDTO;
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
 * REST controller for managing {@link com.agamy.inventory.domain.CashBalance}.
 */
@RestController
@RequestMapping("/api/cash-balances")
public class CashBalanceResource {

    private static final Logger LOG = LoggerFactory.getLogger(CashBalanceResource.class);

    private static final String ENTITY_NAME = "cashBalance";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CashBalanceService cashBalanceService;

    private final CashBalanceRepository cashBalanceRepository;

    private final CashBalanceQueryService cashBalanceQueryService;

    public CashBalanceResource(
        CashBalanceService cashBalanceService,
        CashBalanceRepository cashBalanceRepository,
        CashBalanceQueryService cashBalanceQueryService
    ) {
        this.cashBalanceService = cashBalanceService;
        this.cashBalanceRepository = cashBalanceRepository;
        this.cashBalanceQueryService = cashBalanceQueryService;
    }

    /**
     * {@code POST  /cash-balances} : Create a new cashBalance.
     *
     * @param cashBalanceDTO the cashBalanceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cashBalanceDTO, or with status {@code 400 (Bad Request)} if the cashBalance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CashBalanceDTO> createCashBalance(@Valid @RequestBody CashBalanceDTO cashBalanceDTO) throws URISyntaxException {
        LOG.debug("REST request to save CashBalance : {}", cashBalanceDTO);
        if (cashBalanceDTO.getId() != null) {
            throw new BadRequestAlertException("A new cashBalance cannot already have an ID", ENTITY_NAME, "idexists");
        }
        cashBalanceDTO = cashBalanceService.save(cashBalanceDTO);
        return ResponseEntity.created(new URI("/api/cash-balances/" + cashBalanceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, cashBalanceDTO.getId().toString()))
            .body(cashBalanceDTO);
    }

    /**
     * {@code PUT  /cash-balances/:id} : Updates an existing cashBalance.
     *
     * @param id the id of the cashBalanceDTO to save.
     * @param cashBalanceDTO the cashBalanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cashBalanceDTO,
     * or with status {@code 400 (Bad Request)} if the cashBalanceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cashBalanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CashBalanceDTO> updateCashBalance(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CashBalanceDTO cashBalanceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CashBalance : {}, {}", id, cashBalanceDTO);
        if (cashBalanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cashBalanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cashBalanceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        cashBalanceDTO = cashBalanceService.update(cashBalanceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cashBalanceDTO.getId().toString()))
            .body(cashBalanceDTO);
    }

    /**
     * {@code PATCH  /cash-balances/:id} : Partial updates given fields of an existing cashBalance, field will ignore if it is null
     *
     * @param id the id of the cashBalanceDTO to save.
     * @param cashBalanceDTO the cashBalanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cashBalanceDTO,
     * or with status {@code 400 (Bad Request)} if the cashBalanceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cashBalanceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cashBalanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CashBalanceDTO> partialUpdateCashBalance(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CashBalanceDTO cashBalanceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CashBalance partially : {}, {}", id, cashBalanceDTO);
        if (cashBalanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cashBalanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cashBalanceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CashBalanceDTO> result = cashBalanceService.partialUpdate(cashBalanceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cashBalanceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cash-balances} : get all the cashBalances.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cashBalances in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CashBalanceDTO>> getAllCashBalances(
        CashBalanceCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get CashBalances by criteria: {}", criteria);

        Page<CashBalanceDTO> page = cashBalanceQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cash-balances/count} : count all the cashBalances.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCashBalances(CashBalanceCriteria criteria) {
        LOG.debug("REST request to count CashBalances by criteria: {}", criteria);
        return ResponseEntity.ok().body(cashBalanceQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /cash-balances/:id} : get the "id" cashBalance.
     *
     * @param id the id of the cashBalanceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cashBalanceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CashBalanceDTO> getCashBalance(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CashBalance : {}", id);
        Optional<CashBalanceDTO> cashBalanceDTO = cashBalanceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cashBalanceDTO);
    }

    /**
     * {@code DELETE  /cash-balances/:id} : delete the "id" cashBalance.
     *
     * @param id the id of the cashBalanceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCashBalance(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CashBalance : {}", id);
        cashBalanceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
