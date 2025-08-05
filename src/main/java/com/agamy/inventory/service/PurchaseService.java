package com.agamy.inventory.service;

import com.agamy.inventory.domain.Purchase;
import com.agamy.inventory.repository.PurchaseRepository;
import com.agamy.inventory.service.dto.PurchaseDTO;
import com.agamy.inventory.service.mapper.PurchaseMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.agamy.inventory.domain.Purchase}.
 */
@Service
@Transactional
public class PurchaseService {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseService.class);

    private final PurchaseRepository purchaseRepository;

    private final PurchaseMapper purchaseMapper;

    public PurchaseService(PurchaseRepository purchaseRepository, PurchaseMapper purchaseMapper) {
        this.purchaseRepository = purchaseRepository;
        this.purchaseMapper = purchaseMapper;
    }

    /**
     * Save a purchase.
     *
     * @param purchaseDTO the entity to save.
     * @return the persisted entity.
     */
    public PurchaseDTO save(PurchaseDTO purchaseDTO) {
        LOG.debug("Request to save Purchase : {}", purchaseDTO);
        Purchase purchase = purchaseMapper.toEntity(purchaseDTO);
        purchase = purchaseRepository.save(purchase);
        return purchaseMapper.toDto(purchase);
    }

    /**
     * Update a purchase.
     *
     * @param purchaseDTO the entity to save.
     * @return the persisted entity.
     */
    public PurchaseDTO update(PurchaseDTO purchaseDTO) {
        LOG.debug("Request to update Purchase : {}", purchaseDTO);
        Purchase purchase = purchaseMapper.toEntity(purchaseDTO);
        purchase.setIsPersisted();
        purchase = purchaseRepository.save(purchase);
        return purchaseMapper.toDto(purchase);
    }

    /**
     * Partially update a purchase.
     *
     * @param purchaseDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PurchaseDTO> partialUpdate(PurchaseDTO purchaseDTO) {
        LOG.debug("Request to partially update Purchase : {}", purchaseDTO);

        return purchaseRepository
            .findById(purchaseDTO.getId())
            .map(existingPurchase -> {
                purchaseMapper.partialUpdate(existingPurchase, purchaseDTO);

                return existingPurchase;
            })
            .map(purchaseRepository::save)
            .map(purchaseMapper::toDto);
    }

    /**
     * Get one purchase by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PurchaseDTO> findOne(Long id) {
        LOG.debug("Request to get Purchase : {}", id);
        return purchaseRepository.findById(id).map(purchaseMapper::toDto);
    }

    /**
     * Delete the purchase by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Purchase : {}", id);
        purchaseRepository.deleteById(id);
    }
}
