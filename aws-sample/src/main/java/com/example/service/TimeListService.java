package com.example.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.data.TimeBlock;
import com.example.entity.BusinessHour;
import com.example.entity.Facility;
import com.example.entity.PermissionHour;
import com.example.entity.Reservation;
import com.example.entity.TemporaryBusiness;
import com.example.repository.BusinessHourRepository;
import com.example.repository.PermissionHourRepository;
import com.example.repository.ReservationRepository;
import com.example.repository.TemporaryBusinessRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TimeListService {

	private final ReservationRepository reservationRepository;
	private final BusinessHourRepository businessHourRepository;
	private final TemporaryBusinessRepository temporaryBusinessRepository;
	private final PermissionHourRepository permissionHourRepository;

	public List<TimeBlock> makeTimeList(Facility facility, LocalDate r_day) {

		//時間枠リスト
		List<TimeBlock> timeList = new ArrayList<>();

		//曜日を取得
		DayOfWeek w = r_day.getDayOfWeek();
		//曜日で表示（MONDAY TUESDAY...SUNDAY）
		//System.out.println(w);
		//数字で表示（MONDAY⇒1 TUESDAY⇒2...SUNDAY⇒7）
		//System.out.println(w.getValue());

		//通常営業時間
		BusinessHour bh = businessHourRepository.findById(w.getValue()).get();
		Integer opent = bh.getOpenTime();
		Integer closet = bh.getCloseTime();
		//臨時営業時間　で上書き
		Optional<TemporaryBusiness> temp = temporaryBusinessRepository.findByRdayAndDeleteDateTimeIsNull(r_day);
		if( temp!=null && !temp.isEmpty() ) {
			opent = temp.get().getOpenTime();
			closet = temp.get().getCloseTime();
		}

		//予約不可時間
		PermissionHour permissionHour = permissionHourRepository.findById(1L).get();
		//現在日時に予約不可時間を足すと、最小の予約可能日時が分かる
		//例えば現在23:50で予約不可時間が180分に設定してある場合、翌日の2:50まで予約不可であり、3時枠からしか表示されない
		LocalDateTime nearDateTime = LocalDateTime.now().plusMinutes(permissionHour.getNonReservableTime().longValue());
		LocalDate nearDate = nearDateTime.toLocalDate();
		LocalTime nearTime = nearDateTime.toLocalTime();
		//選択した日が最小予約可能日より前である時、時間枠は無し
		if( r_day.isBefore(nearDate) ) {
			return timeList;
		//選択した日が最小予約可能日であり、営業開始時間が最小予約可能時間以下である時
		}else if( r_day.equals(nearDate) && opent <= nearTime.getHour()) {
			//予約可能時間がまだ最終枠前
			if(closet > nearTime.getHour()) {
				//終了時間が開始時間より1以上大きい場合、開始時間に最小予約可能時間＋１をセット。
				//予約画面で1時間を超えて放置すると過去の時間枠が表示されることになるが最終的にバリデーションエラーで対処
				if(opent<closet) {
					opent = nearTime.getHour()+1;
				//終了時間＝開始時間の場合、時間枠は空で返す
				}else {
					return timeList;
				}
			//予約可能時間が最終枠以上
			}else {
				return timeList;
			}
		}

		/*
		//------------------予約不可時間実装前-------------------------
		//予約日が今日の場合、過ぎた時間枠は表示しない。
		//ただ例えば11:59に12:00~12:59の枠を表示させて12:00に登録ボタンを押下できるので他に処理が必要
		if(r_day.equals(LocalDate.now()) && opent <= LocalTime.now().getHour()) {
			//現在がまだ最終枠前
			if(closet > LocalTime.now().getHour()) {
				//終了時間が開始時間より1以上大きい場合、開始時間に現在時刻＋１をセット。
				//予約画面で1時間を超えて放置すると過去の時間枠が表示されることになるが最終的にバリデーションエラーで対処
				if(opent<closet) {
					opent = LocalTime.now().getHour()+1;
				//終了時間＝開始時間の場合、時間枠は空で返す
				}else {
					return timeList;
				}
			//現在が最終枠以上
			}else {
				return timeList;
			}
		}*/

		List<Reservation> reservationList = reservationRepository.findByFacilityIdAndRdayAndDeleteDateTimeIsNull(facility.getId(), r_day);
		for(int i=opent; i<=closet ; i++) {
			TimeBlock tb = new TimeBlock();
			tb.setRday(r_day);
			tb.setRstart(LocalTime.of(i, 0));
			tb.setRend(LocalTime.of(i, 59));
			tb.setReserved(false);
			//数量処理
			int amount = 0;
			for(Reservation res: reservationList) {
				if(i>=res.getRstart().getHour() && i<=res.getRend().getHour()) { //1時間単位の枠だと過剰なif文 if(9<=9 && 9>=9)みたいな
					amount ++;
					if(amount >= facility.getAmount()) {
						tb.setReserved(true);
					}
				}
			}
			timeList.add(tb);
		}
		//for(TimeBlock tb: timeList) {
			//System.out.println(tb.getR_day().toString() +" : "+ tb.getR_start().toString() +" : "+ tb.getR_end().toString() +" : "+ tb.isReserved());
		//}
		return timeList;
	}


	//休業曜日をセット
	//JQuery用
	public List<Integer> makeOffDayList() {
	    List<BusinessHour> list = businessHourRepository.findAll();
	    List<Integer> offday = new ArrayList<>();
	    for(BusinessHour bh:list) {
	    	if(bh.getIsOpen()==null) {
	    		if(bh.getId()==7) { //jQueryのgetDay()が日曜=0～土曜=6なので
	    			offday.add(0);
	    		}else {
	    			offday.add(bh.getId());
	    		}
	    	}
	    }
	    return offday;
	}

	//休業曜日をセット
	//android用
	public List<Integer> makeOffDayList2() {
	    List<BusinessHour> list = businessHourRepository.findAll();
	    List<Integer> offday = new ArrayList<>();
	    for(BusinessHour bh:list) {
	    	if(bh.getIsOpen()==null) {
	    		offday.add(bh.getId());
	    	}
	    }
	    return offday;
	}


	//臨時営業日をセット
	public List<LocalDate> makeTempOnDayList() {
	    List<TemporaryBusiness> list = temporaryBusinessRepository.findByDeleteDateTimeIsNull();
	    List<LocalDate> onday = new ArrayList<>();
	    for(TemporaryBusiness tb:list) {
	    	if(tb.getIsOpen()!=null)
	    		onday.add(tb.getRday());
	    }
	    return onday;
	}
	//臨時休業日をセット
	public List<LocalDate> makeTempOffDayList() {
	    List<TemporaryBusiness> list = temporaryBusinessRepository.findByDeleteDateTimeIsNull();
	    List<LocalDate> offday = new ArrayList<>();
	    for(TemporaryBusiness tb:list) {
	    	if(tb.getIsOpen()==null)
	    		offday.add(tb.getRday());
	    }
	    return offday;
	}
	//臨時営業・休業日をセット
	public List<LocalDate> makeTempDayList() {
	    List<TemporaryBusiness> list = temporaryBusinessRepository.findByDeleteDateTimeIsNull();
	    List<LocalDate> tempday = new ArrayList<>();
	    for(TemporaryBusiness tb:list) {
	    	tempday.add(tb.getRday());
	    }
	    return tempday;
	}

}
