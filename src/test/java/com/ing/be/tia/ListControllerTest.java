package com.ing.be.tia;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import com.ing.be.tia.controller.ListController;
import com.ing.be.tia.model.BulkStatus;
import com.ing.be.tia.service.ListService;

@WebMvcTest(ListController.class)
public class ListControllerTest {
	@Autowired
    private MockMvc mockMvc;

    @MockBean
    private ListService listService;

    @Test
    public void testBulkUpload_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "listings-1.json", MediaType.APPLICATION_JSON_VALUE, "[{\"title\":\"Test Listing\", \"description\":\"Test Description\"}]".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/list/bulk").file(file))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$._links.status.href").exists());
    }

    @Test
    public void testBulkUpload_emptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "", MediaType.APPLICATION_JSON_VALUE, "".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/list/bulk").file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetStatus_success() throws Exception {
        String id = "test-id";
        when(listService.getStatus(anyString())).thenReturn(BulkStatus.Status.DONE);

        mockMvc.perform(MockMvcRequestBuilders.get("/listing/bulk/status/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("DONE"));
    }

    @Test
    public void testGetStatus_notFound() throws Exception {
        String id = "test-id";
        when(listService.getStatus(anyString())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/listing/bulk/status/" + id))
                .andExpect(status().isNotFound());
    }
}
