package com.example.Crypto.service.implement;

import com.example.Crypto.entity.*;
import com.example.Crypto.repository.WalletRepository;
import com.example.Crypto.service.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class WalletServiceImpl implements WalletService {


    private final WalletRepository walletRepository;

    @Override
    public List<Wallet> getUserWallets(Long userId) {
        // Giả sử rằng WalletRepository có method findAllByUser(User user)
        // Trong trường hợp cần, bạn có thể lấy user từ UserRepository trước.
        // Ở đây, mình cho rằng repository đã được thiết kế đúng.
        // Nếu cần thiết, bạn có thể thay đổi phương thức để nhận User thay vì userId.
        // Ví dụ:
        //    return walletRepository.findAllByUserId(userId);
        return walletRepository.findAllByUserId(userId);
    }

    @Override
    public Optional<Wallet> findByUserAndCurrency(User user, String currency) {
        return walletRepository.findByUserAndCurrency(user, currency);
    }

    @Override
    public Wallet saveWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }
}
