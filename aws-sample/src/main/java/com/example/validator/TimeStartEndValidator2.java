package com.example.validator;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import com.example.data.BusinessHourForm;
import com.example.entity.BusinessHour;

@Component
public class TimeStartEndValidator2 implements ConstraintValidator<TimeStartEnd2, BusinessHourForm> {

    @Override
    public boolean isValid(BusinessHourForm value, ConstraintValidatorContext context) {

    	List<BusinessHour> businessHours = value.getBusinessHours();

    	for(BusinessHour bh:businessHours) {
    		if(bh.getOpenTime() == null
    		|| bh.getCloseTime() == null
    		|| bh.getOpenTime() > bh.getCloseTime()) {
    			return false;
    		}
    	}
    	return true;
    }
}