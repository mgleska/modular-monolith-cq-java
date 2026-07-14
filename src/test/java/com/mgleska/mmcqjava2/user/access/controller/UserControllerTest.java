package com.mgleska.mmcqjava2.user.access.controller;

import com.mgleska.mmcqjava2.shared.CustomExceptionHandler;
import com.mgleska.mmcqjava2.shared.MainCommand;
import com.mgleska.mmcqjava2.user.action.command.AdminLoginCmd;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import picocli.CommandLine;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CustomExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MainCommand mainCommand;

    @MockitoBean
    private CommandLine.IFactory factory;

    @MockitoBean
    private AdminLoginCmd loginCmd;

    @Test
    void loginReturnsTokenFromLoginCmd() throws Exception {
        when(loginCmd.handle(new AdminLoginCmd.ParamDto("user@example.com", "User Name")))
            .thenReturn(new AdminLoginCmd.ResultDto("login-token"));

        mockMvc.perform(post("/api/admin/user/login")
                .contentType("application/json")
                .content("{\"email\":\"user@example.com\",\"name\":\"User Name\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("login-token"));
    }

    @Test
    void loginReturnsUnprocessableContentWhenEmailIsMissing() throws Exception {
        mockMvc.perform(post("/api/admin/user/login")
                .contentType("application/json")
                .content("{\"name\":\"User Name\"}"))
            .andExpect(status().isUnprocessableContent());

        verify(loginCmd, never()).handle(any());
    }
}
