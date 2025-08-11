package com.agamy.inventory.service;

import com.agamy.inventory.domain.SaleOperation;
import com.agamy.inventory.repository.SaleOperationRepository;
import com.agamy.inventory.service.dto.SaleOperationDTO;
import com.agamy.inventory.service.mapper.SaleOperationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.agamy.inventory.domain.SaleOperation}.
 */
@Service
@Transactional
public class SaleOperationService {

    private static final Logger LOG = LoggerFactory.getLogger(SaleOperationService.class);

    private final SaleOperationRepository saleOperationRepository;

    private final SaleOperationMapper saleOperationMapper;

    public SaleOperationService(SaleOperationRepository saleOperationRepository, SaleOperationMapper saleOperationMapper) {
        this.saleOperationRepository = saleOperationRepository;
        this.saleOperationMapper = saleOperationMapper;
    }

    /**
     * Save a saleOperation.
     *
     * @param saleOperationDTO the entity to save.
     * @return the persisted entity.
     */
    public SaleOperationDTO save(SaleOperationDTO saleOperationDTO) {
        LOG.debug("Request to save SaleOperation : {}", saleOperationDTO);
        SaleOperation saleOperation = saleOperationMapper.toEntity(saleOperationDTO);
        saleOperation = saleOperationRepository.save(saleOperation);
        return saleOperationMapper.toDto(saleOperation);
    }

    /**
     * Update a saleOperation.
     *
     * @param saleOperationDTO the entity to save.
     * @return the persisted entity.
     */
    public SaleOperationDTO update(SaleOperationDTO saleOperationDTO) {
        LOG.debug("Request to update SaleOperation : {}", saleOperationDTO);
        SaleOperation saleOperation = saleOperationMapper.toEntity(saleOperationDTO);
        saleOperation.setIsPersisted();
        saleOperation = saleOperationRepository.save(saleOperation);
        return saleOperationMapper.toDto(saleOperation);
    }

    /**
     * Partially update a saleOperation.
     *
     * @param saleOperationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SaleOperationDTO> partialUpdate(SaleOperationDTO saleOperationDTO) {
        LOG.debug("Request to partially update SaleOperation : {}", saleOperationDTO);

        return saleOperationRepository
            .findById(saleOperationDTO.getId())
            .map(existingSaleOperation -> {
                saleOperationMapper.partialUpdate(existingSaleOperation, saleOperationDTO);

                return existingSaleOperation;
            })
            .map(saleOperationRepository::save)
            .map(saleOperationMapper::toDto);
    }

    /**
     * Get one saleOperation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SaleOperationDTO> findOne(Long id) {
        LOG.debug("Request to get SaleOperation : {}", id);
        return saleOperationRepository.findById(id).map(saleOperationMapper::toDto);
    }

    /**
     * Delete the saleOperation by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SaleOperation : {}", id);
        saleOperationRepository.deleteById(id);
    }
}
