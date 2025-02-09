package com.example.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailCodeData {
    private String email;
    private String code;
    private String pageSessionId;
    private SessionData loginSession;
}
