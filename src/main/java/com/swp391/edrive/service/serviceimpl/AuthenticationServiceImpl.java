package com.swp391.edrive.service.serviceimpl;


import com.swp391.edrive.constant.PredefinedRole;
import com.swp391.edrive.dto.request.LoginRequest;
import com.swp391.edrive.dto.request.UserRegistrationRequest;
import com.swp391.edrive.dto.response.UnverifiedUserResponse;
import com.swp391.edrive.dto.response.UserResponse;
import com.swp391.edrive.entity.*;
import com.swp391.edrive.exception.exceptions.BadRequestException;
import com.swp391.edrive.exception.exceptions.ConflictException;
import com.swp391.edrive.mapper.UserMapper;
import com.swp391.edrive.repository.DealerRepository;
import com.swp391.edrive.repository.PasswordResetTokenRepository;
import com.swp391.edrive.repository.RoleRepository;
import com.swp391.edrive.repository.UserRepository;
import com.swp391.edrive.repository.VerificationTokenRepository;
import com.swp391.edrive.service.AuthenticationService;
import com.swp391.edrive.service.EmailService;
import com.swp391.edrive.service.MailService;
import com.swp391.edrive.service.RefreshTokenService;
import com.swp391.edrive.service.TokenService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j

public class AuthenticationServiceImpl implements AuthenticationService {

    // Constants
    private static final String ADMIN_EMAIL = "cuongcaoleanh@gmail.com";

    @Value("${frontend.url.email.verification}")
    private String emailVerificationUrl;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DealerRepository dealerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    MailService mailService;

    @Autowired
    RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }

    @Override
    @Transactional
    public User register(UserRegistrationRequest request, org.springframework.web.multipart.MultipartFile businessLicense) {
        // Validate unique constraints
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getDealerEmail())) {
            throw new ConflictException("Email already exists");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new ConflictException("Phone number already exists");
        }

        if (dealerRepository.existsByDealerName(request.getDealerName())) {
            throw new ConflictException("Dealer name already exists");
        }

        // Validate and upload business license file
        String businessLicenseUrl = null;
        if (businessLicense != null && !businessLicense.isEmpty()) {
            try {
                String originalFileName = businessLicense.getOriginalFilename();
                if (originalFileName == null || originalFileName.isEmpty()) {
                    throw new BadRequestException("Tên file giấy phép kinh doanh không hợp lệ");
                }

                // Validate file extension
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
                if (!fileExtension.matches("\\.(pdf|jpg|jpeg|png)$")) {
                    throw new BadRequestException("Chỉ chấp nhận file PDF, JPG, JPEG hoặc PNG");
                }

                // Create unique filename
                String uniqueFileName = "business_license_" + UUID.randomUUID() + fileExtension;

                // Define upload directory
                String userHome = System.getProperty("user.home");
                String uploadDirPath = userHome + java.io.File.separator + "edrive_uploads" + java.io.File.separator + "licenses";

                java.io.File uploadDirFile = new java.io.File(uploadDirPath);
                if (!uploadDirFile.exists()) {
                    boolean created = uploadDirFile.mkdirs();
                    if (!created) {
                        throw new RuntimeException("Không thể tạo thư mục upload: " + uploadDirPath);
                    }
                }

                // Save file
                java.io.File uploadFile = new java.io.File(uploadDirFile, uniqueFileName);
                businessLicense.transferTo(uploadFile);
                businessLicenseUrl = uploadFile.getAbsolutePath();

                log.info("Business license uploaded successfully: {}", businessLicenseUrl);
            } catch (java.io.IOException e) {
                throw new RuntimeException("Lỗi khi tải lên giấy phép kinh doanh: " + e.getMessage(), e);
            }
        }

        // Create Dealer entity
        Dealer dealer = new Dealer();
        dealer.setDealerName(request.getDealerName());
        dealer.setDealerEmail(request.getDealerEmail());
        dealer.setHouseNumberAndStreet(request.getHouseNumberAndStreet());
        dealer.setWardOrCommune(request.getWardOrCommune());
        dealer.setDistrict(request.getDistrict());
        dealer.setProvinceOrCity(request.getProvinceOrCity());
        dealer.setContactPerson(request.getFullName());
        dealer.setPhone(request.getPhone());
        dealer.setBusinessLicenseUrl(businessLicenseUrl);

        Dealer savedDealer = dealerRepository.save(dealer);

        // Create User entity
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getDealerEmail())
                .phone(request.getPhone())
                .fullName(request.getFullName())
                .dealer(savedDealer)
                .isVerify(false) // Account not verified until admin approves
                .build();

        // Assign DEALER_MANAGER role
        Set<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.DEALER_MANAGER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        // Generate verification token for admin approval
        String token = UUID.randomUUID().toString();
        createVerificationToken(savedUser, token);

        // Send email to admin for approval
        String fullAddress = String.format("%s, %s, %s, %s",
            request.getHouseNumberAndStreet(),
            request.getWardOrCommune(),
            request.getDistrict(),
            request.getProvinceOrCity()
        );

        mailService.sendDealerApprovalRequestToAdmin(
            ADMIN_EMAIL,
            request.getDealerName(),
            request.getFullName(),
            request.getDealerEmail(),
            request.getPhone(),
            fullAddress,
            token
        );

        log.info("Dealer registration request sent to admin for: {}", request.getDealerName());

        return savedUser;
    }



    private void createVerificationToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(token, user);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public void verifyAccount(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            throw new BadRequestException("Token không hợp lệ");
        }

        if (verificationToken.isExpired()) {
            throw new BadRequestException("Token đã hết hạn");
        }

        User user = verificationToken.getUser();
        user.setVerify(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);
    }

    @Override
    @Transactional
    public void verifyDealerAccount(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            throw new BadRequestException("Token không hợp lệ");
        }

        if (verificationToken.isExpired()) {
            throw new BadRequestException("Token đã hết hạn");
        }

        User user = verificationToken.getUser();
        user.setVerify(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);
        
        log.info("Dealer account verified successfully for user: {}", user.getUsername());
    }



    @Override
    public UserResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            User user = userRepository
                    .findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (!user.isVerify()) {
                throw new DisabledException("Account not verified. Please check your email.");
            }

        } catch (BadCredentialsException e) {
            // Fixed: Preserve stack trace
            throw new BadRequestException("Username/ password is invalid. Please try again!", e);
        } catch (LockedException e) {
            // Fixed: Preserve stack trace
            throw new BadRequestException("Account has been locked!", e);
        } catch (Exception e) {
            // Fixed: Preserve stack trace
            throw new BadRequestException("Login failed: " + e.getMessage(), e);
        }

        User user = userRepository
                .findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found after authentication"));

        // Tạo authentication với authorities từ permissions
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        String token = tokenService.generateToken(user);

        return UserMapper.toResponse(user, token, refreshToken.getToken());
    }

    private Date calculateExpiryDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 3600);
        return new Date(cal.getTime().getTime());
    }

    @Override
    public User validatePasswordResetToken(String token) {
        PasswordResetToken passToken = passwordResetTokenRepository.findByToken(token);
        if (passToken.getExpiryDate().before(new Date())) {
            throw new IllegalArgumentException("Token expired");
        }
        return passToken.getUser();
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void deleteResetToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        passwordResetTokenRepository.delete(resetToken);
    }




    @Override
    public void createPasswordResetTokenForAccount(User user, String token) {
        // Xóa tất cả token cũ trước khi tạo mới (đảm bảo chỉ token mới nhất có hiệu lực)
        deleteAllResetTokensByUser(user);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(calculateExpiryDate());
        passwordResetTokenRepository.save(resetToken);
    }

    @Override
    public void deleteAllResetTokensByUser(User user) {
        passwordResetTokenRepository.deleteByUser(user);
    }

    // Thêm phương thức mới để xử lý reset password qua token
    @Override
    public void resetPasswordWithToken(String token, String newPassword) {
        User user = validatePasswordResetToken(token);
        changePassword(user, newPassword);
        deleteResetToken(token);
    }

    @Override
    public void changeUserPassword(String oldPassword, String newPassword) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Verify old password matches
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        // Fixed: Use efficient blank string check
        if (isBlankString(newPassword)) {
            throw new BadRequestException("New password cannot be empty");
        }

        if (newPassword.equals(oldPassword)) {
            throw new BadRequestException("New password must be different from old password");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


    private boolean isBlankString(String str) {
        return str == null || str.isBlank();
    }

    @Override
    public List<UnverifiedUserResponse> getAllUnverifiedAccounts() {
        List<User> unverifiedUsers = userRepository.findByIsVerify(false);
        
        return unverifiedUsers.stream()
                .map(user -> {
                    String dealerAddress = "";
                    String dealerName = "";
                    String businessLicenseUrl = "";
                    Long dealerId = null;

                    if (user.getDealer() != null) {
                        Dealer dealer = user.getDealer();
                        dealerId = dealer.getDealerId();
                        dealerName = dealer.getDealerName();
                        businessLicenseUrl = dealer.getBusinessLicenseUrl();
                        dealerAddress = String.format("%s, %s, %s, %s",
                                dealer.getHouseNumberAndStreet() != null ? dealer.getHouseNumberAndStreet() : "",
                                dealer.getWardOrCommune() != null ? dealer.getWardOrCommune() : "",
                                dealer.getDistrict() != null ? dealer.getDistrict() : "",
                                dealer.getProvinceOrCity() != null ? dealer.getProvinceOrCity() : ""
                        ).replaceAll("^[, ]+|[, ]+$", ""); // Remove leading/trailing commas
                    }
                    
                    return UnverifiedUserResponse.builder()
                            .userId(user.getUserId())
                            .dealerId(dealerId)
                            .username(user.getUsername())
                            .fullName(user.getFullName())
                            .email(user.getEmail())
                            .phone(user.getPhone())
                            .dealerName(dealerName)
                            .dealerAddress(dealerAddress)
                            .businessLicenseUrl(businessLicenseUrl)
                            .isVerified(user.isVerify())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void verifyAccountByDealerId(Long dealerId) {
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy đại lý với ID: " + dealerId));

        User user = userRepository.findByDealer(dealer)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy tài khoản cho đại lý ID: " + dealerId));

        if (user.isVerify()) {
            throw new BadRequestException("Tài khoản đã được xác nhận");
        }
        
        // Set account as verified
        user.setVerify(true);
        userRepository.save(user);
        
        // Delete verification token if exists
        VerificationToken verificationToken = verificationTokenRepository.findByUser(user);
        if (verificationToken != null) {
            verificationTokenRepository.delete(verificationToken);
        }
        
        // Send confirmation email to the user
        String emailSubject = "Tài khoản của bạn đã được xác nhận";
        String emailText = String.format(
                "Xin chào %s,\n\n" +
                "Tài khoản đại lý của bạn đã được quản trị viên phê duyệt thành công!\n\n" +
                "Thông tin tài khoản:\n" +
                "- Tên đăng nhập: %s\n" +
                "- Email: %s\n" +
                "- Tên đại lý: %s\n\n" +
                "Bạn có thể đăng nhập vào hệ thống ngay bây giờ.\n\n" +
                "Trân trọng,\n" +
                "Đội ngũ EDrive",
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                dealer.getDealerName()
        );
        
        emailService.sendEmail(user.getEmail(), emailSubject, emailText);
        
        log.info("Account verified successfully by admin for dealer ID: {}", dealerId);
    }

    @Override
    public String getBusinessLicenseUrl(Long dealerId) {
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy đại lý với ID: " + dealerId));

        String businessLicenseUrl = dealer.getBusinessLicenseUrl();
        if (businessLicenseUrl == null || businessLicenseUrl.isEmpty()) {
            throw new BadRequestException("Không tìm thấy giấy phép kinh doanh cho đại lý này");
        }

        return businessLicenseUrl;
    }

}