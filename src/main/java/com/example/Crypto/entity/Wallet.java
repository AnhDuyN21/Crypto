package com.example.Crypto.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "wallets")
@Getter
@Setter
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String currency; // BTC / ETH / USDT

    private Double balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
