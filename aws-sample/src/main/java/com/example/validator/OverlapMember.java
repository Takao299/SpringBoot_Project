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
@Constraint(validatedBy = OverlapMemberValidator.class)
public @interface OverlapMember {
    String message() default "予約時間エラー：選択した会員はその時間に予約があります";
    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default{};

    /*
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {
    	OverlapMember[] value();
    }
    */
}
