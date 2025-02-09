package com.example.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = {UnusedMailAddressValidator.class})
@Target({ElementType.TYPE})
//@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UnusedMailAddress {

    String message() default "このメールアドレスは既に登録されています";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /*
    //@Target({ElementType.FIELD})
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {
        UnusedMailAddress[] value();
    }*/
}