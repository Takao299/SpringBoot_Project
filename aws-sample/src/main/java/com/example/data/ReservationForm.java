package com.example.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.entity.Facility;
import com.example.entity.Member;
import com.example.entity.Reservation;
import com.example.validator.OverNonCancellableTime;
import com.example.validator.OverTodaysTime;
import com.example.validator.OverlapFacility;
import com.example.validator.OverlapMember;
import com.example.validator.TimeStartEnd;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TimeStartEnd
@OverlapMember
@OverlapFacility
@OverTodaysTime
@OverNonCancellableTime
public class ReservationForm {

    private Long id;

    //@NotNull
    private Member member;

    //@NotNull
    private Facility facility;

    @NotNull(message = "予約日を入力して下さい")
    //@FutureOrPresent
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate rday;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime rstart;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime rend;

    private LocalDateTime deleteDateTime;


    public ReservationForm(Reservation res, Member member, Facility facility) {
    	if(res.getId() != null)
    		this.id = res.getId();
		this.member = member;
		this.facility = facility;
		this.rday = res.getRday();
		this.rstart = res.getRstart();
		this.rend = res.getRend();
		if(res.getDeleteDateTime() != null)
			this.deleteDateTime = res.getDeleteDateTime();
    }

    public ReservationForm(Member member, Facility facility) {
		this.member = member;
		this.facility = facility;
    }

    public ReservationForm(Member member) {
    	this.member = member;
    }

    public ReservationForm() {
    }

    public Reservation makeReservation(){
    	Reservation reservation = new Reservation();
    	reservation.setId(id);
    	reservation.setMemberId(member.getId());
    	reservation.setFacilityId(facility.getId());
    	reservation.setRday(rday);
    	reservation.setRstart(rstart);
    	reservation.setRend(rend);
    	reservation.setDeleteDateTime(deleteDateTime);
    	return reservation;
    }

}
