package com.example.Crypto.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_sources")
public class PriceSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source; // BINANCE / HUOBI

    private String pair;

    private Double askPrice;

    private Double bidPrice;

    private LocalDateTime timestamp;
}
