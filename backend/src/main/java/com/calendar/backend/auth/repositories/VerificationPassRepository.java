package com.calendar.backend.auth.repositories;

import com.calendar.backend.auth.models.VerificationPass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationPassRepository extends JpaRepository<VerificationPass, Long> { }
