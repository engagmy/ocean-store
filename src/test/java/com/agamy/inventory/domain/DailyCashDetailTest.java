package com.agamy.inventory.domain;

import static com.agamy.inventory.domain.DailyCashDetailTestSamples.*;
import static com.agamy.inventory.domain.DailyCashReconciliationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DailyCashDetailTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DailyCashDetail.class);
        DailyCashDetail dailyCashDetail1 = getDailyCashDetailSample1();
        DailyCashDetail dailyCashDetail2 = new DailyCashDetail();
        assertThat(dailyCashDetail1).isNotEqualTo(dailyCashDetail2);

        dailyCashDetail2.setId(dailyCashDetail1.getId());
        assertThat(dailyCashDetail1).isEqualTo(dailyCashDetail2);

        dailyCashDetail2 = getDailyCashDetailSample2();
        assertThat(dailyCashDetail1).isNotEqualTo(dailyCashDetail2);
    }

    @Test
    void dailyCashReconciliationTest() {
        DailyCashDetail dailyCashDetail = getDailyCashDetailRandomSampleGenerator();
        DailyCashReconciliation dailyCashReconciliationBack = getDailyCashReconciliationRandomSampleGenerator();

        dailyCashDetail.setDailyCashReconciliation(dailyCashReconciliationBack);
        assertThat(dailyCashDetail.getDailyCashReconciliation()).isEqualTo(dailyCashReconciliationBack);

        dailyCashDetail.dailyCashReconciliation(null);
        assertThat(dailyCashDetail.getDailyCashReconciliation()).isNull();
    }
}
