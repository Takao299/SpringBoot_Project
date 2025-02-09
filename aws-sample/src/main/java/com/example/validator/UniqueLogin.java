package com.example.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
//@Target({ElementType.METHOD, ElementType.FIELD})//@Target({ElementType.TYPE})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueLoginValidator.class)
public @interface UniqueLogin {
    String message() default "このユーザ名は既に登録されています";
    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default{};
}
