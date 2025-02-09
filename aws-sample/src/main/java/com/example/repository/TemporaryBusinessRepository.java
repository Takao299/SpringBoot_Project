package com.example.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.TemporaryBusiness;

public interface TemporaryBusinessRepository extends JpaRepository<TemporaryBusiness, Long> {

	Optional<TemporaryBusiness> findById(Integer id);
	List<TemporaryBusiness> findByDeleteDateTimeIsNull();
	Optional<TemporaryBusiness> findByRdayAndDeleteDateTimeIsNull(LocalDate rday);

}
