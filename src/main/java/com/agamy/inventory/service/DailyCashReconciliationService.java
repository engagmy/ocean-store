package com.agamy.inventory.service;

import com.agamy.inventory.domain.DailyCashReconciliation;
import com.agamy.inventory.repository.DailyCashReconciliationRepository;
import com.agamy.inventory.service.dto.DailyCashReconciliationDTO;
import com.agamy.inventory.service.mapper.DailyCashReconciliationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.agamy.inventory.domain.DailyCashReconciliation}.
 */
@Service
@Transactional
public class DailyCashReconciliationService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyCashReconciliationService.class);

    private final DailyCashReconciliationRepository dailyCashReconciliationRepository;

    private final DailyCashReconciliationMapper dailyCashReconciliationMapper;

    public DailyCashReconciliationService(
        DailyCashReconciliationRepository dailyCashReconciliationRepository,
        DailyCashReconciliationMapper dailyCashReconciliationMapper
    ) {
        this.dailyCashReconciliationRepository = dailyCashReconciliationRepository;
        this.dailyCashReconciliationMapper = dailyCashReconciliationMapper;
    }

    /**
     * Save a dailyCashReconciliation.
     *
     * @param dailyCashReconciliationDTO the entity to save.
     * @return the persisted entity.
     */
    public DailyCashReconciliationDTO save(DailyCashReconciliationDTO dailyCashReconciliationDTO) {
        LOG.debug("Request to save DailyCashReconciliation : {}", dailyCashReconciliationDTO);
        DailyCashReconciliation dailyCashReconciliation = dailyCashReconciliationMapper.toEntity(dailyCashReconciliationDTO);
        dailyCashReconciliation = dailyCashReconciliationRepository.save(dailyCashReconciliation);
        return dailyCashReconciliationMapper.toDto(dailyCashReconciliation);
    }

    /**
     * Update a dailyCashReconciliation.
     *
     * @param dailyCashReconciliationDTO the entity to save.
     * @return the persisted entity.
     */
    public DailyCashReconciliationDTO update(DailyCashReconciliationDTO dailyCashReconciliationDTO) {
        LOG.debug("Request to update DailyCashReconciliation : {}", dailyCashReconciliationDTO);
        DailyCashReconciliation dailyCashReconciliation = dailyCashReconciliationMapper.toEntity(dailyCashReconciliationDTO);
        dailyCashReconciliation.setIsPersisted();
        dailyCashReconciliation = dailyCashReconciliationRepository.save(dailyCashReconciliation);
        return dailyCashReconciliationMapper.toDto(dailyCashReconciliation);
    }

    /**
     * Partially update a dailyCashReconciliation.
     *
     * @param dailyCashReconciliationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DailyCashReconciliationDTO> partialUpdate(DailyCashReconciliationDTO dailyCashReconciliationDTO) {
        LOG.debug("Request to partially update DailyCashReconciliation : {}", dailyCashReconciliationDTO);

        return dailyCashReconciliationRepository
            .findById(dailyCashReconciliationDTO.getId())
            .map(existingDailyCashReconciliation -> {
                dailyCashReconciliationMapper.partialUpdate(existingDailyCashReconciliation, dailyCashReconciliationDTO);

                return existingDailyCashReconciliation;
            })
            .map(dailyCashReconciliationRepository::save)
            .map(dailyCashReconciliationMapper::toDto);
    }

    /**
     * Get one dailyCashReconciliation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DailyCashReconciliationDTO> findOne(Long id) {
        LOG.debug("Request to get DailyCashReconciliation : {}", id);
        return dailyCashReconciliationRepository.findById(id).map(dailyCashReconciliationMapper::toDto);
    }

    /**
     * Delete the dailyCashReconciliation by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete DailyCashReconciliation : {}", id);
        dailyCashReconciliationRepository.deleteById(id);
    }
}
