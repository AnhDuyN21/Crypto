package com.example.Crypto.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "crypto_price")
@Getter
@Setter
public class CryptoPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pair; // ETHUSDT / BTCUSDT

    @Column(name = "best_ask_price")
    private Double bestAskPrice;

    @Column(name = "best_bid_price")
    private Double bestBidPrice;

    private LocalDateTime updatedAt;
}
