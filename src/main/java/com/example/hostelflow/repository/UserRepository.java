package com.example.hostelflow.repository;

import com.example.hostelflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Authentication lookup — email is the universal login identifier.
    Optional<User> findByEmail(String email);

    // Business-identity lookup — used for activation and for reporting-domain
    // lookups. Only populated for STUDENT rows.
    Optional<User> findByRegistrationNumber(String registrationNumber);

    boolean existsByEmail(String email);

    boolean existsByRegistrationNumber(String registrationNumber);

    // Atomic conditional activation. The "WHERE ... activated = false" makes
    // this a single compare-and-set at the database level: if two requests
    // race to activate the same user, only the one that the DB processes
    // first will find a matching row (activated still false) and flip it —
    // the second will match zero rows because by the time its UPDATE runs,
    // activated is already true. This closes the check-then-act gap that
    // existed in the old find -> check isActivated() -> save() sequence,
    // without needing application-level locking.
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE User u SET u.password = :passwordHash, u.activated = true, u.activationCodeHash = null " +
            "WHERE u.registrationNumber = :registrationNumber AND u.activated = false")
    int atomicallyActivate(@Param("registrationNumber") String registrationNumber,
                            @Param("passwordHash") String passwordHash);
}