package com.example.Crypto.config;

import com.example.Crypto.entity.User;
import com.example.Crypto.entity.Wallet;
import com.example.Crypto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;


    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User user1 = new User();
            user1.setUsername("alice");
            user1.setEmail("alice@gmail.com");
            user1.setPassword("123456");

            Wallet wallet1 = new Wallet(null,"USDT", 50000.0, user1);
            user1.getWallets().add(wallet1);

            User user2 = new User();
            user2.setUsername("bob");
            user2.setEmail("bob@gmail.com");
            user2.setPassword("123456");

            Wallet wallet2 = new Wallet(null, "USDT",50000.0,  user2);
            user2.getWallets().add(wallet2);

            userRepository.saveAll(List.of(user1, user2));
        }
    }
}

