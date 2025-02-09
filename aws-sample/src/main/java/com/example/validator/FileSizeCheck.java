package com.example.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileSizeCheckValidator.class)
public @interface FileSizeCheck {

    String message() default "ファイルサイズが超過しています";
    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default{};

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {
    	FileSizeCheck[] value();
    }
}
