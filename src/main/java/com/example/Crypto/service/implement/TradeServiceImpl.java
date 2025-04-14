package com.example.Crypto.service.implement;

import com.example.Crypto.entity.*;
import com.example.Crypto.repository.*;
import com.example.Crypto.service.PriceService;
import com.example.Crypto.service.TradeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final PriceService priceService;

    @Override
    @Transactional  // Đảm bảo nếu một bước thất bại, toàn bộ giao dịch được rollback
    public Transaction executeTrade(Long userId, String pair, String tradeType, BigDecimal amount) {
        // Bước 1: Lấy thông tin người dùng
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Bước 2: Lấy giá aggregate mới nhất dựa trên cặp giao dịch
        CryptoPrice cryptoPrice = priceService.getLatestPrice(pair);
        if (cryptoPrice == null) {
            throw new RuntimeException("Price data not available for pair: " + pair);
        }

        // Xác định giá giao dịch: dùng bestAsk cho BUY, bestBid cho SELL.
        BigDecimal transactionPrice = tradeType.equalsIgnoreCase("BUY")
                ? cryptoPrice.getBestAskPrice()
                : cryptoPrice.getBestBidPrice();
        BigDecimal total = transactionPrice.multiply(amount);

        // Bước 3: Kiểm tra và cập nhật số dư ví
        String cryptoCurrency = pair.replace("USDT", "");  // Lấy mã coin từ cặp (vd: "ETH" từ "ETHUSDT")
        Wallet usdtWallet = walletRepository.findByUserAndCurrency(user, "USDT")
                .orElseThrow(() -> new RuntimeException("USDT wallet not found"));
        Wallet cryptoWallet = walletRepository.findByUserAndCurrency(user, cryptoCurrency)
                .orElseThrow(() -> new RuntimeException(cryptoCurrency + " wallet not found"));

        // Nếu giao dịch là BUY: trừ USDT, cộng coin vào ví
        // Nếu giao dịch là SELL: trừ coin, cộng USDT vào ví
        if (tradeType.equalsIgnoreCase("BUY")) {
            if (usdtWallet.getBalance().compareTo(total) < 0) {
                throw new RuntimeException("Insufficient USDT balance");
            }
            usdtWallet.setBalance(usdtWallet.getBalance().subtract(total));
            cryptoWallet.setBalance(cryptoWallet.getBalance().add(total));
        } else if (tradeType.equalsIgnoreCase("SELL")) {
            if (cryptoWallet.getBalance().compareTo(total) < 0) {
                throw new RuntimeException("Insufficient " + cryptoCurrency + " balance");
            }
            cryptoWallet.setBalance(cryptoWallet.getBalance().subtract(total));
            usdtWallet.setBalance(usdtWallet.getBalance().add(amount));
        } else {
            throw new IllegalArgumentException("Trade type must be either BUY or SELL");
        }

        // Lưu thay đổi vào database
        walletRepository.save(usdtWallet);
        walletRepository.save(cryptoWallet);

        // Bước 4: Ghi nhận giao dịch vào lịch sử
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setPair(pair);
        transaction.setType(tradeType.toUpperCase());
        transaction.setPrice(transactionPrice);
        transaction.setAmount(amount);
        transaction.setTotal(total);
        transaction.setTimestamp(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }
}
