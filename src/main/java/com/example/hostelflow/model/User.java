package com.example.hostelflow.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // internal DB identity — never exposed externally

    // University-issued student business identifier. Null for STAFF/ADMIN.
    // Immutable after creation — deliberately no setter.
    @Column(unique = true, updatable = false)
    private String registrationNumber;

    @Column(nullable = false)
    private String name;

    // Universal authentication identifier for all roles.
    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 60)
    private String password; // BCrypt hash; null until a student activates

    @Column(length = 60)
    private String activationCodeHash; // BCrypt hash; null for STAFF/ADMIN and after activation

    @Column(nullable = false)
    private boolean activated;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }

    /** JPA only — not for application code. */
    protected User() {
    }

    // ---- Factory methods -------------------------------------------------

    /**
     * Creates a pre-provisioned, unactivated STUDENT record. Called by the
     * university-provisioning/seed process, never by the student themself.
     */
    public static User forStudent(String registrationNumber, String name,
                                  String email, String activationCodeHash) {
        User user = new User();
        user.registrationNumber = registrationNumber;
        user.name = name;
        user.email = email;
        user.activationCodeHash = activationCodeHash;
        user.userRole = UserRole.STUDENT;
        user.activated = false;
        return user;
    }

    /**
     * Creates an already-active STAFF or ADMIN record, provisioned directly
     * (by an admin, or seeded for the first admin) with a password already set.
     */
    public static User forStaffOrAdmin(String name, String email,
                                       String passwordHash, UserRole role) {
        if (role != UserRole.STAFF && role != UserRole.ADMIN) {
            throw new IllegalArgumentException("Use forStudent() for STUDENT role");
        }
        User user = new User();
        user.name = name;
        user.email = email;
        user.password = passwordHash;
        user.userRole = role;
        user.activated = true;
        return user;
    }

    // ---- Domain behaviour --------------------------------------------

    /**
     * Completes student activation: sets the password, marks the account
     * active, and clears the activation code hash so it can't be reused.
     * The caller (service layer) is responsible for verifying the raw
     * activation code against activationCodeHash BEFORE calling this.
     */
    public void activate(String passwordHash) {
        if (this.activated) {
            throw new IllegalStateException("User is already activated");
        }

        if (this.activationCodeHash == null) {
            throw new IllegalStateException("No activation code available");
        }

        this.password = passwordHash;
        this.activated = true;
        this.activationCodeHash = null;
    }

    // ---- Getters ---------------------------------------------------------

    public Long getId() {
        return id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }
    // no setter — immutable business identity

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    // no public setter — mutate only via activate() or a future
    // changePassword()-style method in the service layer

    public String getActivationCodeHash() {
        return activationCodeHash;
    }
    // no setter — only cleared internally by activate()

    public boolean isActivated() {
        return activated;
    }
    // no public setter — activated flips true only via activate()

    public UserRole getUserRole() {
        return userRole;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}