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
        // Check username trùng
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new Exception("Username đã được sử dụng");
        }

        // Check phone trùng
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new Exception("Số điện thoại đã được sử dụng");
        }

        // Encode password
        String encodedPassword = passwordEncoder.encode(request.getPassword());



        // Kiểm tra dealerName trùng
        dealerRepository.findByDealerName(request.getDealerName())
                .ifPresent(d -> {
                    throw new RuntimeException("Dealer name đã được sử dụng");
                });


        Dealer dealer = new Dealer();
        dealer.setDealerName(request.getDealerName());
        dealer.setAddress(request.getAddress());
        dealer.setContactPerson(request.getFullName());
        dealer.setPhone(request.getPhone());
        dealerRepository.save(dealer);
        // Map request -> entity User
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(encodedPassword);
        // Gán role mặc định (ví dụ CUSTOMER)
        user.setRole(UserRole.DEALER_STAFF);
        user.setDealer(dealer);

        return userRepository.save(user);
    }
}
