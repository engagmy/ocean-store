package com.agamy.inventory.service;

import com.agamy.inventory.domain.SalaryPayment;
import com.agamy.inventory.repository.SalaryPaymentRepository;
import com.agamy.inventory.service.dto.SalaryPaymentDTO;
import com.agamy.inventory.service.mapper.SalaryPaymentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.agamy.inventory.domain.SalaryPayment}.
 */
@Service
@Transactional
public class SalaryPaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(SalaryPaymentService.class);

    private final SalaryPaymentRepository salaryPaymentRepository;

    private final SalaryPaymentMapper salaryPaymentMapper;

    public SalaryPaymentService(SalaryPaymentRepository salaryPaymentRepository, SalaryPaymentMapper salaryPaymentMapper) {
        this.salaryPaymentRepository = salaryPaymentRepository;
        this.salaryPaymentMapper = salaryPaymentMapper;
    }

    /**
     * Save a salaryPayment.
     *
     * @param salaryPaymentDTO the entity to save.
     * @return the persisted entity.
     */
    public SalaryPaymentDTO save(SalaryPaymentDTO salaryPaymentDTO) {
        LOG.debug("Request to save SalaryPayment : {}", salaryPaymentDTO);
        SalaryPayment salaryPayment = salaryPaymentMapper.toEntity(salaryPaymentDTO);
        salaryPayment = salaryPaymentRepository.save(salaryPayment);
        return salaryPaymentMapper.toDto(salaryPayment);
    }

    /**
     * Update a salaryPayment.
     *
     * @param salaryPaymentDTO the entity to save.
     * @return the persisted entity.
     */
    public SalaryPaymentDTO update(SalaryPaymentDTO salaryPaymentDTO) {
        LOG.debug("Request to update SalaryPayment : {}", salaryPaymentDTO);
        SalaryPayment salaryPayment = salaryPaymentMapper.toEntity(salaryPaymentDTO);
        salaryPayment.setIsPersisted();
        salaryPayment = salaryPaymentRepository.save(salaryPayment);
        return salaryPaymentMapper.toDto(salaryPayment);
    }

    /**
     * Partially update a salaryPayment.
     *
     * @param salaryPaymentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SalaryPaymentDTO> partialUpdate(SalaryPaymentDTO salaryPaymentDTO) {
        LOG.debug("Request to partially update SalaryPayment : {}", salaryPaymentDTO);

        return salaryPaymentRepository
            .findById(salaryPaymentDTO.getId())
            .map(existingSalaryPayment -> {
                salaryPaymentMapper.partialUpdate(existingSalaryPayment, salaryPaymentDTO);

                return existingSalaryPayment;
            })
            .map(salaryPaymentRepository::save)
            .map(salaryPaymentMapper::toDto);
    }

    /**
     * Get one salaryPayment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SalaryPaymentDTO> findOne(Long id) {
        LOG.debug("Request to get SalaryPayment : {}", id);
        return salaryPaymentRepository.findById(id).map(salaryPaymentMapper::toDto);
    }

    /**
     * Delete the salaryPayment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SalaryPayment : {}", id);
        salaryPaymentRepository.deleteById(id);
    }
}
