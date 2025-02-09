package com.example.validator;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.data.ReservationForm;
import com.example.entity.Reservation;
import com.example.repository.ReservationRepository;

@Component
public class OverlapFacilityValidator implements ConstraintValidator<OverlapFacility, ReservationForm> {

	@Autowired
	private ReservationRepository reservationRepository;

    public OverlapFacilityValidator() {
    	this.reservationRepository = null;
    }

    @Autowired
    public OverlapFacilityValidator(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public boolean isValid(ReservationForm value, ConstraintValidatorContext context) {

    	List<Reservation> reservationList = reservationRepository.findByFacilityIdAndDeleteDateTimeIsNull(value.getFacility().getId());

        if(value.getId() != null || value.getRstart() == null || value.getRend() == null
        || value.getMember() == null || value.getFacility() == null || value.getRday() == null) {
            return true;
          }

        int num = 0;
        for(Reservation list : reservationList) {
        	if( list.getDeleteDateTime() == null ) { //削除済みではない
        										// || !(value.getId().equals(list.getId()) 同じ予約ID(更新)で時間を変えないまま更新ボタンを押した時はエラーが出るようにする
	        	if(value.getRday().equals(list.getRday())) { //予約日が同じ
	        		if(( value.getRend().isAfter(list.getRstart()) && value.getRend().isBefore(list.getRend()) )	//入力値の終了時間が既存予約時間範囲内
	        		|| ( value.getRstart().isAfter(list.getRstart()) && value.getRstart().isBefore(list.getRend()) )//入力値の開始時間が既存予約時間範囲内
	        		|| ( value.getRstart().isAfter(list.getRstart()) && value.getRend().isBefore(list.getRend()) )	//入力値の開始＆終了時間が既存予約時間範囲内
	        		|| ( value.getRend().isAfter(list.getRend()) && value.getRstart().isBefore(list.getRstart()) )	//入力値の開始＆終了時間が既存予約時間範囲を覆う
	        		//以下はTimeStartEndValidatorで開始==終了を許す場合に必要
	        		|| ( value.getRstart().equals(list.getRstart()) )	//入力値の開始時間が既存予約時間範囲内
	        		|| ( value.getRstart().equals(list.getRend()) )	//入力値の開始時間が既存予約時間範囲内
	        		|| ( value.getRend().equals(list.getRstart()) )	//入力値の終了時間が既存予約時間範囲内
	        		|| ( value.getRend().equals(list.getRend()) )		//入力値の終了時間が既存予約時間範囲内
	        		) {
	        			num += 1;	//被った予約時間が+1
	        			if(num >= value.getFacility().getAmount()) {	//被った予約時間が施設数量以上になったらエラー
	        																//例：被った予約時間が１件、しかし台数２なのでOK
	        				return false;	//falseを返してエラー表示
	        			}
	        		}
	        	}
        	}
        }
        return true;
    }
    
}