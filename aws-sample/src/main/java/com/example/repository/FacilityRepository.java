package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.Facility;

public interface FacilityRepository extends JpaRepository<Facility, Long> {

	List<Facility> findByDeleteDateTimeIsNullOrderByIdAsc();
	Optional<Facility> findByIdAndDeleteDateTimeIsNull(Long id);
	Optional<Facility> findFirstByDeleteDateTimeIsNull();

}
