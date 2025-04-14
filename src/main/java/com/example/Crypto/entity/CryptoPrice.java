package com.example.Crypto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "crypto_price")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CryptoPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pair; // ETHUSDT / BTCUSDT

    @Column(name = "best_ask_price")
    private BigDecimal bestAskPrice;

    @Column(name = "best_bid_price")
    private BigDecimal  bestBidPrice;

    private LocalDateTime updatedAt;
}
