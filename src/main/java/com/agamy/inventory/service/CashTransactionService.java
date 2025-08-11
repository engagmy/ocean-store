package com.agamy.inventory.service;

import com.agamy.inventory.domain.CashTransaction;
import com.agamy.inventory.repository.CashTransactionRepository;
import com.agamy.inventory.service.dto.CashTransactionDTO;
import com.agamy.inventory.service.mapper.CashTransactionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.agamy.inventory.domain.CashTransaction}.
 */
@Service
@Transactional
public class CashTransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(CashTransactionService.class);

    private final CashTransactionRepository cashTransactionRepository;

    private final CashTransactionMapper cashTransactionMapper;

    public CashTransactionService(CashTransactionRepository cashTransactionRepository, CashTransactionMapper cashTransactionMapper) {
        this.cashTransactionRepository = cashTransactionRepository;
        this.cashTransactionMapper = cashTransactionMapper;
    }

    /**
     * Save a cashTransaction.
     *
     * @param cashTransactionDTO the entity to save.
     * @return the persisted entity.
     */
    public CashTransactionDTO save(CashTransactionDTO cashTransactionDTO) {
        LOG.debug("Request to save CashTransaction : {}", cashTransactionDTO);
        CashTransaction cashTransaction = cashTransactionMapper.toEntity(cashTransactionDTO);
        cashTransaction = cashTransactionRepository.save(cashTransaction);
        return cashTransactionMapper.toDto(cashTransaction);
    }

    /**
     * Update a cashTransaction.
     *
     * @param cashTransactionDTO the entity to save.
     * @return the persisted entity.
     */
    public CashTransactionDTO update(CashTransactionDTO cashTransactionDTO) {
        LOG.debug("Request to update CashTransaction : {}", cashTransactionDTO);
        CashTransaction cashTransaction = cashTransactionMapper.toEntity(cashTransactionDTO);
        cashTransaction.setIsPersisted();
        cashTransaction = cashTransactionRepository.save(cashTransaction);
        return cashTransactionMapper.toDto(cashTransaction);
    }

    /**
     * Partially update a cashTransaction.
     *
     * @param cashTransactionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CashTransactionDTO> partialUpdate(CashTransactionDTO cashTransactionDTO) {
        LOG.debug("Request to partially update CashTransaction : {}", cashTransactionDTO);

        return cashTransactionRepository
            .findById(cashTransactionDTO.getId())
            .map(existingCashTransaction -> {
                cashTransactionMapper.partialUpdate(existingCashTransaction, cashTransactionDTO);

                return existingCashTransaction;
            })
            .map(cashTransactionRepository::save)
            .map(cashTransactionMapper::toDto);
    }

    /**
     * Get one cashTransaction by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CashTransactionDTO> findOne(Long id) {
        LOG.debug("Request to get CashTransaction : {}", id);
        return cashTransactionRepository.findById(id).map(cashTransactionMapper::toDto);
    }

    /**
     * Delete the cashTransaction by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CashTransaction : {}", id);
        cashTransactionRepository.deleteById(id);
    }
}
