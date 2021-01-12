package uk.tw.energy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.tw.energy.builders.MeterReadingsBuilder;
import uk.tw.energy.domain.MeterReadings;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = App.class)
@AutoConfigureMockMvc
public class EndpointTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldStoreReadings() throws Exception {
        MeterReadings meterReadings = new MeterReadingsBuilder().generateElectricityReadings().build();

        mockMvc.perform(
                post("/readings/store")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(meterReadings)
                )
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
    }

    @Test
    public void givenMeterIdShouldReturnAMeterReadingAssociatedWithMeterId() throws Exception {
        String smartMeterId = "bob";
        populateMeterReadingsForMeter(smartMeterId);

        mockMvc.perform(
                get("/readings/read/{smartMeterId}", smartMeterId)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

    }

    @Test
    public void shouldCalculateAllPrices() throws Exception {
        String smartMeterId = "bob";
        populateMeterReadingsForMeter(smartMeterId);

        mockMvc.perform(
                get("/price-plans/compare-all/{smartMeterId}", smartMeterId)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    public void givenMeterIdAndLimitShouldReturnRecommendedCheapestPricePlans() throws Exception {
        String smartMeterId = "bob";
        populateMeterReadingsForMeter(smartMeterId);

        mockMvc.perform(
                get("/price-plans/compare-all/{smartMeterId}/?limit={limit}", smartMeterId, 2)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    private void populateMeterReadingsForMeter(String smartMeterId) throws Exception {
        MeterReadings readings = new MeterReadingsBuilder().setSmartMeterId(smartMeterId)
                .generateElectricityReadings(20)
                .build();

        mockMvc.perform(
                post("/readings/store")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(readings)
                )
        )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }
}
