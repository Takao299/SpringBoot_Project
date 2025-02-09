package com.example.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import lombok.Data;

@Entity
@Data
public class BusinessHour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String dayName;

	private String isOpen;

	@NotNull(message = "未入力")
	@Range(min = 0, max = 23 )
	private Integer openTime;

	@NotNull(message = "未入力")
	@Range(min = 0, max = 23 )
	private Integer closeTime;

    //@Version
    //private Integer version;

    private LocalDateTime updateDateTime;

}
