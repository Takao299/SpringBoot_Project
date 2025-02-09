package com.example.info;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.ToString;

@Component
@Getter
@ToString
public class AndroidInfo {

    @Value("${android-setting.session-time.login.day}")
    private Long day;

    @Value("${android-setting.session-time.login.hour}")
    private Long hour;

    @Value("${android-setting.session-time.login.minute}")
    private Long minute;

    @Value("${android-setting.session-time.codepage.hour}")
    private Long pageHour;

    @Value("${android-setting.session-time.codepage.minute}")
    private Long pageMinute;

}
