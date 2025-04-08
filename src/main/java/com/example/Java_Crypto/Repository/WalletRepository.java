package com.example.Java_Crypto.Repository;

import com.example.Java_Crypto.Entity.User;
import com.example.Java_Crypto.Entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserAndCurrency(User user, String currency);
}
