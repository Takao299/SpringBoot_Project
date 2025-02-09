package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	//List<Member> findByNameLike(String name);
	//List<Member> findByNameLikeAndId(String name, Long id);
	List<Member> findByDeleteDateTimeIsNull();
	Optional<Member> findByEmailAndDeleteDateTimeIsNull(String email);
	Optional<Member> findByIdAndDeleteDateTimeIsNull(Long id);
	Optional<Member> findFirstByDeleteDateTimeIsNull();

}
