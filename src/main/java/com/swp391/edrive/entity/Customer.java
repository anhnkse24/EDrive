    package com.swp391.edrive.entity;

    import com.swp391.edrive.enums.Gender;
    import jakarta.persistence.*;
    import jakarta.validation.constraints.*;
    import lombok.*;

    import java.time.LocalDate;
    import java.util.List;

    @Entity
    @Table(
            name = "customers",
            indexes = {
                    @Index(name = "idx_customer_email", columnList = "email"),
                    @Index(name = "idx_customer_phone", columnList = "phone"),
                    @Index(name = "idx_customer_idcard", columnList = "id_card_no")
            },
            uniqueConstraints = {
                    @UniqueConstraint(name = "uk_customer_email", columnNames = {"email"}),
                    @UniqueConstraint(name = "uk_customer_phone", columnNames = {"phone"}),
                    @UniqueConstraint(name = "uk_customer_idcard", columnNames = {"id_card_no"})
            }
    )
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public class Customer {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long customerId;

        @NotBlank(message = "Họ tên không được để trống")
        @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
        @Column(name = "full_name", length = 100, nullable = false)
        private String fullName;

        @Past(message = "Ngày sinh phải ở trong quá khứ")
        @Column(name = "dob")
        private LocalDate dob;

        @Enumerated(EnumType.STRING)
        @Column(name = "gender", length = 10, nullable = false)
        private Gender gender = Gender.KHAC;

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không hợp lệ")
        @Size(max = 100, message = "Email tối đa 100 ký tự")
        @Column(name = "email", length = 100, nullable = false)
        private String email;

        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "^(0[0-9]{9})$", message = "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0")
        @Column(name = "phone", length = 20, nullable = false)
        private String phone;

        @NotBlank(message = "Địa chỉ không được để trống")
        @Size(max = 200, message = "Địa chỉ tối đa 200 ký tự")
        @Column(name = "address", length = 200, nullable = false)
        private String address;

        @NotBlank(message = "Số CMND/CCCD không được để trống")
        @Pattern(regexp = "^[0-9]{9,12}$", message = "Số CMND/CCCD phải từ 9 đến 12 chữ số")
        @Column(name = "id_card_no", length = 12, nullable = false)
        private String idCardNo;

        @OneToMany(mappedBy = "customer")
        @ToString.Exclude
        private List<TestDrive> testDrives;

        @OneToMany(mappedBy = "customer")
        @ToString.Exclude
        private List<Feedback> feedbacks;

        @OneToMany(mappedBy = "customer")
        @ToString.Exclude
        private List<Quotation> quotations;

        @OneToMany(mappedBy = "customer")
        @ToString.Exclude
        private List<Order> orders;

        @OneToMany(mappedBy = "customer")
        @ToString.Exclude
        private List<CustomerDebt> customerDebts;
    }
