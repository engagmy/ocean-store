package com.agamy.inventory.service;

import com.agamy.inventory.domain.SalePayment;
import com.agamy.inventory.repository.SalePaymentRepository;
import com.agamy.inventory.service.dto.SalePaymentDTO;
import com.agamy.inventory.service.mapper.SalePaymentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.agamy.inventory.domain.SalePayment}.
 */
@Service
@Transactional
public class SalePaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(SalePaymentService.class);

    private final SalePaymentRepository salePaymentRepository;

    private final SalePaymentMapper salePaymentMapper;

    public SalePaymentService(SalePaymentRepository salePaymentRepository, SalePaymentMapper salePaymentMapper) {
        this.salePaymentRepository = salePaymentRepository;
        this.salePaymentMapper = salePaymentMapper;
    }

    /**
     * Save a salePayment.
     *
     * @param salePaymentDTO the entity to save.
     * @return the persisted entity.
     */
    public SalePaymentDTO save(SalePaymentDTO salePaymentDTO) {
        LOG.debug("Request to save SalePayment : {}", salePaymentDTO);
        SalePayment salePayment = salePaymentMapper.toEntity(salePaymentDTO);
        salePayment = salePaymentRepository.save(salePayment);
        return salePaymentMapper.toDto(salePayment);
    }

    /**
     * Update a salePayment.
     *
     * @param salePaymentDTO the entity to save.
     * @return the persisted entity.
     */
    public SalePaymentDTO update(SalePaymentDTO salePaymentDTO) {
        LOG.debug("Request to update SalePayment : {}", salePaymentDTO);
        SalePayment salePayment = salePaymentMapper.toEntity(salePaymentDTO);
        salePayment.setIsPersisted();
        salePayment = salePaymentRepository.save(salePayment);
        return salePaymentMapper.toDto(salePayment);
    }

    /**
     * Partially update a salePayment.
     *
     * @param salePaymentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SalePaymentDTO> partialUpdate(SalePaymentDTO salePaymentDTO) {
        LOG.debug("Request to partially update SalePayment : {}", salePaymentDTO);

        return salePaymentRepository
            .findById(salePaymentDTO.getId())
            .map(existingSalePayment -> {
                salePaymentMapper.partialUpdate(existingSalePayment, salePaymentDTO);

                return existingSalePayment;
            })
            .map(salePaymentRepository::save)
            .map(salePaymentMapper::toDto);
    }

    /**
     * Get one salePayment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SalePaymentDTO> findOne(Long id) {
        LOG.debug("Request to get SalePayment : {}", id);
        return salePaymentRepository.findById(id).map(salePaymentMapper::toDto);
    }

    /**
     * Delete the salePayment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SalePayment : {}", id);
        salePaymentRepository.deleteById(id);
    }
}
