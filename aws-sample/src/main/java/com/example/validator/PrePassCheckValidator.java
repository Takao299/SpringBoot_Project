package com.example.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.data.PrePassword;
import com.example.repository.AdminUserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PrePassCheckValidator implements ConstraintValidator<PrePassCheck, PrePassword> {

    private final AdminUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean isValid(PrePassword value, ConstraintValidatorContext context) {
    	//System.out.println("preTypePass:"+value.getTypePass());
    	boolean existPass = false;
    	if(value.getTypePass() != null) //passwordEncoder～をreturn内に書くと rawPassword cannot be null
    		existPass = passwordEncoder.matches(value.getTypePass(), userRepository.findById(value.getId()).get().getPassword());
    	return	value.getId() == null
    			|| value.getUpdate().equals("false")
    			|| existPass;
    			//|| passwordEncoder.matches(value.getTypePass(), userRepository.findById(value.getId()).get().getPassword() );
    }
}