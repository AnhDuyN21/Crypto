package com.example.Crypto.service;

import com.example.Crypto.entity.Transaction;

public interface TradeService {
    /**
     * Thực hiện giao dịch BUY hoặc SELL của một user.
     *
     * @param userId    id của người dùng
     * @param pair      cặp giao dịch (ví dụ: "ETHUSDT", "BTCUSDT")
     * @param tradeType kiểu giao dịch ("BUY" hoặc "SELL")
     * @param amount    số lượng giao dịch
     * @return giao dịch đã được lưu
     */
    Transaction executeTrade(Long userId, String pair, String tradeType, double amount);
}
