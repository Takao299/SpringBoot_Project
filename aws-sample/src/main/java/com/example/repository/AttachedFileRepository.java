package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.AttachedFile;

public interface AttachedFileRepository extends JpaRepository<AttachedFile, Integer> {

    List<AttachedFile> findByForeignIdOrderByAfId(Integer foreignId);

	List<AttachedFile>  findByForeignIdAndDeleteDateTimeIsNullOrderByAfId(Integer foreignId);

}
