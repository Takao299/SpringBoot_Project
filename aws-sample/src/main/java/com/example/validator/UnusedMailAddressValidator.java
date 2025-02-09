package com.example.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.entity.Member;
import com.example.repository.MemberRepository;

public class UnusedMailAddressValidator implements ConstraintValidator<UnusedMailAddress, Member> {

    private final MemberRepository memberRepository;

    public UnusedMailAddressValidator() {
        this.memberRepository = null;
    }

    @Autowired
    public UnusedMailAddressValidator(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public boolean isValid(Member value, ConstraintValidatorContext context) {
    	return	memberRepository == null
   			 || memberRepository.findByEmailAndDeleteDateTimeIsNull(value.getEmail()).isEmpty()
   			 || memberRepository.findByEmailAndDeleteDateTimeIsNull(value.getEmail()).get().getId() == value.getId();
    			//もし重複したEmailがあってもIDが同じなら更新処理なので許可
    }
}