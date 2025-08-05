package com.agamy.inventory.domain;

import static com.agamy.inventory.domain.EmployeeTestSamples.*;
import static com.agamy.inventory.domain.SalaryPaymentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SalaryPaymentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalaryPayment.class);
        SalaryPayment salaryPayment1 = getSalaryPaymentSample1();
        SalaryPayment salaryPayment2 = new SalaryPayment();
        assertThat(salaryPayment1).isNotEqualTo(salaryPayment2);

        salaryPayment2.setId(salaryPayment1.getId());
        assertThat(salaryPayment1).isEqualTo(salaryPayment2);

        salaryPayment2 = getSalaryPaymentSample2();
        assertThat(salaryPayment1).isNotEqualTo(salaryPayment2);
    }

    @Test
    void employeeTest() {
        SalaryPayment salaryPayment = getSalaryPaymentRandomSampleGenerator();
        Employee employeeBack = getEmployeeRandomSampleGenerator();

        salaryPayment.setEmployee(employeeBack);
        assertThat(salaryPayment.getEmployee()).isEqualTo(employeeBack);

        salaryPayment.employee(null);
        assertThat(salaryPayment.getEmployee()).isNull();
    }
}
