package com.example.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import com.example.data.ReservationForm;

@Component
public class TimeStartEndValidator implements ConstraintValidator<TimeStartEnd, ReservationForm> {

    @Override
    public boolean isValid(ReservationForm value, ConstraintValidatorContext context) {

        if(value.getId() != null || value.getRstart() == null || value.getRend() == null
        || value.getMember() == null || value.getFacility() == null || value.getRday() == null) {
            return true; //予約時間が未入力の時はこのエラーは表示しない。NotNullの単体エラー表示に任せる
          }
        return value.getRstart().isBefore(value.getRend()) ? true : false;	//falseならエラー表示
        		//value.getRstart().equals(value.getRend())	//開始==終了は許していない仕様。許す場合Overlap系Validatorを変更する必要がある
    }
}