    package com.swp391.edrive.entity;

    import jakarta.persistence.*;
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

        private String fullName;
        private LocalDate dob;
        private String gender;
        private String email;
        private String phone;
        private String address;
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
    }
