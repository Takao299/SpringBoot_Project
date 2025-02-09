package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.PermissionHour;

public interface PermissionHourRepository extends JpaRepository<PermissionHour, Long> {

}