package com.example.data;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.validator.PrePassCheck;

import lombok.Data;

@Data
@PrePassCheck
public class PrePassword {

	private Long id;
	private String typePass;
	private String update;	//typePassの@PrePassCheckバリデーションのためだけのフィールド

    public PrePassword() {
        this.id = null;
        this.typePass = null;
        this.update = "false";
    }

    @Autowired
    public PrePassword(Long id) {
        this.id = id;
        this.typePass = null;
        this.update = "false";
    }
}
