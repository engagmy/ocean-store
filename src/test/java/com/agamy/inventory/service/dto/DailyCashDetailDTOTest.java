package com.agamy.inventory.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DailyCashDetailDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DailyCashDetailDTO.class);
        DailyCashDetailDTO dailyCashDetailDTO1 = new DailyCashDetailDTO();
        dailyCashDetailDTO1.setId(1L);
        DailyCashDetailDTO dailyCashDetailDTO2 = new DailyCashDetailDTO();
        assertThat(dailyCashDetailDTO1).isNotEqualTo(dailyCashDetailDTO2);
        dailyCashDetailDTO2.setId(dailyCashDetailDTO1.getId());
        assertThat(dailyCashDetailDTO1).isEqualTo(dailyCashDetailDTO2);
        dailyCashDetailDTO2.setId(2L);
        assertThat(dailyCashDetailDTO1).isNotEqualTo(dailyCashDetailDTO2);
        dailyCashDetailDTO1.setId(null);
        assertThat(dailyCashDetailDTO1).isNotEqualTo(dailyCashDetailDTO2);
    }
}
