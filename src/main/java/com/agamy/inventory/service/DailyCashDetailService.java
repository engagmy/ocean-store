package com.agamy.inventory.service;

import com.agamy.inventory.domain.DailyCashDetail;
import com.agamy.inventory.repository.DailyCashDetailRepository;
import com.agamy.inventory.service.dto.DailyCashDetailDTO;
import com.agamy.inventory.service.mapper.DailyCashDetailMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.agamy.inventory.domain.DailyCashDetail}.
 */
@Service
@Transactional
public class DailyCashDetailService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyCashDetailService.class);

    private final DailyCashDetailRepository dailyCashDetailRepository;

    private final DailyCashDetailMapper dailyCashDetailMapper;

    public DailyCashDetailService(DailyCashDetailRepository dailyCashDetailRepository, DailyCashDetailMapper dailyCashDetailMapper) {
        this.dailyCashDetailRepository = dailyCashDetailRepository;
        this.dailyCashDetailMapper = dailyCashDetailMapper;
    }

    /**
     * Save a dailyCashDetail.
     *
     * @param dailyCashDetailDTO the entity to save.
     * @return the persisted entity.
     */
    public DailyCashDetailDTO save(DailyCashDetailDTO dailyCashDetailDTO) {
        LOG.debug("Request to save DailyCashDetail : {}", dailyCashDetailDTO);
        DailyCashDetail dailyCashDetail = dailyCashDetailMapper.toEntity(dailyCashDetailDTO);
        dailyCashDetail = dailyCashDetailRepository.save(dailyCashDetail);
        return dailyCashDetailMapper.toDto(dailyCashDetail);
    }

    /**
     * Update a dailyCashDetail.
     *
     * @param dailyCashDetailDTO the entity to save.
     * @return the persisted entity.
     */
    public DailyCashDetailDTO update(DailyCashDetailDTO dailyCashDetailDTO) {
        LOG.debug("Request to update DailyCashDetail : {}", dailyCashDetailDTO);
        DailyCashDetail dailyCashDetail = dailyCashDetailMapper.toEntity(dailyCashDetailDTO);
        dailyCashDetail.setIsPersisted();
        dailyCashDetail = dailyCashDetailRepository.save(dailyCashDetail);
        return dailyCashDetailMapper.toDto(dailyCashDetail);
    }

    /**
     * Partially update a dailyCashDetail.
     *
     * @param dailyCashDetailDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DailyCashDetailDTO> partialUpdate(DailyCashDetailDTO dailyCashDetailDTO) {
        LOG.debug("Request to partially update DailyCashDetail : {}", dailyCashDetailDTO);

        return dailyCashDetailRepository
            .findById(dailyCashDetailDTO.getId())
            .map(existingDailyCashDetail -> {
                dailyCashDetailMapper.partialUpdate(existingDailyCashDetail, dailyCashDetailDTO);

                return existingDailyCashDetail;
            })
            .map(dailyCashDetailRepository::save)
            .map(dailyCashDetailMapper::toDto);
    }

    /**
     * Get one dailyCashDetail by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DailyCashDetailDTO> findOne(Long id) {
        LOG.debug("Request to get DailyCashDetail : {}", id);
        return dailyCashDetailRepository.findById(id).map(dailyCashDetailMapper::toDto);
    }

    /**
     * Delete the dailyCashDetail by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete DailyCashDetail : {}", id);
        dailyCashDetailRepository.deleteById(id);
    }
}
