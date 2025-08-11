package com.agamy.inventory.service;

import com.agamy.inventory.domain.PurchasePayment;
import com.agamy.inventory.repository.PurchasePaymentRepository;
import com.agamy.inventory.service.dto.PurchasePaymentDTO;
import com.agamy.inventory.service.mapper.PurchasePaymentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.agamy.inventory.domain.PurchasePayment}.
 */
@Service
@Transactional
public class PurchasePaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(PurchasePaymentService.class);

    private final PurchasePaymentRepository purchasePaymentRepository;

    private final PurchasePaymentMapper purchasePaymentMapper;

    public PurchasePaymentService(PurchasePaymentRepository purchasePaymentRepository, PurchasePaymentMapper purchasePaymentMapper) {
        this.purchasePaymentRepository = purchasePaymentRepository;
        this.purchasePaymentMapper = purchasePaymentMapper;
    }

    /**
     * Save a purchasePayment.
     *
     * @param purchasePaymentDTO the entity to save.
     * @return the persisted entity.
     */
    public PurchasePaymentDTO save(PurchasePaymentDTO purchasePaymentDTO) {
        LOG.debug("Request to save PurchasePayment : {}", purchasePaymentDTO);
        PurchasePayment purchasePayment = purchasePaymentMapper.toEntity(purchasePaymentDTO);
        purchasePayment = purchasePaymentRepository.save(purchasePayment);
        return purchasePaymentMapper.toDto(purchasePayment);
    }

    /**
     * Update a purchasePayment.
     *
     * @param purchasePaymentDTO the entity to save.
     * @return the persisted entity.
     */
    public PurchasePaymentDTO update(PurchasePaymentDTO purchasePaymentDTO) {
        LOG.debug("Request to update PurchasePayment : {}", purchasePaymentDTO);
        PurchasePayment purchasePayment = purchasePaymentMapper.toEntity(purchasePaymentDTO);
        purchasePayment.setIsPersisted();
        purchasePayment = purchasePaymentRepository.save(purchasePayment);
        return purchasePaymentMapper.toDto(purchasePayment);
    }

    /**
     * Partially update a purchasePayment.
     *
     * @param purchasePaymentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PurchasePaymentDTO> partialUpdate(PurchasePaymentDTO purchasePaymentDTO) {
        LOG.debug("Request to partially update PurchasePayment : {}", purchasePaymentDTO);

        return purchasePaymentRepository
            .findById(purchasePaymentDTO.getId())
            .map(existingPurchasePayment -> {
                purchasePaymentMapper.partialUpdate(existingPurchasePayment, purchasePaymentDTO);

                return existingPurchasePayment;
            })
            .map(purchasePaymentRepository::save)
            .map(purchasePaymentMapper::toDto);
    }

    /**
     * Get one purchasePayment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PurchasePaymentDTO> findOne(Long id) {
        LOG.debug("Request to get PurchasePayment : {}", id);
        return purchasePaymentRepository.findById(id).map(purchasePaymentMapper::toDto);
    }

    /**
     * Delete the purchasePayment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PurchasePayment : {}", id);
        purchasePaymentRepository.deleteById(id);
    }
}
