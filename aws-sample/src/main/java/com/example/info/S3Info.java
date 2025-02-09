package com.example.info;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.ToString;

@Component
@Getter
@ToString
public class S3Info {
    @Value("${bucket.name:no_bucket}")
    private String buckectName;

    @Value("${buckettemp.name:no_buckettemp}")
    private String buckectTempName;
}
