package com.example.Crypto.repository;

import com.example.Crypto.entity.CryptoPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptoPriceRepository extends JpaRepository<CryptoPrice, Long> {
    CryptoPrice findTopByPairOrderByUpdatedAtDesc(String pair);
}
