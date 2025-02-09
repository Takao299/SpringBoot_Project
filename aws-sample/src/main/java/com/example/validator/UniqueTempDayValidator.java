package com.example.validator;

import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.repository.TemporaryBusinessRepository;

public class UniqueTempDayValidator implements ConstraintValidator<UniqueTempDay, LocalDate> {

	private final TemporaryBusinessRepository temporaryBusinessRepository;

    public UniqueTempDayValidator() {
        this.temporaryBusinessRepository = null;
    }

    @Autowired
    public UniqueTempDayValidator(TemporaryBusinessRepository temporaryBusinessRepository) {
        this.temporaryBusinessRepository = temporaryBusinessRepository;
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
    	return		temporaryBusinessRepository == null
    			 || temporaryBusinessRepository.findByRdayAndDeleteDateTimeIsNull(value).isEmpty();
    }
}
