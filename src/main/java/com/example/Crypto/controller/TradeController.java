package com.example.Crypto.controller;

import com.example.Crypto.entity.Transaction;
import com.example.Crypto.service.TradeService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/trade")
@AllArgsConstructor
public class TradeController {
    private final TradeService tradeService;

    /**
     * Thực hiện giao dịch BUY/SELL.
     * Ví dụ: POST /api/trade/1?pair=ETHUSDT&tradeType=BUY&amount=2.5
     *
     * @param userId    ID của user thực hiện giao dịch
     * @param pair      Cặp giao dịch (ví dụ "ETHUSDT", "BTCUSDT")
     * @param tradeType Kiểu giao dịch ("BUY" hoặc "SELL")
     * @param amount    Số lượng giao dịch
     */
    @PostMapping("/{userId}")
    public ResponseEntity<Transaction> executeTrade(
            @PathVariable Long userId,
            @RequestParam String pair,
            @RequestParam String tradeType,
            @RequestParam BigDecimal amount) {
        try {
            Transaction transaction = tradeService.executeTrade(userId, pair, tradeType, amount);
            return ResponseEntity.ok(transaction);
        } catch (Exception ex) {
            // Trong thực tế, bạn nên trả về thông báo lỗi chi tiết hơn
            return ResponseEntity.badRequest().body(null);
        }
    }
}
