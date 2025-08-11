package com.agamy.inventory.service;

import com.agamy.inventory.domain.Bill;
import com.agamy.inventory.repository.BillRepository;
import com.agamy.inventory.service.dto.BillDTO;
import com.agamy.inventory.service.mapper.BillMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.agamy.inventory.domain.Bill}.
 */
@Service
@Transactional
public class BillService {

    private static final Logger LOG = LoggerFactory.getLogger(BillService.class);

    private final BillRepository billRepository;

    private final BillMapper billMapper;

    public BillService(BillRepository billRepository, BillMapper billMapper) {
        this.billRepository = billRepository;
        this.billMapper = billMapper;
    }

    /**
     * Save a bill.
     *
     * @param billDTO the entity to save.
     * @return the persisted entity.
     */
    public BillDTO save(BillDTO billDTO) {
        LOG.debug("Request to save Bill : {}", billDTO);
        Bill bill = billMapper.toEntity(billDTO);
        bill = billRepository.save(bill);
        return billMapper.toDto(bill);
    }

    /**
     * Update a bill.
     *
     * @param billDTO the entity to save.
     * @return the persisted entity.
     */
    public BillDTO update(BillDTO billDTO) {
        LOG.debug("Request to update Bill : {}", billDTO);
        Bill bill = billMapper.toEntity(billDTO);
        bill.setIsPersisted();
        bill = billRepository.save(bill);
        return billMapper.toDto(bill);
    }

    /**
     * Partially update a bill.
     *
     * @param billDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BillDTO> partialUpdate(BillDTO billDTO) {
        LOG.debug("Request to partially update Bill : {}", billDTO);

        return billRepository
            .findById(billDTO.getId())
            .map(existingBill -> {
                billMapper.partialUpdate(existingBill, billDTO);

                return existingBill;
            })
            .map(billRepository::save)
            .map(billMapper::toDto);
    }

    /**
     * Get one bill by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BillDTO> findOne(Long id) {
        LOG.debug("Request to get Bill : {}", id);
        return billRepository.findById(id).map(billMapper::toDto);
    }

    /**
     * Delete the bill by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Bill : {}", id);
        billRepository.deleteById(id);
    }
}
