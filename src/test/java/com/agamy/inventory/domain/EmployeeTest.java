package com.agamy.inventory.domain;

import static com.agamy.inventory.domain.EmployeeTestSamples.*;
import static com.agamy.inventory.domain.SalaryPaymentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EmployeeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Employee.class);
        Employee employee1 = getEmployeeSample1();
        Employee employee2 = new Employee();
        assertThat(employee1).isNotEqualTo(employee2);

        employee2.setId(employee1.getId());
        assertThat(employee1).isEqualTo(employee2);

        employee2 = getEmployeeSample2();
        assertThat(employee1).isNotEqualTo(employee2);
    }

    @Test
    void salaryPaymentTest() {
        Employee employee = getEmployeeRandomSampleGenerator();
        SalaryPayment salaryPaymentBack = getSalaryPaymentRandomSampleGenerator();

        employee.addSalaryPayment(salaryPaymentBack);
        assertThat(employee.getSalaryPayments()).containsOnly(salaryPaymentBack);
        assertThat(salaryPaymentBack.getEmployee()).isEqualTo(employee);

        employee.removeSalaryPayment(salaryPaymentBack);
        assertThat(employee.getSalaryPayments()).doesNotContain(salaryPaymentBack);
        assertThat(salaryPaymentBack.getEmployee()).isNull();

        employee.salaryPayments(new HashSet<>(Set.of(salaryPaymentBack)));
        assertThat(employee.getSalaryPayments()).containsOnly(salaryPaymentBack);
        assertThat(salaryPaymentBack.getEmployee()).isEqualTo(employee);

        employee.setSalaryPayments(new HashSet<>());
        assertThat(employee.getSalaryPayments()).doesNotContain(salaryPaymentBack);
        assertThat(salaryPaymentBack.getEmployee()).isNull();
    }
}
