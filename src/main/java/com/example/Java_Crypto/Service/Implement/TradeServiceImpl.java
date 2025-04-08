package com.example.Java_Crypto.Service.Implement;

import com.example.Java_Crypto.Entity.CryptoPrice;
import com.example.Java_Crypto.Entity.Transaction;
import com.example.Java_Crypto.Entity.User;
import com.example.Java_Crypto.Entity.Wallet;
import com.example.Java_Crypto.Repository.TransactionRepository;
import com.example.Java_Crypto.Repository.UserRepository;
import com.example.Java_Crypto.Repository.WalletRepository;
import com.example.Java_Crypto.Service.PriceService;
import com.example.Java_Crypto.Service.TradeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TradeServiceImpl implements TradeService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final PriceService priceService;

    @Override
    @Transactional
    public Transaction executeTrade(Long userId, String pair, String tradeType, double amount) {
        // Lấy user từ DB
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Lấy giá mới nhất cho pair
        CryptoPrice cryptoPrice = priceService.getLatestPrice(pair);
        if (cryptoPrice == null) {
            throw new RuntimeException("Price not available for pair: " + pair);
        }
        // Xác định giá giao dịch
        double transactionPrice = tradeType.equalsIgnoreCase("BUY")
                ? cryptoPrice.getBestAskPrice() : cryptoPrice.getBestBidPrice();
        double total = transactionPrice * amount;

        // Xử lý ví: nếu BUY trừ USDT, nếu SELL trừ coin (vd: ETH hoặc BTC)
        String cryptoCurrency = pair.replace("USDT", "");
        Wallet usdtWallet = walletRepository.findByUserAndCurrency(user, "USDT")
                .orElseThrow(() -> new RuntimeException("USDT wallet not found"));
        Wallet cryptoWallet = walletRepository.findByUserAndCurrency(user, cryptoCurrency)
                .orElseThrow(() -> new RuntimeException(cryptoCurrency + " wallet not found"));

        if (tradeType.equalsIgnoreCase("BUY")) {
            if (usdtWallet.getBalance() < total) {
                throw new RuntimeException("Insufficient USDT balance");
            }
            usdtWallet.setBalance(usdtWallet.getBalance() - total);
            cryptoWallet.setBalance(cryptoWallet.getBalance() + amount);
        } else if (tradeType.equalsIgnoreCase("SELL")) {
            if (cryptoWallet.getBalance() < amount) {
                throw new RuntimeException("Insufficient " + cryptoCurrency + " balance");
            }
            cryptoWallet.setBalance(cryptoWallet.getBalance() - amount);
            usdtWallet.setBalance(usdtWallet.getBalance() + total);
        } else {
            throw new IllegalArgumentException("Trade type must be either BUY or SELL");
        }

        // Cập nhật lại ví
        walletRepository.save(usdtWallet);
        walletRepository.save(cryptoWallet);

        // Lưu giao dịch
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
