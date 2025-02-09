package com.example.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.entity.AdminUser;
import com.example.repository.AdminUserRepository;

public class UniqueLoginValidator implements ConstraintValidator<UniqueLogin, AdminUser> {

    private final AdminUserRepository userRepository;

    public UniqueLoginValidator() {
        this.userRepository = null;
    }

    @Autowired
    public UniqueLoginValidator(AdminUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(AdminUser value, ConstraintValidatorContext context) {
    	return		userRepository == null
    			 || userRepository.findByUsernameAndDeleteDateTimeIsNull(value.getUsername()).isEmpty()
    			 || userRepository.findByUsernameAndDeleteDateTimeIsNull(value.getUsername()).get().getId() == value.getId();
    				//もし重複した名前があってもIDが同じなら更新処理なので許可
    }
}
