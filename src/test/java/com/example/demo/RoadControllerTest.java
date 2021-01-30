package com.example.demo;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@SpringBootTest
@AutoConfigureMockMvc
public class RoadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void get10RandomsRoad() throws Exception {
        for (int i = 0; i < 10; i++) {
            int roadId = RandomUtils.nextInt(1, 1000);
            this.mockMvc.perform(MockMvcRequestBuilders.get("/road/{id}/ranks", roadId))
                        .andExpect(status().isOk())
                        .andDo(MockMvcResultHandlers.print());
        }
    }
}
