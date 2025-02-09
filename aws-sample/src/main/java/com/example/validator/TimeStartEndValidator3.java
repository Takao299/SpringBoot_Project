package com.example.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import com.example.entity.TemporaryBusiness;

@Component
public class TimeStartEndValidator3 implements ConstraintValidator<TimeStartEnd3, TemporaryBusiness> {

    @Override
    public boolean isValid(TemporaryBusiness value, ConstraintValidatorContext context) {

    	return value.getOpenTime() <= value.getCloseTime();
    }
}