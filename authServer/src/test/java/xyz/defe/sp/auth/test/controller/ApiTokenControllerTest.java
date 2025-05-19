package xyz.defe.sp.auth.test.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import xyz.defe.sp.auth.controller.ApiTokenController;
import xyz.defe.sp.auth.service.ApiTokenService;
import xyz.defe.sp.common.entity.spUser.ApiToken;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApiTokenController.class)
class ApiTokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApiTokenService apiTokenService;

    @Test
    void returnTokenForValidCredentials() throws Exception {
        ApiToken apiToken = new ApiToken();
        apiToken.setUid("uid123");
        apiToken.setToken("token123");
        Mockito.when(apiTokenService.generateToken("user", "pwd"))
                .thenReturn(apiToken);
        mockMvc.perform(post("/api/token")
                .param("uname", "user")
                .param("pwd", "pwd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token", is("token123")));
    }

    @Test
    void returnBadRequestWhenParamMissing() throws Exception {
        mockMvc.perform(post("/api/token").param("uname", "user"))
                .andExpect(jsonPath("$.status", is(400)));
        mockMvc.perform(post("/api/token").param("pwd", "pwd"))
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    void returnNullForInvalidCredentials() throws Exception {
        Mockito.when(apiTokenService.generateToken("user", "wrong")).thenReturn(null);
        mockMvc.perform(post("/api/token")
                .param("uname", "user")
                .param("pwd", "wrong"))
                .andExpect(jsonPath("$.data", nullValue()));
    }
}