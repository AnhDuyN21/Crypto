package com.example.Crypto.service;

import com.example.Crypto.entity.CryptoPrice;

public interface PriceService {
    /**
     * Lấy giá aggregate mới nhất của cặp giao dịch.
     *
     * @param pair tên cặp giao dịch, ví dụ "ETHUSDT", "BTCUSDT"
     * @return đối tượng CryptoPrice mới nhất
     */
    CryptoPrice getLatestPrice(String pair);

    /**
     * Cập nhật giá mới (được aggregate từ các nguồn như Binance & Huobi)
     *
     * @param newPrice đối tượng CryptoPrice chứa thông tin giá
     * @return đối tượng CryptoPrice sau khi lưu vào DB
     */
    CryptoPrice updatePrice(CryptoPrice newPrice);
}
