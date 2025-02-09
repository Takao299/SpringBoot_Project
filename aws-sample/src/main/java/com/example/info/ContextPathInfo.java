package com.example.info;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.ToString;

@Component
@Getter
@ToString
public class ContextPathInfo {

    @Value("${server.servlet.context-path}")
    private String context_path;
}
