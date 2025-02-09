package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.SessionId;

public interface SessionIdRepository extends JpaRepository<SessionId, Long> {

	Optional<SessionId> findBySessionId(String sessionId);

	List<SessionId> findByMemberIdAndActiveIsTrue(Long memberId);

}
