package cn.leansd.cotrip.application.cotrip;

import cn.leansd.base.model.UserId;
import cn.leansd.base.types.TimeSpan;
import cn.leansd.cotrip.domain.cotrip.CoTripRepository;
import cn.leansd.cotrip.domain.plan.PlanSpecification;
import cn.leansd.cotrip.domain.plan.TripPlan;
import cn.leansd.cotrip.domain.plan.TripPlanRepository;
import cn.leansd.cotrip.domain.plan.TripPlanStatus;
import cn.leansd.cotrip.application.plan.TripPlanDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static cn.leansd.cotrip.application.TestMap.orientalPear;
import static cn.leansd.cotrip.application.TestMap.peopleSquare;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class CoTripMatchingResultMvcTest {
    private static final String urlTripPlan = "/cotrip/plan/v1/trip-plans/";

    @Autowired
    private TripPlanRepository tripPlanRepository;
    @Autowired
    private CoTripRepository coTripRepository;

    @Autowired
    private MockMvc mockMvc;

    TripPlanDTO firstTripPlan = null;
    TripPlanDTO secondTripPlan = null;
    ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    public void setUp() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        LocalDateTime Y2305010800 = LocalDateTime.of(2023, 5, 1, 8, 00);
        LocalDateTime Y2305010830 = LocalDateTime.of(2023, 5, 1, 8, 30);
        TripPlanDTO tripPlanDTO = new TripPlanDTO(new PlanSpecification(orientalPear, peopleSquare, TimeSpan.builder()
                .start(Y2305010800)
                .end(Y2305010830)
                .build(),1));
        TripPlan firstPlan = new TripPlan(UserId.of("user_id_1"),
                tripPlanDTO.getPlanSpecification());
        TripPlan secondPlan = new TripPlan(UserId.of("user_id_2"),
                tripPlanDTO.getPlanSpecification());

        MvcResult response_1 = mockMvc.perform(post(urlTripPlan)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("user-id", "user-id-1")
                        .content(objectMapper.writeValueAsString(firstPlan))
                )
                .andExpect(status().isCreated())
                .andReturn();

        firstTripPlan = objectMapper.readValue(response_1.getResponse().getContentAsString(), TripPlanDTO.class);

        MvcResult response_2 = mockMvc.perform(post(urlTripPlan)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("user-id", "user-id-2")
                        .content(objectMapper.writeValueAsString(secondPlan))
                )
                .andExpect(status().isCreated())
                .andReturn();

        secondTripPlan = objectMapper.readValue(response_2.getResponse().getContentAsString(), TripPlanDTO.class);
    }



    @DisplayName("匹配成功后应该更新TripPlan的状态(数据库验证)")
    @Test
    public void shouldChangeTripPlanStatusWhenMatchedVerifiedByDb() {
        TripPlan savedExistingPlan = tripPlanRepository.findById(firstTripPlan.getId()).orElse(null);
        TripPlan savedNewPlan = tripPlanRepository.findById(secondTripPlan.getId()).orElse(null);
        assertThat(savedExistingPlan.getStatus()).isEqualTo(TripPlanStatus.JOINED);
        assertThat(savedNewPlan.getStatus()).isEqualTo(TripPlanStatus.JOINED);
    }

    @DisplayName("匹配成功后应该更新TripPlan的状态(接口验证)")
    @Test
    public void shouldChangeTripPlanStatusWhenMatchedVerifiedByAPI() throws Exception {
        MvcResult result1 = mockMvc.perform(
                        get(urlTripPlan + firstTripPlan.getId())
                                .header("User-Id", "user-id-1")
                )
                .andExpect(status().isOk())
                .andReturn();

        TripPlanDTO tripPlanDTO1 = objectMapper.readValue(result1.getResponse().getContentAsString(), TripPlanDTO.class);
        assertThat(tripPlanDTO1.getStatus()).isEqualTo(TripPlanStatus.JOINED.toString());

        MvcResult result2 = mockMvc.perform(
                        get(urlTripPlan + secondTripPlan.getId())
                                .header("User-Id", "user-id-2")
                )
                .andExpect(status().isOk())
                .andReturn();

        TripPlanDTO tripPlanDTO2 = objectMapper.readValue(result2.getResponse().getContentAsString(), TripPlanDTO.class);
        assertThat(tripPlanDTO2.getStatus()).isEqualTo(TripPlanStatus.JOINED.toString());
    }

}
