package com.example.data;

import java.util.List;

import com.example.entity.BusinessHour;
import com.example.entity.TemporaryBusiness;
import com.example.validator.TimeStartEnd2;

import lombok.Data;

@Data
@TimeStartEnd2
public class BusinessHourForm {

	private List<BusinessHour> businessHours;
	private List<TemporaryBusiness> temporaryBusiness;

	public BusinessHourForm() {
		this.businessHours = null;
		this.temporaryBusiness = null;
	}

	public BusinessHourForm(List<BusinessHour> businessHours, List<TemporaryBusiness> temporaryBusiness) {
		this.businessHours = businessHours;
		this.temporaryBusiness = temporaryBusiness;
	}
}
