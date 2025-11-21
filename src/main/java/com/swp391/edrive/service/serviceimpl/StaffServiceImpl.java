    package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.constant.PredefinedRole;
import com.swp391.edrive.dto.request.StaffCreateRequest;
import com.swp391.edrive.dto.request.StaffUpdateRequest;
import com.swp391.edrive.dto.response.StaffResponse;
import com.swp391.edrive.entity.Dealer;
import com.swp391.edrive.entity.Role;
import com.swp391.edrive.entity.User;
import com.swp391.edrive.exception.exceptions.BadRequestException;
import com.swp391.edrive.exception.exceptions.ConflictException;
import com.swp391.edrive.exception.exceptions.ForbiddenException;
import com.swp391.edrive.exception.exceptions.NotFoundException;
import com.swp391.edrive.repository.DealerRepository;
import com.swp391.edrive.repository.RoleRepository;
import com.swp391.edrive.repository.UserRepository;
import com.swp391.edrive.service.StaffService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffServiceImpl implements StaffService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DealerRepository dealerRepository;
    private final PasswordEncoder passwordEncoder;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    @Transactional
    public StaffResponse createDealerStaff(StaffCreateRequest request) {
        // Validate unique constraints
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã tồn tại");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new ConflictException("Số điện thoại đã tồn tại");
        }

        // Get current dealer manager
        User currentUser = getCurrentUser();
        if (currentUser.getDealer() == null) {
            throw new BadRequestException("Chỉ Dealer Manager mới có thể tạo nhân viên đại lý");
        }

        // Create new staff user
        User staff = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .fullName(request.getFullName())
                .dealer(currentUser.getDealer())
                .isVerify(true) // Staff account is immediately active
                .build();

        // Assign DEALER_STAFF role
        Set<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.DEALER_STAFF_ROLE)
                .ifPresent(roles::add);
        staff.setRoles(roles);

        User savedStaff = userRepository.save(staff);
        log.info("Dealer staff created successfully by {}: {}", currentUser.getUsername(), savedStaff.getUsername());

        return mapToStaffResponse(savedStaff);
    }

    @Override
    @Transactional
    public StaffResponse createEvmStaff(StaffCreateRequest request) {
        // Validate unique constraints
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã tồn tại");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new ConflictException("Số điện thoại đã tồn tại");
        }

        // Create new EVM staff user
        User staff = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .fullName(request.getFullName())
                .dealer(null) // EVM staff doesn't belong to any dealer
                .isVerify(true) // Staff account is immediately active
                .build();

        // Assign EVM_STAFF role
        Set<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.EVM_STAFF_ROLE)
                .ifPresent(roles::add);
        staff.setRoles(roles);

        User savedStaff = userRepository.save(staff);
        log.info("EVM staff created successfully: {}", savedStaff.getUsername());

        return mapToStaffResponse(savedStaff);
    }

    @Override
    public StaffResponse getStaffById(Long staffId) {
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhân viên với ID: " + staffId));

        // Check authorization
        User currentUser = getCurrentUser();
        if (currentUser.hasRole(PredefinedRole.DEALER_MANAGER_ROLE)) {
            // Dealer manager can only view their own dealer's staff
            if (staff.getDealer() == null || !staff.getDealer().getDealerId().equals(currentUser.getDealer().getDealerId())) {
                throw new ForbiddenException("Bạn không có quyền xem nhân viên này");
            }
        }

        return mapToStaffResponse(staff);
    }

    @Override
    public List<StaffResponse> getAllDealerStaff() {
        User currentUser = getCurrentUser();
        if (currentUser.getDealer() == null) {
            throw new BadRequestException("Chỉ Dealer Manager mới có thể xem danh sách nhân viên đại lý");
        }

        List<User> staffList = userRepository.findByDealerAndRoles_Name(
                currentUser.getDealer(),
                PredefinedRole.DEALER_STAFF_ROLE
        );

        return staffList.stream()
                .map(this::mapToStaffResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StaffResponse> getAllEvmStaff() {
        List<User> staffList = userRepository.findByRoles_NameAndDealerIsNull(PredefinedRole.EVM_STAFF_ROLE);

        return staffList.stream()
                .map(this::mapToStaffResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StaffResponse updateStaff(Long staffId, StaffUpdateRequest request) {
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhân viên với ID: " + staffId));

        // Check authorization
        User currentUser = getCurrentUser();
        if (currentUser.hasRole(PredefinedRole.DEALER_MANAGER_ROLE)) {
            // Dealer manager can only update their own dealer's staff
            if (staff.getDealer() == null || !staff.getDealer().getDealerId().equals(currentUser.getDealer().getDealerId())) {
                throw new ForbiddenException("Bạn không có quyền cập nhật nhân viên này");
            }
        }

        // Update fields if provided
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (!request.getEmail().equals(staff.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new ConflictException("Email đã tồn tại");
            }
            staff.setEmail(request.getEmail());
        }

        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            if (!request.getPhone().equals(staff.getPhone()) && userRepository.existsByPhone(request.getPhone())) {
                throw new ConflictException("Số điện thoại đã tồn tại");
            }
            staff.setPhone(request.getPhone());
        }

        if (request.getFullName() != null && !request.getFullName().isEmpty()) {
            staff.setFullName(request.getFullName());
        }

        User updatedStaff = userRepository.save(staff);
        log.info("Staff updated successfully: {}", updatedStaff.getUsername());

        return mapToStaffResponse(updatedStaff);
    }

    @Override
    @Transactional
    public void deleteStaff(Long staffId) {
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhân viên với ID: " + staffId));

        // Check authorization
        User currentUser = getCurrentUser();
        if (currentUser.hasRole(PredefinedRole.DEALER_MANAGER_ROLE)) {
            // Dealer manager can only delete their own dealer's staff
            if (staff.getDealer() == null || !staff.getDealer().getDealerId().equals(currentUser.getDealer().getDealerId())) {
                throw new ForbiddenException("Bạn không có quyền xóa nhân viên này");
            }
        }

        // Soft delete by setting isVerify to false
        staff.setVerify(false);
        userRepository.save(staff);
        log.info("Staff deactivated successfully: {}", staff.getUsername());
    }

    @Override
    @Transactional
    public void reactivateStaff(Long staffId) {
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhân viên với ID: " + staffId));

        // Check authorization
        User currentUser = getCurrentUser();
        if (currentUser.hasRole(PredefinedRole.DEALER_MANAGER_ROLE)) {
            // Dealer manager can only reactivate their own dealer's staff
            if (staff.getDealer() == null || !staff.getDealer().getDealerId().equals(currentUser.getDealer().getDealerId())) {
                throw new ForbiddenException("Bạn không có quyền kích hoạt lại nhân viên này");
            }
        }

        // Reactivate by setting isVerify to true
        staff.setVerify(true);
        userRepository.save(staff);
        log.info("Staff reactivated successfully: {}", staff.getUsername());
    }

    private StaffResponse mapToStaffResponse(User user) {
        return StaffResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .dealerId(user.getDealer() != null ? user.getDealer().getDealerId() : null)
                .dealerName(user.getDealer() != null ? user.getDealer().getDealerName() : null)
                .isVerified(user.isVerify())
                .build();
    }
}

