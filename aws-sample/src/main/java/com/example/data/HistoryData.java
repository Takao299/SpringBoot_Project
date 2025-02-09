package com.example.data;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.entity.Facility;
import com.example.entity.Member;
import com.example.entity.Reservation;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import lombok.Data;

@Data
public class HistoryData {

    private Long id;
    private Long memberId;
    private Long facilityId;
    private String memberName;
    private String facilityName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate rday;

    @DateTimeFormat(pattern = "HH:mm")
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime rstart;

    @DateTimeFormat(pattern = "HH:mm")
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime rend;


    public HistoryData(){
    }

    public HistoryData(ReservationForm rf){
    	this.id = rf.getId();
    	this.memberId = rf.getMember().getId();
    	this.facilityId = rf.getFacility().getId();
    	this.memberName = rf.getMember().getName();
    	this.facilityName = rf.getFacility().getName();
    	this.rday = rf.getRday();
    	this.rstart = rf.getRstart();
    	this.rend = rf.getRend();
    }

    public HistoryData(Reservation res, Member member, Facility facility){
    	this.id = res.getId();
    	this.memberId = member.getId();
    	this.facilityId = facility.getId();
    	this.memberName = member.getName();
    	this.facilityName = facility.getName();
    	this.rday = res.getRday();
    	this.rstart = res.getRstart();
    	this.rend = res.getRend();
    }

}
