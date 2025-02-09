package com.example.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.data.TimeBlock;
import com.example.entity.Facility;
import com.example.service.TimeListService;

@RestController
public class TimeListController {

    @Autowired
    private TimeListService timeListService;

    @PostMapping("/reservation/select")
    public List<TimeBlock> selectDay(//@RequestParam Member member,
    						@RequestParam Facility facility,
    						@RequestParam String rday	//LocalDateで受け取れない //2024-07-31
    						) {
    	LocalDate ld = LocalDate.parse(rday);
        return timeListService.makeTimeList(facility, ld);
    }

}
