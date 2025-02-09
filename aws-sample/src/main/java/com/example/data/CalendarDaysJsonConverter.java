package com.example.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import lombok.Data;

@Data
public class CalendarDaysJsonConverter {

	private List<OffDay> offDayList;
    @Data
    public static class OffDay {

        private Integer intday;

    }

	private List<TempOnDay> tempOnDayList;
    @Data
    public static class TempOnDay {
        @JsonDeserialize(using = LocalDateDeserializer.class)
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate tonday;

    }

	private List<TempOffDay> tempOffDayList;
    @Data
    public static class TempOffDay {
        @JsonDeserialize(using = LocalDateDeserializer.class)
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate toffday;

    }

    public CalendarDaysJsonConverter(List<Integer> offDayList, List<LocalDate> tempOnDayList, List<LocalDate> tempOffDayList) {

    	List<OffDay> _offDayList = new ArrayList<>();
    	for(Integer day:offDayList) {
    		OffDay od = new OffDay();
    		od.setIntday(day);
    		_offDayList.add(od);
    	}
    	this.offDayList = _offDayList;

    	List<TempOnDay> _tempOnDayList = new ArrayList<>();
    	for(LocalDate day:tempOnDayList) {
    		TempOnDay od = new TempOnDay();
    		od.setTonday(day);
    		_tempOnDayList.add(od);
    	}
    	this.tempOnDayList = _tempOnDayList;

    	List<TempOffDay> _tempOffDayList = new ArrayList<>();
    	for(LocalDate day:tempOffDayList) {
    		TempOffDay od = new TempOffDay();
    		od.setToffday(day);
    		_tempOffDayList.add(od);
    	}
    	this.tempOffDayList = _tempOffDayList;

    }

}
