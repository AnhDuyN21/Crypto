package com.example.Crypto.service.implement;

import com.example.Crypto.entity.CryptoPrice;
import com.example.Crypto.repository.CryptoPriceRepository;
import com.example.Crypto.service.PriceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PriceServiceImpl implements PriceService {


    private final CryptoPriceRepository cryptoPriceRepository;

    @Override
    public CryptoPrice getLatestPrice(String pair) {
        // Lấy đối tượng CryptoPrice mới nhất từ DB theo pair
        return cryptoPriceRepository.findTopByPairOrderByUpdatedAtDesc(pair);
    }

    @Override
    public CryptoPrice updatePrice(CryptoPrice newPrice) {
        // Lưu đối tượng CryptoPrice mới và trả về đối tượng sau khi lưu
        return cryptoPriceRepository.save(newPrice);
    }
}
