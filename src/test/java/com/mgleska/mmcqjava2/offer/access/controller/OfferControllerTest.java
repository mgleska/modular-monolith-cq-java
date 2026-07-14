package com.mgleska.mmcqjava2.offer.access.controller;

import com.mgleska.mmcqjava2.offer.action.enums.QuantityLevelEnum;
import com.mgleska.mmcqjava2.offer.action.query.GetOfferDetailsQry;
import com.mgleska.mmcqjava2.offer.action.query.GetOfferListQry;
import com.mgleska.mmcqjava2.shared.CustomExceptionHandler;
import com.mgleska.mmcqjava2.shared.MainCommand;
import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
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

@WebMvcTest(OfferController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CustomExceptionHandler.class)
class OfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MainCommand mainCommand;

    @MockitoBean
    private CommandLine.IFactory factory;

    @MockitoBean
    private GetOfferListQry getOfferListQry;

    @MockitoBean
    private GetOfferDetailsQry getOfferDetailsQry;

    @Test
    void listReturnsResultFromGetOfferListQry() throws Exception {
        var item = new GetOfferListQry.ResultItemDto(1, "1234567890123", "Product", 500, 400);
        when(getOfferListQry.handle(new GetOfferListQry.ParamDto(null)))
            .thenReturn(new GetOfferListQry.ResultDto(List.of(item), 1, 5));

        mockMvc.perform(get("/api/offer/list").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.perPage").value(5))
            .andExpect(jsonPath("$.items[0].id").value(1))
            .andExpect(jsonPath("$.items[0].productEan").value("1234567890123"))
            .andExpect(jsonPath("$.items[0].productName").value("Product"))
            .andExpect(jsonPath("$.items[0].price").value(500))
            .andExpect(jsonPath("$.items[0].lowestPrice").value(400));
    }

    @Test
    void listPassesPageParamToGetOfferListQry() throws Exception {
        when(getOfferListQry.handle(new GetOfferListQry.ParamDto(2)))
            .thenReturn(new GetOfferListQry.ResultDto(List.of(), 2, 5));

        mockMvc.perform(get("/api/offer/list").contentType(MediaType.APPLICATION_JSON).param("page", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(2));

        verify(getOfferListQry).handle(new GetOfferListQry.ParamDto(2));
    }

    @Test
    void listReturnsUnprocessableContentWhenPageIsNotPositive() throws Exception {
        mockMvc.perform(get("/api/offer/list").contentType(MediaType.APPLICATION_JSON).param("page", "0"))
            .andExpect(status().isUnprocessableContent());

        verify(getOfferListQry, never()).handle(any());
    }

    @Test
    void detailsReturnsResultFromGetOfferDetailsQry() throws Exception {
        when(getOfferDetailsQry.handle(5)).thenReturn(new GetOfferDetailsQry.ResultDto(
            5, "1234567890123", "Product", 500, 400, "http://image", QuantityLevelEnum.AVAILABLE
        ));

        mockMvc.perform(get("/api/offer/5").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.productEan").value("1234567890123"))
            .andExpect(jsonPath("$.productName").value("Product"))
            .andExpect(jsonPath("$.price").value(500))
            .andExpect(jsonPath("$.lowestPrice").value(400))
            .andExpect(jsonPath("$.imageUrl").value("http://image"))
            .andExpect(jsonPath("$.quantityLevel").value("AVAILABLE"));
    }

    @Test
    void detailsPropagatesAppValidationExceptionFromQry() throws Exception {
        when(getOfferDetailsQry.handle(5)).thenThrow(new AppValidationException("offerId", "Offer not found"));

        mockMvc.perform(get("/api/offer/5").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.errors[0].field").value("offerId"));
    }

    @Test
    void detailsReturnsNotFoundForNonNumericId() throws Exception {
        mockMvc.perform(get("/api/offer/abc"))
            .andExpect(status().isNotFound());

        verify(getOfferDetailsQry, never()).handle(any(Integer.class));
    }
}
