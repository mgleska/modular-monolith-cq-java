package com.mgleska.mmcqjava2.store.access.controller;

import com.mgleska.mmcqjava2.shared.CustomExceptionHandler;
import com.mgleska.mmcqjava2.shared.MainCommand;
import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import com.mgleska.mmcqjava2.store.action.query.GetStoreDetailsQry;
import com.mgleska.mmcqjava2.store.action.query.GetStoreListQry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import picocli.CommandLine;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StoreController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CustomExceptionHandler.class)
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MainCommand mainCommand;

    @MockitoBean
    private CommandLine.IFactory factory;

    @MockitoBean
    private GetStoreDetailsQry getStoreDetailsQry;

    @MockitoBean
    private GetStoreListQry getStoreListQry;

    @Test
    void listReturnsResultFromGetStoreListQry() throws Exception {
        when(getStoreListQry.handle()).thenReturn(List.of(
            new GetStoreListQry.ListItemDto(1, "ext-1", "Store One")
        ));

        mockMvc.perform(get("/api/store/list").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].externalId").value("ext-1"))
            .andExpect(jsonPath("$[0].name").value("Store One"));
    }

    @Test
    void detailsReturnsResultFromGetStoreDetailsQry() throws Exception {
        when(getStoreDetailsQry.handle(5)).thenReturn(
            new GetStoreDetailsQry.ResultDto(5, "ext-5", "Store Five", "Main Street 5")
        );

        mockMvc.perform(get("/api/store/5").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.externalId").value("ext-5"))
            .andExpect(jsonPath("$.name").value("Store Five"))
            .andExpect(jsonPath("$.address").value("Main Street 5"));
    }

    @Test
    void detailsPropagatesAppValidationExceptionFromQry() throws Exception {
        when(getStoreDetailsQry.handle(5)).thenThrow(new AppValidationException("id", "Store not found"));

        mockMvc.perform(get("/api/store/5").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.errors[0].field").value("id"));
    }

    @Test
    void detailsReturnsNotFoundForNonNumericId() throws Exception {
        mockMvc.perform(get("/api/store/abc").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(getStoreDetailsQry, never()).handle(any(Integer.class));
    }
}
