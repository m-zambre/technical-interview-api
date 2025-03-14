package com.ing.be.tia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ing.be.tia.model.BulkStatus.Status;
import com.ing.be.tia.service.ListService;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/listing")
public class ListController {
	
    private final ListService listService;

    @Autowired
    public ListController(ListService listService) {
        this.listService = listService;
    }

    @PostMapping("/bulk")
    public ResponseEntity<EntityModel<String>> uploadFile(@RequestParam("file") MultipartFile file) {
    	 if (file.isEmpty()) {
             return ResponseEntity.badRequest().body(EntityModel.of("File is empty"));
         }

         String id = UUID.randomUUID().toString();
         listService.processBulkUpload(id, file);

         Link statusLink = linkTo(methodOn(ListController.class).getStatus(id)).withRel("status");
         EntityModel<String> response = EntityModel.of(id);
         response.add(statusLink);

         return ResponseEntity.accepted().body(response);

    }

    @GetMapping("/status/{id}")
    public ResponseEntity<Map<String, Object>> getStatus(@PathVariable String id) {
        Status status = listService.getStatus(id);
        return ResponseEntity.ok(Map.of("id", id, "status", status));
    }

}
