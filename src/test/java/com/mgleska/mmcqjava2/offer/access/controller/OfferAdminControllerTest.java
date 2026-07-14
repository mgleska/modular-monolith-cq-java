package com.mgleska.mmcqjava2.offer.access.controller;

import com.mgleska.mmcqjava2.offer.action.command.ChangeVisibilityCmd;
import com.mgleska.mmcqjava2.offer.action.query.AdminGetListFiltersQry;
import com.mgleska.mmcqjava2.offer.action.query.AdminGetOfferDetailsQry;
import com.mgleska.mmcqjava2.offer.action.query.AdminGetOfferListQry;
import com.mgleska.mmcqjava2.shared.CustomExceptionHandler;
import com.mgleska.mmcqjava2.shared.MainCommand;
import com.mgleska.mmcqjava2.shared.exception.AppEntityVersionException;
import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OfferAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CustomExceptionHandler.class)
class OfferAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MainCommand mainCommand;

    @MockitoBean
    private CommandLine.IFactory factory;

    @MockitoBean
    private AdminGetListFiltersQry adminGetListFiltersQry;

    @MockitoBean
    private AdminGetOfferDetailsQry adminGetOfferDetailsQry;

    @MockitoBean
    private AdminGetOfferListQry adminGetOfferListQry;

    @MockitoBean
    private ChangeVisibilityCmd changeVisibilityCmd;

    @Test
    void filtersReturnsResultFromAdminGetListFiltersQry() throws Exception {
        var item = new AdminGetListFiltersQry.ResultItemDto(1, "Store One");
        when(adminGetListFiltersQry.handle()).thenReturn(new AdminGetListFiltersQry.ResultDto(List.of(item)));

        mockMvc.perform(get("/api/admin/offer/filters").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stores[0].storeId").value(1))
            .andExpect(jsonPath("$.stores[0].storeName").value("Store One"));
    }

    @Test
    void listReturnsResultFromAdminGetOfferListQry() throws Exception {
        var item = new AdminGetOfferListQry.ResultItemDto(1, "Store", true, "1234567890123", "Product", 500);
        when(adminGetOfferListQry.handle(new AdminGetOfferListQry.ParamDto(null, null, null, null)))
            .thenReturn(new AdminGetOfferListQry.ResultDto(List.of(item), 1, 10));

        mockMvc.perform(get("/api/admin/offer/list").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.perPage").value(10))
            .andExpect(jsonPath("$.items[0].id").value(1))
            .andExpect(jsonPath("$.items[0].storeName").value("Store"))
            .andExpect(jsonPath("$.items[0].visible").value(true))
            .andExpect(jsonPath("$.items[0].productEan").value("1234567890123"))
            .andExpect(jsonPath("$.items[0].productName").value("Product"))
            .andExpect(jsonPath("$.items[0].price").value(500));
    }

    @Test
    void listPassesQueryParamsToAdminGetOfferListQry() throws Exception {
        when(adminGetOfferListQry.handle(new AdminGetOfferListQry.ParamDto("milk", 4, 2, 20)))
            .thenReturn(new AdminGetOfferListQry.ResultDto(List.of(), 2, 20));

        mockMvc.perform(get("/api/admin/offer/list")
                .contentType(MediaType.APPLICATION_JSON)
                .param("search", "milk")
                .param("storeId", "4")
                .param("page", "2")
                .param("perPage", "20"))
            .andExpect(status().isOk());

        verify(adminGetOfferListQry).handle(new AdminGetOfferListQry.ParamDto("milk", 4, 2, 20));
    }

    @Test
    void detailsReturnsResultFromAdminGetOfferDetailsQry() throws Exception {
        when(adminGetOfferDetailsQry.handle(5)).thenReturn(new AdminGetOfferDetailsQry.ResultDto(
            5, 1, true, "1234567890123", "Product", 500, 400, "http://image", 10
        ));

        mockMvc.perform(get("/api/admin/offer/5").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.version").value(1))
            .andExpect(jsonPath("$.visible").value(true))
            .andExpect(jsonPath("$.productEan").value("1234567890123"))
            .andExpect(jsonPath("$.productName").value("Product"))
            .andExpect(jsonPath("$.price").value(500))
            .andExpect(jsonPath("$.lowestPrice").value(400))
            .andExpect(jsonPath("$.imageUrl").value("http://image"))
            .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    void detailsPropagatesAppValidationExceptionFromQry() throws Exception {
        when(adminGetOfferDetailsQry.handle(5)).thenThrow(new AppValidationException("offerId", "Offer not found"));

        mockMvc.perform(get("/api/admin/offer/5").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.errors[0].field").value("offerId"));
    }

    @Test
    void detailsReturnsNotFoundForNonNumericId() throws Exception {
        mockMvc.perform(get("/api/admin/offer/abc").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(adminGetOfferDetailsQry, never()).handle(any(Integer.class));
    }

    @Test
    void changeVisibilityCallsCommandWithParsedBody() throws Exception {
        mockMvc.perform(post("/api/admin/offer/change-visibility")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":5,\"version\":1,\"visible\":false}"))
            .andExpect(status().isOk());

        verify(changeVisibilityCmd).handle(new ChangeVisibilityCmd.ParamDto(5, 1, false));
    }

    @Test
    void changeVisibilityReturnsUnprocessableContentOnInvalidBody() throws Exception {
        mockMvc.perform(post("/api/admin/offer/change-visibility")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":null,\"version\":1,\"visible\":false}"))
            .andExpect(status().isUnprocessableContent());

        verify(changeVisibilityCmd, never()).handle(any());
    }

    @Test
    void changeVisibilityPropagatesAppEntityVersionExceptionFromCmd() throws Exception {
        var dto = new ChangeVisibilityCmd.ParamDto(5, 1, false);
        doThrow(new AppEntityVersionException()).when(changeVisibilityCmd).handle(dto);

        mockMvc.perform(post("/api/admin/offer/change-visibility")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":5,\"version\":1,\"visible\":false}"))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.errors[0].field").value("version"));
    }
}
