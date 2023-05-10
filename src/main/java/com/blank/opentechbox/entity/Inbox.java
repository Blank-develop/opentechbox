package com.blank.opentechbox.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Inbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String receiver;
    private String message;
    private boolean isRead;
    @CreationTimestamp
    private LocalDate createDate;
    public Inbox(String receiver, String message) {
        this.receiver = receiver;
        this.message = message;
    }

    public Inbox(String message, Date date) {
    }
}
