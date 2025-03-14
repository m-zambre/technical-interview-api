package com.ing.be.tia.service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ing.be.tia.model.BulkStatus;
import com.ing.be.tia.repository.ListRepository;
import com.ing.be.tia.model.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
 

@Service
public class ListService {
	
	private final ListRepository listRepository;
    private final Map<String, BulkStatus> statusMap = new HashMap<>();

    @Autowired
    public ListService(ListRepository listRepository) {
        this.listRepository = listRepository;
    }

    @Async
    public void processBulkUpload(String id, MultipartFile file) {
        statusMap.put(id, new BulkStatus(BulkStatus.Status.IN_PROGRESS));
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Lists> lists = objectMapper.readValue(file.getInputStream(), objectMapper.getTypeFactory().constructCollectionType(List.class, Lists.class));
            listRepository.save(lists);
            statusMap.put(id, new BulkStatus(BulkStatus.Status.DONE));
        } catch (IOException e) {
            statusMap.put(id, new BulkStatus(BulkStatus.Status.ERROR));
            e.printStackTrace();
        }
    }

    public BulkStatus.Status getStatus(String id) {
        BulkStatus bulkStatus = statusMap.get(id);
        if(bulkStatus != null) {
            return bulkStatus.getStatus();
        }
        return null;
    }


}
