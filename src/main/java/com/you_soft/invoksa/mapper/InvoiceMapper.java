package com.you_soft.invoksa.mapper;

import com.you_soft.invoksa.dto.request.ClientRequest;
import com.you_soft.invoksa.dto.request.InvoiceRequest;
import com.you_soft.invoksa.dto.response.InvoiceItemResponse;
import com.you_soft.invoksa.dto.response.InvoiceResponse;
import com.you_soft.invoksa.entity.Client;
import com.you_soft.invoksa.entity.Invoice;
import com.you_soft.invoksa.entity.InvoiceItem;
import com.you_soft.invoksa.repository.ClientRepository;
import com.you_soft.invoksa.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class InvoiceMapper {

    private final ClientMapper clientMapper;
    private  final InvoiceItemMapper invoiceItemMaper;
    private final UserMapper userMapper;
    private final ClientRepository clientRepository;


    public InvoiceResponse toResponse(Invoice invoice) {
        List<InvoiceItemResponse> itemResponses = invoice.getItems()
                .stream()
                .map(invoiceItemMaper::toResponse) // méthode pour convertir InvoiceItem -> InvoiceItemResponse
                .toList();

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .client(clientMapper.toResponse(invoice.getClient())) // conversion Client -> ClientResponse
                //.user(userMapper.toResponse(invoice.getUser()))       // conversion User -> UserResponse
                .total(invoice.getTotal())
                .status(invoice.getStatus())
                .items(itemResponses)
                .build();
    }

    public Invoice toEntity(InvoiceRequest invoiceRequest) {
        List<InvoiceItem> items = invoiceRequest.getItems()
                .stream()
                .map(invoiceItemMaper::toEntity) // convertir InvoiceItemRequest -> InvoiceItem
                .toList();

        Client client = clientRepository.findById(invoiceRequest.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found id " + invoiceRequest.getClientId()));


        Invoice invoice = Invoice.builder()
                .id(invoiceRequest.getId())
                .client(client)
                //.user(userMapper.toEntity(invoiceRequest.getUser()))
                .total(invoiceRequest.getTotal())
                .status(invoiceRequest.getStatus())
                .items(items)
                .build();

        // On lie chaque item à la facture
        items.forEach(item -> item.setInvoice(invoice));

        return invoice;
    }


}