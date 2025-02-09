package com.example.entity;



import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.example.util.AuthAdminUser;
import com.example.util.AuthBusiness;
import com.example.util.AuthFacility;
import com.example.util.AuthMember;
import com.example.util.AuthReservation;
import com.example.validator.UniqueLogin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name="admin_user") //エンティティ名AdminUserは自動的にテーブル名admin_userになる模様（postgresの仕様？中の大文字はアンダーバー+小文字になる。カラムも同様）
@Entity
@UniqueLogin
public class AdminUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 2, max = 20)
    @NotNull(message = "ユーザー名を入力して下さい")
    private String username;

    //@Size(min = 4, max = 255)
    //@NotNull
    private String password;	//バリデーションはNewPassword.javaの@NewPassCheckに任せる

    @NotNull
    private AuthAdminUser authAdminUser;

    @NotNull
    private AuthMember authMember;

    @NotNull
    private AuthFacility authFacility;

    @NotNull
    private AuthReservation authReservation;

    @NotNull
    private AuthBusiness authBusiness;

    private LocalDateTime deleteDateTime;

    @Version
    private Integer version;
}
