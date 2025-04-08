package com.example.Java_Crypto.Service.Implement;

import com.example.Java_Crypto.Entity.CryptoPrice;
import com.example.Java_Crypto.Repository.CryptoPriceRepository;
import com.example.Java_Crypto.Service.PriceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PriceServiceImpl implements PriceService {

    private final CryptoPriceRepository cryptoPriceRepository;

    @Override
    public CryptoPrice getLatestPrice(String pair) {
        return cryptoPriceRepository.findTopByPairOrderByUpdatedAtDesc(pair);
    }

    @Override
    public CryptoPrice updatePrice(CryptoPrice newPrice) {
        return cryptoPriceRepository.save(newPrice);
    }
}
