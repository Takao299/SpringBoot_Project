package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.MailCode;

public interface MailCodeRepository extends JpaRepository<MailCode, Long> {

	Optional<MailCode> findByEmailAndCodeAndSessionId(String email, String code, String sessionId);

}
