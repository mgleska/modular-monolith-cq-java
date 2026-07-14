package com.mgleska.mmcqjava2.customer.access.controller;

import com.mgleska.mmcqjava2.customer.action.command.ChangeStoreCmd;
import com.mgleska.mmcqjava2.customer.action.command.LoginCmd;
import com.mgleska.mmcqjava2.shared.CustomExceptionHandler;
import com.mgleska.mmcqjava2.shared.MainCommand;
import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
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

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CustomExceptionHandler.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MainCommand mainCommand;

    @MockitoBean
    private CommandLine.IFactory factory;

    @MockitoBean
    private LoginCmd loginCmd;

    @MockitoBean
    private ChangeStoreCmd changeStoreCmd;

    @Test
    void loginReturnsTokenFromLoginCmd() throws Exception {
        when(loginCmd.handle(new LoginCmd.ParamDto(5))).thenReturn(new LoginCmd.ResultDto("login-token"));

        mockMvc.perform(post("/api/customer/login")
                .contentType("application/json")
                .content("{\"customerId\":5}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("login-token"));
    }

    @Test
    void loginReturnsUnprocessableContentOnInvalidBody() throws Exception {
        mockMvc.perform(post("/api/customer/login")
                .contentType("application/json")
                .content("{\"customerId\":-1}"))
            .andExpect(status().isUnprocessableContent());

        verify(loginCmd, never()).handle(any());
    }

    @Test
    void loginPropagatesAppValidationExceptionFromCmd() throws Exception {
        when(loginCmd.handle(any())).thenThrow(new AppValidationException("customerId", "Customer not found"));

        mockMvc.perform(post("/api/customer/login")
                .contentType("application/json")
                .content("{\"customerId\":7}"))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.errors[0].field").value("customerId"));
    }

    @Test
    void changeStoreReturnsTokenFromChangeStoreCmd() throws Exception {
        when(changeStoreCmd.handle(new ChangeStoreCmd.ParamDto(3))).thenReturn(new ChangeStoreCmd.ResultDto("store-token"));

        mockMvc.perform(post("/api/customer/change-store")
                .contentType("application/json")
                .content("{\"storeId\":3}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("store-token"));
    }

    @Test
    void changeStoreReturnsUnprocessableContentOnInvalidBody() throws Exception {
        mockMvc.perform(post("/api/customer/change-store")
                .contentType("application/json")
                .content("{\"storeId\":0}"))
            .andExpect(status().isUnprocessableContent());

        verify(changeStoreCmd, never()).handle(any());
    }
}
