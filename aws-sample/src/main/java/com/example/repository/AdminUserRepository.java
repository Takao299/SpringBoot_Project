package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.AdminUser;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
	Optional<AdminUser> findByUsernameAndDeleteDateTimeIsNull(String username);

	Optional<AdminUser> findByIdAndDeleteDateTimeIsNull(Long id);

	List<AdminUser> findByDeleteDateTimeIsNull();
}
