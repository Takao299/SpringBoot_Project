package com.example.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.data.MemberQuery;
import com.example.entity.Member;

//@Repository
public interface MemberDao {

	  // JPQLによる検索
	  Page<Member> findByJPQL(MemberQuery memberQuery, Pageable pageable);

}
