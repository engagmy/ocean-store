package com.agamy.inventory.service;

import com.agamy.inventory.domain.CashBalance;
import com.agamy.inventory.repository.CashBalanceRepository;
import com.agamy.inventory.service.dto.CashBalanceDTO;
import com.agamy.inventory.service.mapper.CashBalanceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.agamy.inventory.domain.CashBalance}.
 */
@Service
@Transactional
public class CashBalanceService {

    private static final Logger LOG = LoggerFactory.getLogger(CashBalanceService.class);

    private final CashBalanceRepository cashBalanceRepository;

    private final CashBalanceMapper cashBalanceMapper;

    public CashBalanceService(CashBalanceRepository cashBalanceRepository, CashBalanceMapper cashBalanceMapper) {
        this.cashBalanceRepository = cashBalanceRepository;
        this.cashBalanceMapper = cashBalanceMapper;
    }

    /**
     * Save a cashBalance.
     *
     * @param cashBalanceDTO the entity to save.
     * @return the persisted entity.
     */
    public CashBalanceDTO save(CashBalanceDTO cashBalanceDTO) {
        LOG.debug("Request to save CashBalance : {}", cashBalanceDTO);
        CashBalance cashBalance = cashBalanceMapper.toEntity(cashBalanceDTO);
        cashBalance = cashBalanceRepository.save(cashBalance);
        return cashBalanceMapper.toDto(cashBalance);
    }

    /**
     * Update a cashBalance.
     *
     * @param cashBalanceDTO the entity to save.
     * @return the persisted entity.
     */
    public CashBalanceDTO update(CashBalanceDTO cashBalanceDTO) {
        LOG.debug("Request to update CashBalance : {}", cashBalanceDTO);
        CashBalance cashBalance = cashBalanceMapper.toEntity(cashBalanceDTO);
        cashBalance.setIsPersisted();
        cashBalance = cashBalanceRepository.save(cashBalance);
        return cashBalanceMapper.toDto(cashBalance);
    }

    /**
     * Partially update a cashBalance.
     *
     * @param cashBalanceDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CashBalanceDTO> partialUpdate(CashBalanceDTO cashBalanceDTO) {
        LOG.debug("Request to partially update CashBalance : {}", cashBalanceDTO);

        return cashBalanceRepository
            .findById(cashBalanceDTO.getId())
            .map(existingCashBalance -> {
                cashBalanceMapper.partialUpdate(existingCashBalance, cashBalanceDTO);

                return existingCashBalance;
            })
            .map(cashBalanceRepository::save)
            .map(cashBalanceMapper::toDto);
    }

    /**
     * Get one cashBalance by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CashBalanceDTO> findOne(Long id) {
        LOG.debug("Request to get CashBalance : {}", id);
        return cashBalanceRepository.findById(id).map(cashBalanceMapper::toDto);
    }

    /**
     * Delete the cashBalance by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CashBalance : {}", id);
        cashBalanceRepository.deleteById(id);
    }
}
