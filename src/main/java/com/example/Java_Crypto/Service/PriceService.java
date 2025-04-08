package com.example.Java_Crypto.Service;

import com.example.Java_Crypto.Entity.CryptoPrice;

public interface PriceService {
    CryptoPrice getLatestPrice(String pair);
    CryptoPrice updatePrice(CryptoPrice newPrice);
}
