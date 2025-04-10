package com.example.Crypto.repository;

import com.example.Crypto.entity.User;
import com.example.Crypto.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    List<Wallet> findAllByUserId(Long userId);
    Optional<Wallet> findByUserAndCurrency(User user, String currency);
}
