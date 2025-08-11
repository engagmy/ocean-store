package com.agamy.inventory.service;

import com.agamy.inventory.domain.OutgoingPayment;
import com.agamy.inventory.repository.OutgoingPaymentRepository;
import com.agamy.inventory.service.dto.OutgoingPaymentDTO;
import com.agamy.inventory.service.mapper.OutgoingPaymentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.agamy.inventory.domain.OutgoingPayment}.
 */
@Service
@Transactional
public class OutgoingPaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(OutgoingPaymentService.class);

    private final OutgoingPaymentRepository outgoingPaymentRepository;

    private final OutgoingPaymentMapper outgoingPaymentMapper;

    public OutgoingPaymentService(OutgoingPaymentRepository outgoingPaymentRepository, OutgoingPaymentMapper outgoingPaymentMapper) {
        this.outgoingPaymentRepository = outgoingPaymentRepository;
        this.outgoingPaymentMapper = outgoingPaymentMapper;
    }

    /**
     * Save a outgoingPayment.
     *
     * @param outgoingPaymentDTO the entity to save.
     * @return the persisted entity.
     */
    public OutgoingPaymentDTO save(OutgoingPaymentDTO outgoingPaymentDTO) {
        LOG.debug("Request to save OutgoingPayment : {}", outgoingPaymentDTO);
        OutgoingPayment outgoingPayment = outgoingPaymentMapper.toEntity(outgoingPaymentDTO);
        outgoingPayment = outgoingPaymentRepository.save(outgoingPayment);
        return outgoingPaymentMapper.toDto(outgoingPayment);
    }

    /**
     * Update a outgoingPayment.
     *
     * @param outgoingPaymentDTO the entity to save.
     * @return the persisted entity.
     */
    public OutgoingPaymentDTO update(OutgoingPaymentDTO outgoingPaymentDTO) {
        LOG.debug("Request to update OutgoingPayment : {}", outgoingPaymentDTO);
        OutgoingPayment outgoingPayment = outgoingPaymentMapper.toEntity(outgoingPaymentDTO);
        outgoingPayment.setIsPersisted();
        outgoingPayment = outgoingPaymentRepository.save(outgoingPayment);
        return outgoingPaymentMapper.toDto(outgoingPayment);
    }

    /**
     * Partially update a outgoingPayment.
     *
     * @param outgoingPaymentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OutgoingPaymentDTO> partialUpdate(OutgoingPaymentDTO outgoingPaymentDTO) {
        LOG.debug("Request to partially update OutgoingPayment : {}", outgoingPaymentDTO);

        return outgoingPaymentRepository
            .findById(outgoingPaymentDTO.getId())
            .map(existingOutgoingPayment -> {
                outgoingPaymentMapper.partialUpdate(existingOutgoingPayment, outgoingPaymentDTO);

                return existingOutgoingPayment;
            })
            .map(outgoingPaymentRepository::save)
            .map(outgoingPaymentMapper::toDto);
    }

    /**
     * Get one outgoingPayment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OutgoingPaymentDTO> findOne(Long id) {
        LOG.debug("Request to get OutgoingPayment : {}", id);
        return outgoingPaymentRepository.findById(id).map(outgoingPaymentMapper::toDto);
    }

    /**
     * Delete the outgoingPayment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete OutgoingPayment : {}", id);
        outgoingPaymentRepository.deleteById(id);
    }
}
