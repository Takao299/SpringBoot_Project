package com.example.info;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.ToString;

@Component
@Getter
@ToString
public class LocalInfo {

    @Value("${attached.file.path:no_pass}")
    private String attachedFilePath;

    @Value("${attachedtemp.file.path:no_pass}")
    private String attachedTempFilePath;

    @Value("${my-file-size.size:}")
    private Long fileSize;
}
