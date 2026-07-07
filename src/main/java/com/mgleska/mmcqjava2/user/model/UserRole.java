package com.mgleska.mmcqjava2.user.model;

import com.mgleska.mmcqjava2.user.enums.RoleEnum;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "usr_user_role")
public class UserRole implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    User user;

    @Column(columnDefinition = "ENUM('ROLE_USER', 'ROLE_ADMIN')", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleEnum role = RoleEnum.ROLE_USER;

    public UserRole() {
    }

    public UserRole(User user, RoleEnum role) {
        this.user = user;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }
}
