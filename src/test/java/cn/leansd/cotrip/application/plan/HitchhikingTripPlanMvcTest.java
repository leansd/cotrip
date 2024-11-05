package cn.leansd.cotrip.application.plan;

import cn.leansd.base.types.TimeSpan;
import cn.leansd.cotrip.domain.plan.PlanSpecification;
import cn.leansd.cotrip.domain.plan.TripPlanType;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static cn.leansd.base.RestTemplateUtil.asJson;
import static cn.leansd.cotrip.application.TestMap.hqAirport;
import static cn.leansd.cotrip.application.TestMap.peopleSquare;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@DirtiesContext
public class HitchhikingTripPlanMvcTest {
    private static final String urlTripPlan = "/cotrip/plan/v1/trip-plans/";
    @Autowired
    MockMvc mockMvc;

    @DisplayName("创建顺风车乘客单应该成功且返回正确的出行计划类型")
    @Test
    public void testCreateHitchhikingTripPlan() throws Exception {
        TripPlanDTO tripPlanDTO = TripPlanDTO.builder().planSpecification(new PlanSpecification(hqAirport, peopleSquare, TimeSpan.builder()
                .start(LocalDateTime.of(2023, 5, 1, 8, 0))
                .end(LocalDateTime.of(2023, 5, 1, 8, 30))
                .build(), 1))
                .planType(TripPlanType.HITCHHIKING_CONSUMER.name())
                .build();
        MvcResult result = mockMvc.perform(post(urlTripPlan)
                .header("user-id", "user-id-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(tripPlanDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        String tripPlanId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        result = mockMvc.perform(get(urlTripPlan + tripPlanId)
                        .header("user-id", "user-id-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        assertEquals(TripPlanType.HITCHHIKING_CONSUMER.name(),JsonPath.read(result.getResponse().getContentAsString(),"$.planType"));
    }
}
