package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.BusinessHour;

public interface BusinessHourRepository extends JpaRepository<BusinessHour, Long> {

	Optional<BusinessHour> findById(Integer id);

	List<BusinessHour> findAllByOrderById();

}
