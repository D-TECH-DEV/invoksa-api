package com.you_soft.invoksa.utils;

import com.you_soft.invoksa.entity.Client;
import com.you_soft.invoksa.entity.User;
import com.you_soft.invoksa.repository.InvoiceRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class MatriculeUtils {

    private final InvoiceRepository repository;

    @Transactional
    public synchronized String generate(Client client) {
        User user = client.getUser();
        int year = LocalDate.now().getYear();
        LocalDateTime start = LocalDateTime.of(year,1,1,0,0);
        LocalDateTime end = LocalDateTime.of(year,12,31,23,59);
        long count = repository.countByUserAndYear(user, start, end);
        long next = count + 1;
        return String.format("INV-%d-%04d", year, next);
    }
    }