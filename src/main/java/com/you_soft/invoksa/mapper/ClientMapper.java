package com.you_soft.invoksa.mapper;

import com.you_soft.invoksa.dto.request.ClientRequest;
import com.you_soft.invoksa.dto.response.ClientResponse;
import com.you_soft.invoksa.entity.Client;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
 @Component
public class ClientMapper {


    public ClientResponse toResponse(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .name(client.getName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .build();
    }


    public Client toEntity(ClientRequest clientRequest) {
       return Client.builder()
                .name(clientRequest.getName())
                .email(clientRequest.getEmail())
                .phone(clientRequest.getPhone())
                .build();
    }
}