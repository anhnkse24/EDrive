    package com.swp391.edrive.entity;

    import jakarta.persistence.*;
    import jakarta.validation.constraints.*;
    import lombok.Getter;
    import lombok.Setter;

    import java.time.LocalDate;
    import java.util.List;

    @Entity
    @Table(name = "customers")
    @Getter
    @Setter
    public class Customer {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long customerId;

        @NotBlank(message = "Họ tên không được để trống")
        @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
        private String fullName;

        @Past(message = "Ngày sinh phải ở trong quá khứ")
        private LocalDate dob;

        @NotBlank(message = "Giới tính không được để trống")
        @Pattern(regexp = "Nam|Nữ|Khác", message = "Giới tính phải là Nam, Nữ hoặc Khác")
        private String gender;

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không hợp lệ")
        @Size(max = 100, message = "Email tối đa 100 ký tự")
        private String email;

        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "^(0[0-9]{9})$", message = "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0")
        private String phone;

        @NotBlank(message = "Địa chỉ không được để trống")
        @Size(max = 200, message = "Địa chỉ tối đa 200 ký tự")
        private String address;

        @NotBlank(message = "Số CMND/CCCD không được để trống")
        @Pattern(regexp = "^[0-9]{9,12}$", message = "Số CMND/CCCD phải từ 9 đến 12 chữ số")
        private String idCardNo;

        @OneToMany(mappedBy = "customer")
        private List<TestDrive> testDrives;

        @OneToMany(mappedBy = "customer")
        private List<Feedback> feedbacks;

        @OneToMany(mappedBy = "customer")
        private List<Quotation> quotations;

        @OneToMany(mappedBy = "customer")
        private List<Order> orders;

        @OneToMany(mappedBy = "customer")
        private List<CustomerDebt> customerDebts;

        @ManyToOne
        @JoinColumn(name = "dealer_id", nullable = false)
        private Dealer dealer;
    }
