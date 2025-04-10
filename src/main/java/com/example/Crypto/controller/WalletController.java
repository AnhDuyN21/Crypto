package com.example.Crypto.controller;

import com.example.Crypto.entity.Wallet;
import com.example.Crypto.service.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@AllArgsConstructor
public class WalletController {


    private final WalletService walletService;

    /**
     * Lấy danh sách ví của user theo userId.
     * Ví dụ: GET /api/wallet/1
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<Wallet>> getUserWallets(@PathVariable Long userId) {
        List<Wallet> wallets = walletService.getUserWallets(userId);
        return ResponseEntity.ok(wallets);
    }
}
