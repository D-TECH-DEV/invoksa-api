package com.you_soft.invoksa.service;

import com.you_soft.invoksa.dto.request.ClientRequest;
import com.you_soft.invoksa.dto.response.ClientResponse;
import com.you_soft.invoksa.entity.Client;
import com.you_soft.invoksa.mapper.ClientMapper;
import com.you_soft.invoksa.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;


    public ClientResponse create(ClientRequest clientRequest) {
        Client client = clientMapper.toEntity(clientRequest);
        Client clientSaved = clientRepository.save(client);
        return clientMapper.toResponse(clientSaved);
    }


    public List<ClientResponse> getAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toResponse)
                .collect(Collectors.toList());
    }


    public ClientResponse getById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        return clientMapper.toResponse(client);
    }


    public ClientResponse update(Long id, ClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        client.setName(request.getName());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());
        Client clientUpdated = clientRepository.save(client);
        return clientMapper.toResponse(clientUpdated);
    }


    public void delete(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Client not found");
        }
        clientRepository.deleteById(id);
    }

}