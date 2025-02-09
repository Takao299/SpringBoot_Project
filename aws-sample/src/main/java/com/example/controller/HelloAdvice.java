package com.example.controller;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import com.example.util.CustomeStringTrimmerEditor;

@ControllerAdvice
public class HelloAdvice {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new CustomeStringTrimmerEditor(true));
    }
}
