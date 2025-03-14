package com.ing.be.tia;

import com.ing.be.tia.controller.ListController;
import com.ing.be.tia.model.BulkStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ing.be.tia.model.Lists;
import com.ing.be.tia.repository.ListRepository;
import com.ing.be.tia.service.ListService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
@WebMvcTest(ListService.class)
public class ListServiceTest {

    @Mock
    private ListRepository listRepository;

    @InjectMocks
    private ListService listService;

    private String testId;
    private MockMultipartFile testFile;

    @BeforeEach
    void setUp() {
        testId = "testid";
        testFile = new MockMultipartFile("file", "listings-1.json", "application/json", "[{\"title\":\"Test Listing\", \"description\":\"Test Description\"}]".getBytes());
    }

    @Test
    void processBulkUpload_success() throws IOException {
        List<Lists> listings = new ObjectMapper().readValue(testFile.getInputStream(), new ObjectMapper().getTypeFactory().constructCollectionType(List.class, Lists.class));
        when(listRepository.saveAll(listings)).thenReturn(listings);

        listService.processBulkUpload(testId, testFile);

        assertEquals(BulkStatus.Status.DONE, listService.getStatus(testId));
        verify(listRepository, times(1)).saveAll(listings);
    }

    @Test
    void processBulkUpload_error() {
        MockMultipartFile invalidFile = new MockMultipartFile("file", "invalid.txt", "text/plain", "invalid content".getBytes());

        listService.processBulkUpload(testId, invalidFile);

        assertEquals(BulkStatus.Status.ERROR, listService.getStatus(testId));
        verify(listRepository, never()).saveAll(anyList());
    }

    @Test
    void getStatus_existingId() {
    	listService.processBulkUpload(testId, testFile);
        assertEquals(BulkStatus.Status.IN_PROGRESS, listService.getStatus(testId));
    }

    @Test
    void getStatus_nonExistingId() {
        assertEquals(null, listService.getStatus("nonexisting"));
    }

}
