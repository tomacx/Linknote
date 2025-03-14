package com.example.linknote.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name="avatar_index")
    private int avatarIndex;

    @Column(name="level")
    private int level;

    @Column(name="experience_points")
    private int experiencePoints;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;


    @Column(name="last_login")
    private LocalDateTime lastLogin;

    public String getPassword(){
        return this.password;
    }
}