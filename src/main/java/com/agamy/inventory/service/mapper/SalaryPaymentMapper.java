package com.agamy.inventory.service.mapper;

import com.agamy.inventory.domain.Employee;
import com.agamy.inventory.domain.SalaryPayment;
import com.agamy.inventory.service.dto.EmployeeDTO;
import com.agamy.inventory.service.dto.SalaryPaymentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SalaryPayment} and its DTO {@link SalaryPaymentDTO}.
 */
@Mapper(componentModel = "spring")
public interface SalaryPaymentMapper extends EntityMapper<SalaryPaymentDTO, SalaryPayment> {
    @Mapping(target = "employee", source = "employee", qualifiedByName = "employeeId")
    SalaryPaymentDTO toDto(SalaryPayment s);

    @Named("employeeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EmployeeDTO toDtoEmployeeId(Employee employee);
}
