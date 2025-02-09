package com.example.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.example.data.NewPassword;

public class NewPassCheckValidator implements ConstraintValidator<NewPassCheck, NewPassword> {

    @Override
    public boolean isValid(NewPassword value, ConstraintValidatorContext context) {

    	boolean existPass = false;
    	if(value.getTypePass2() != null) //checkPass～をreturn内に書くと Cannot invoke "java.lang.CharSequence.length()" because "this.text" is null
    		existPass = checkPass(value.getTypePass2());//パスワードの入力規則
    	return	 ( value.getId() != null && value.getUpdate2().equals("false") )//更新画面でアコーディオン閉じている
    			|| existPass;
    }

    public boolean checkPass(String str) {
    	/*
    	 * 【条件】
    	 * ・8文字以上24文字以下であること。
    	 * ・使用できる文字は半角数字、半角英小文字、半角英大文字、ハイフンのみであること。
    	 * ・数字、英小文字、英大文字、ハイフンの混在であること。→・数字、英文字の混在であること。
    	 */
        //Pattern p = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[-])[a-zA-Z0-9-]{8,24}$");
        Pattern p = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9-]{8,24}$");
        Matcher m = p.matcher(str);
        Boolean result = m.matches();
        //System.out.println("result:" + result);
        return result;
    }
}