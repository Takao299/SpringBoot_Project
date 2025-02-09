package com.example.validator;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.example.info.LocalInfo;

public class FileSizeCheckValidator implements ConstraintValidator<FileSizeCheck, List<MultipartFile>> {

	@Autowired
	private LocalInfo localinfo;

    @Override
    public boolean isValid(List<MultipartFile> value, ConstraintValidatorContext context) {

    	//フォームで入力した画像(複数)のサイズを確認。１枚でもサイズオーバーがあれば画像入力は失敗する
    	boolean sizeok = true;
        if (value != null) {
        	for (MultipartFile multipartFile:value) {
        		if ( multipartFile.getSize()> localinfo.getFileSize()*1024*1024) { // 1MBが制限   //multipartFile.getSize()==0 ||
        			sizeok = false;
        			break;
        		}
        	}
        }
        return sizeok;

    }
}