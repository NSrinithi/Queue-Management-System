package com.example.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.backend.config.RedisConfig;

@SpringBootTest(
    properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration"
    }
)
@AutoConfigureMockMvc
public class QueueIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedisTemplate<String, Object> redis;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockBean
    private ListOperations<String, Object> listOps;

    @BeforeEach
    void setup() {
        when(redis.opsForList()).thenReturn(listOps);

        Long[] savedId = new Long[1];

        when(listOps.rightPush(any(), any())).thenAnswer(invocation -> {
            savedId[0] = (Long) invocation.getArgument(1);
            return 1L;
        });

        when(listOps.leftPop(any())).thenAnswer(invocation -> savedId[0]);
    }
    
    @Test
    void fullFlow() throws Exception{
        String json="""
                {
                    "name":"sri",
                    "isVip":false
                }
                """;
        mockMvc.perform(post("/queue/join")
                     .contentType(MediaType.APPLICATION_JSON)
                     .content(json))
                     .andExpect(status().isCreated())
                     .andExpect(jsonPath("$.isVip").value(false));

        mockMvc.perform(get("/queue/next"))
               .andExpect(status().isOk());
    }

}
