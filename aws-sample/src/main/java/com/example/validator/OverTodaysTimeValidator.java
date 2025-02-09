package com.example.validator;

import java.time.LocalDateTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.data.ReservationForm;
import com.example.entity.PermissionHour;
import com.example.repository.PermissionHourRepository;

@Component
public class OverTodaysTimeValidator implements ConstraintValidator<OverTodaysTime, ReservationForm> {

	@Autowired
	private PermissionHourRepository permissionHourRepository;

    public OverTodaysTimeValidator() {
    	this.permissionHourRepository = null;
    }

	@Autowired
	public OverTodaysTimeValidator(PermissionHourRepository permissionHourRepository) {
	    this.permissionHourRepository = permissionHourRepository;
	}

    @Override
    public boolean isValid(ReservationForm value, ConstraintValidatorContext context) {

    	PermissionHour permissionHour = permissionHourRepository.findById(1L).get();

        if(value.getId() != null ||  value.getRstart() == null || value.getRend() == null
        || value.getMember() == null || value.getFacility() == null || value.getRday() == null) {
            return true; //予約時間が未入力の時はこのエラーは表示しない。NotNullの単体エラー表示に任せる //また、新規でないなら不問
          }

        //予約日が今日でかつ、予約開始時間が現在以下の場合エラー 例：予約開始12:00 現在時刻12:01
        //return value.getRday().equals(LocalDate.now())&&value.getRstart().getHour() <= LocalTime.now().getHour() ? false : true;	//falseならエラー表示

        //予約日・予約開始時間をLocalDateTimeに変換
        LocalDateTime ldt1 = LocalDateTime.of(value.getRday(), value.getRstart());
        //予約不可時間をマイナス（分単位）したLocalDateTime
        LocalDateTime ldt2 = ldt1.minusMinutes(permissionHour.getNonCancellableTime().longValue());
        //現在時刻が予約不可時間より前か
        return LocalDateTime.now().isBefore(ldt2) ? true : false;
    }
}