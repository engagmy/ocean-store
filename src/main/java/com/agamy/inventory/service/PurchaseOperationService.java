package com.agamy.inventory.service;

import com.agamy.inventory.domain.PurchaseOperation;
import com.agamy.inventory.repository.PurchaseOperationRepository;
import com.agamy.inventory.service.dto.PurchaseOperationDTO;
import com.agamy.inventory.service.mapper.PurchaseOperationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.agamy.inventory.domain.PurchaseOperation}.
 */
@Service
@Transactional
public class PurchaseOperationService {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOperationService.class);

    private final PurchaseOperationRepository purchaseOperationRepository;

    private final PurchaseOperationMapper purchaseOperationMapper;

    public PurchaseOperationService(
        PurchaseOperationRepository purchaseOperationRepository,
        PurchaseOperationMapper purchaseOperationMapper
    ) {
        this.purchaseOperationRepository = purchaseOperationRepository;
        this.purchaseOperationMapper = purchaseOperationMapper;
    }

    /**
     * Save a purchaseOperation.
     *
     * @param purchaseOperationDTO the entity to save.
     * @return the persisted entity.
     */
    public PurchaseOperationDTO save(PurchaseOperationDTO purchaseOperationDTO) {
        LOG.debug("Request to save PurchaseOperation : {}", purchaseOperationDTO);
        PurchaseOperation purchaseOperation = purchaseOperationMapper.toEntity(purchaseOperationDTO);
        purchaseOperation = purchaseOperationRepository.save(purchaseOperation);
        return purchaseOperationMapper.toDto(purchaseOperation);
    }

    /**
     * Update a purchaseOperation.
     *
     * @param purchaseOperationDTO the entity to save.
     * @return the persisted entity.
     */
    public PurchaseOperationDTO update(PurchaseOperationDTO purchaseOperationDTO) {
        LOG.debug("Request to update PurchaseOperation : {}", purchaseOperationDTO);
        PurchaseOperation purchaseOperation = purchaseOperationMapper.toEntity(purchaseOperationDTO);
        purchaseOperation.setIsPersisted();
        purchaseOperation = purchaseOperationRepository.save(purchaseOperation);
        return purchaseOperationMapper.toDto(purchaseOperation);
    }

    /**
     * Partially update a purchaseOperation.
     *
     * @param purchaseOperationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PurchaseOperationDTO> partialUpdate(PurchaseOperationDTO purchaseOperationDTO) {
        LOG.debug("Request to partially update PurchaseOperation : {}", purchaseOperationDTO);

        return purchaseOperationRepository
            .findById(purchaseOperationDTO.getId())
            .map(existingPurchaseOperation -> {
                purchaseOperationMapper.partialUpdate(existingPurchaseOperation, purchaseOperationDTO);

                return existingPurchaseOperation;
            })
            .map(purchaseOperationRepository::save)
            .map(purchaseOperationMapper::toDto);
    }

    /**
     * Get one purchaseOperation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PurchaseOperationDTO> findOne(Long id) {
        LOG.debug("Request to get PurchaseOperation : {}", id);
        return purchaseOperationRepository.findById(id).map(purchaseOperationMapper::toDto);
    }

    /**
     * Delete the purchaseOperation by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PurchaseOperation : {}", id);
        purchaseOperationRepository.deleteById(id);
    }
}
