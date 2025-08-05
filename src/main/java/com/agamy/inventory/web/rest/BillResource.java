package com.agamy.inventory.web.rest;

import com.agamy.inventory.repository.BillRepository;
import com.agamy.inventory.service.BillQueryService;
import com.agamy.inventory.service.BillService;
import com.agamy.inventory.service.criteria.BillCriteria;
import com.agamy.inventory.service.dto.BillDTO;
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
 * REST controller for managing {@link com.agamy.inventory.domain.Bill}.
 */
@RestController
@RequestMapping("/api/bills")
public class BillResource {

    private static final Logger LOG = LoggerFactory.getLogger(BillResource.class);

    private static final String ENTITY_NAME = "bill";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BillService billService;

    private final BillRepository billRepository;

    private final BillQueryService billQueryService;

    public BillResource(BillService billService, BillRepository billRepository, BillQueryService billQueryService) {
        this.billService = billService;
        this.billRepository = billRepository;
        this.billQueryService = billQueryService;
    }

    /**
     * {@code POST  /bills} : Create a new bill.
     *
     * @param billDTO the billDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new billDTO, or with status {@code 400 (Bad Request)} if the bill has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BillDTO> createBill(@Valid @RequestBody BillDTO billDTO) throws URISyntaxException {
        LOG.debug("REST request to save Bill : {}", billDTO);
        if (billDTO.getId() != null) {
            throw new BadRequestAlertException("A new bill cannot already have an ID", ENTITY_NAME, "idexists");
        }
        billDTO = billService.save(billDTO);
        return ResponseEntity.created(new URI("/api/bills/" + billDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, billDTO.getId().toString()))
            .body(billDTO);
    }

    /**
     * {@code PUT  /bills/:id} : Updates an existing bill.
     *
     * @param id the id of the billDTO to save.
     * @param billDTO the billDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated billDTO,
     * or with status {@code 400 (Bad Request)} if the billDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the billDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BillDTO> updateBill(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BillDTO billDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Bill : {}, {}", id, billDTO);
        if (billDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, billDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!billRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        billDTO = billService.update(billDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, billDTO.getId().toString()))
            .body(billDTO);
    }

    /**
     * {@code PATCH  /bills/:id} : Partial updates given fields of an existing bill, field will ignore if it is null
     *
     * @param id the id of the billDTO to save.
     * @param billDTO the billDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated billDTO,
     * or with status {@code 400 (Bad Request)} if the billDTO is not valid,
     * or with status {@code 404 (Not Found)} if the billDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the billDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BillDTO> partialUpdateBill(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BillDTO billDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Bill partially : {}, {}", id, billDTO);
        if (billDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, billDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!billRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BillDTO> result = billService.partialUpdate(billDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, billDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /bills} : get all the bills.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bills in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BillDTO>> getAllBills(
        BillCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Bills by criteria: {}", criteria);

        Page<BillDTO> page = billQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /bills/count} : count all the bills.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countBills(BillCriteria criteria) {
        LOG.debug("REST request to count Bills by criteria: {}", criteria);
        return ResponseEntity.ok().body(billQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /bills/:id} : get the "id" bill.
     *
     * @param id the id of the billDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the billDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BillDTO> getBill(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Bill : {}", id);
        Optional<BillDTO> billDTO = billService.findOne(id);
        return ResponseUtil.wrapOrNotFound(billDTO);
    }

    /**
     * {@code DELETE  /bills/:id} : delete the "id" bill.
     *
     * @param id the id of the billDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBill(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Bill : {}", id);
        billService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
