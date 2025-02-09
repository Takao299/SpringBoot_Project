package com.example.data;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.validator.NewPassCheck;

import lombok.Data;

@Data
@NewPassCheck
public class NewPassword {

	private Long id;
	private String typePass2;
	private String update2;

    public NewPassword() {
    	this.id = null;
        this.typePass2 = null;
        this.update2 = "false";
    }

    @Autowired
    public NewPassword(Long id) {
        this.id = id;
        this.typePass2 = null;
        this.update2 = "false";
    }
}
