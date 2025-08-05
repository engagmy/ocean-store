package com.agamy.inventory.domain;

import static com.agamy.inventory.domain.DailyCashDetailTestSamples.*;
import static com.agamy.inventory.domain.DailyCashReconciliationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DailyCashReconciliationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DailyCashReconciliation.class);
        DailyCashReconciliation dailyCashReconciliation1 = getDailyCashReconciliationSample1();
        DailyCashReconciliation dailyCashReconciliation2 = new DailyCashReconciliation();
        assertThat(dailyCashReconciliation1).isNotEqualTo(dailyCashReconciliation2);

        dailyCashReconciliation2.setId(dailyCashReconciliation1.getId());
        assertThat(dailyCashReconciliation1).isEqualTo(dailyCashReconciliation2);

        dailyCashReconciliation2 = getDailyCashReconciliationSample2();
        assertThat(dailyCashReconciliation1).isNotEqualTo(dailyCashReconciliation2);
    }

    @Test
    void dailyCashDetailTest() {
        DailyCashReconciliation dailyCashReconciliation = getDailyCashReconciliationRandomSampleGenerator();
        DailyCashDetail dailyCashDetailBack = getDailyCashDetailRandomSampleGenerator();

        dailyCashReconciliation.addDailyCashDetail(dailyCashDetailBack);
        assertThat(dailyCashReconciliation.getDailyCashDetails()).containsOnly(dailyCashDetailBack);
        assertThat(dailyCashDetailBack.getDailyCashReconciliation()).isEqualTo(dailyCashReconciliation);

        dailyCashReconciliation.removeDailyCashDetail(dailyCashDetailBack);
        assertThat(dailyCashReconciliation.getDailyCashDetails()).doesNotContain(dailyCashDetailBack);
        assertThat(dailyCashDetailBack.getDailyCashReconciliation()).isNull();

        dailyCashReconciliation.dailyCashDetails(new HashSet<>(Set.of(dailyCashDetailBack)));
        assertThat(dailyCashReconciliation.getDailyCashDetails()).containsOnly(dailyCashDetailBack);
        assertThat(dailyCashDetailBack.getDailyCashReconciliation()).isEqualTo(dailyCashReconciliation);

        dailyCashReconciliation.setDailyCashDetails(new HashSet<>());
        assertThat(dailyCashReconciliation.getDailyCashDetails()).doesNotContain(dailyCashDetailBack);
        assertThat(dailyCashDetailBack.getDailyCashReconciliation()).isNull();
    }
}
