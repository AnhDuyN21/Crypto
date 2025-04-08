package com.example.Java_Crypto.Repository;

import com.example.Java_Crypto.Entity.CryptoPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptoPriceRepository extends JpaRepository<CryptoPrice, Long> {
    CryptoPrice findTopByPairOrderByUpdatedAtDesc(String pair);
}
