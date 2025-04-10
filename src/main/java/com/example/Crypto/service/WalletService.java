package com.example.Crypto.service;

import com.example.Crypto.entity.User;
import com.example.Crypto.entity.Wallet;
import java.util.List;
import java.util.Optional;

public interface WalletService {
    /**
     * Trả về danh sách ví của một user.
     */
    List<Wallet> getUserWallets(Long userId);

    /**
     * Lấy ví của user theo loại tiền (currency), trả về Optional.
     */
    Optional<Wallet> findByUserAndCurrency(User user, String currency);

    /**
     * Cập nhật (lưu) ví.
     */
    Wallet saveWallet(Wallet wallet);
}
