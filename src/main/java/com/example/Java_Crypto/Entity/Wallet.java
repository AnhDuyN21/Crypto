package com.example.Java_Crypto.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "wallets")
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

