package com.you_soft.invoksa.repository;

import com.you_soft.invoksa.entity.Client;
import com.you_soft.invoksa.entity.Invoice;
import com.you_soft.invoksa.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findAllByClientIn(List<Client> clients, Sort sort);
    List<Invoice> findAllByClientId(Long clientId, Sort sort);

    Invoice findByToken(String token);

    List<Invoice> findAllByClientInOrderByCreatedAtDesc(List<Client> clients);


    @Query("""
        SELECT COUNT(i)
        FROM Invoice i
        WHERE i.client.user = :user
        AND i.createdAt BETWEEN :start AND :end
    """)
    long countByUserAndYear(
            @Param("user") User user,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}