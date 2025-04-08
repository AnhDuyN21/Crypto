package com.example.Java_Crypto.Service;

import com.example.Java_Crypto.Entity.Transaction;

public interface TradeService {
    Transaction executeTrade(Long userId, String pair, String tradeType, double amount);
}
