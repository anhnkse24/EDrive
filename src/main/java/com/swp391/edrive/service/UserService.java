package com.swp391.edrive.service;

import com.swp391.edrive.entity.Dealer;
import com.swp391.edrive.dto.request.RegisterRequest;
import com.swp391.edrive.entity.User;
import com.swp391.edrive.enums.UserRole;
import com.swp391.edrive.repository.DealerRepository;
import com.swp391.edrive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private DealerRepository dealerRepository;

    public User createUser(RegisterRequest request)throws Exception  {
        // Check confirmPassword
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new Exception("Mật khẩu xác nhận không khớp");
        }
        // Check email trùng
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new Exception("Email đã được sử dụng");
        }

        // Check phone trùng
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new Exception("Số điện thoại đã được sử dụng");
        }

        // Encode password
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Map request -> entity User
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(encodedPassword);
        // Gán role mặc định (ví dụ CUSTOMER)
        user.setRole(UserRole.DEALER_STAFF);

        // Nếu request có dealerName thì tìm Dealer theo tên
        if (request.getDealerName() != null && !request.getDealerName().isEmpty()) {
            Dealer dealer = dealerRepository.findByDealerName(request.getDealerName())
                    .orElseThrow(() -> new Exception("Dealer không tồn tại với tên: " + request.getDealerName()));
            user.setDealer(dealer);
        }

        return userRepository.save(user);
    }
}
