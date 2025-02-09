package com.example.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NewPassCheckValidator.class)
public @interface NewPassCheck {
    String message() default "パスワードが正しく入力されていません";
    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default{};

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {
    	NewPassCheck[] value();
    }
}
