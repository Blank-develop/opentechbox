package com.blank.opentechbox.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true)
    private String username;
    @Email
    @Column(unique=true)
    private String email;
    private String password;
    private Double balance;
    private String accountType ="bronze"; //bronze 0%, silver 2%, gold 5%, premium 10%
    @CreationTimestamp
    private LocalDate accountDate;

    private LocalDate accountExpirationDate = null;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

}
