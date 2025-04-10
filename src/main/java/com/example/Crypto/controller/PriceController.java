package com.example.Crypto.controller;

import com.example.Crypto.entity.CryptoPrice;
import com.example.Crypto.service.PriceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prices")
@AllArgsConstructor
public class PriceController {

    private final PriceService priceService;

    /**
     * Lấy giá aggregate mới nhất cho cặp giao dịch
     * Ví dụ: GET /api/prices/ETHUSDT
     */
    @GetMapping("/{pair}")
    public ResponseEntity<CryptoPrice> getLatestPrice(@PathVariable String pair) {
        CryptoPrice cryptoPrice = priceService.getLatestPrice(pair);
        if (cryptoPrice == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cryptoPrice);
    }
}
