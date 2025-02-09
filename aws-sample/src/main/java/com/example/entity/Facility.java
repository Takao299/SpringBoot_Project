package com.example.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "施設名を入力して下さい")
    @Size(min = 2, max = 20, message = "2～20文字以内")
    private String name;

    @NotNull(message = "数量を入力して下さい")
    @Range(min = 1, max = 99, message = "1～99までの数値")
    private Integer amount = 0; //intからIntegerに変更後、確認不足か不明

    private String memo;

	private LocalDateTime deleteDateTime;
	private String deleteUser;
	private LocalDateTime updateDateTime;
	private String updateUser;
	private LocalDateTime createDateTime;
	private String createUser;

	@Version
    private Integer version;
}
