package com.you_soft.invoksa.controller;

import com.you_soft.invoksa.dto.request.ClientRequest;
import com.you_soft.invoksa.dto.response.ClientResponse;
import com.you_soft.invoksa.dto.response.InvoiceResponse;
import com.you_soft.invoksa.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponse> create(@RequestBody ClientRequest request) {
        return ResponseEntity.ok(clientService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> getAll() {
        return ResponseEntity.ok(clientService.getAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ClientResponse>> getAllByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(clientService.getByUser(userId));
    }

    @GetMapping("/me")
    public ResponseEntity<List<ClientResponse>> getMyClients() {
        return ResponseEntity.ok(clientService.getMyClients());
    }

    
    

//    @GetMapping("/{id}")
//    public ResponseEntity<ClientResponse> getById(@PathVariable Long id) {
//        return ResponseEntity.ok(clientService.getById(id));
//    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> update(@PathVariable Long id, @RequestBody ClientRequest request) {
        return ResponseEntity.ok(clientService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}