package com.you_soft.invoksa.service;

import com.you_soft.invoksa.config.JwtUtils;
import com.you_soft.invoksa.dto.request.ClientRequest;
import com.you_soft.invoksa.dto.response.ClientResponse;
import com.you_soft.invoksa.dto.response.InvoiceResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.you_soft.invoksa.entity.Client;
import com.you_soft.invoksa.entity.Invoice;
import com.you_soft.invoksa.entity.User;
import com.you_soft.invoksa.mapper.ClientMapper;
import com.you_soft.invoksa.mapper.UserMapper;
import com.you_soft.invoksa.repository.ClientRepository;
import com.you_soft.invoksa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final UserRepository userRepository;
    private UserMapper userMapper;
    private final JwtUtils jwtUtils;

    public ClientResponse create(ClientRequest clientRequest) {
       // User user = userRepository.findById(clientRequest.getUserId())
         //       .orElseThrow(() -> new RuntimeException("User not found"));
        User user = jwtUtils.getConnectedUser();
        Client client = clientMapper.toEntity(clientRequest);
        client.setUser(user);
        Client clientSaved = clientRepository.save(client);
        return clientMapper.toResponse(clientSaved);
    }

    public List<ClientResponse> getAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<ClientResponse> getMyClients() {
        User user = jwtUtils.getConnectedUser();
        if (user == null) {
           throw  new RuntimeException("User not found");
        }
        return clientRepository.findAllByUserId(user.getId())
                .stream()
                .map(clientMapper::toResponse)
                .toList();
    }

    public List<ClientResponse> getByUser(Long userId) {
        List<Client> clients = clientRepository.findAllByUserId(userId);
        if (clients.isEmpty()) {
            throw new RuntimeException("No clients found for this user");
        }
        return clients.stream()
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