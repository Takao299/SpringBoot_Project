package com.example.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import com.example.validator.TimeStartEnd3;
import com.example.validator.UniqueTempDay;

import lombok.Data;

@Entity
@Data
@TimeStartEnd3
public class TemporaryBusiness {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @UniqueTempDay //重複制限(ブラウザバックで入力可能なため作成)
    @NotNull(message = "未入力")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate rday;

	private String isOpen;

	@NotNull(message = "未入力")
	@Range(min = 0, max = 23 )
	private Integer openTime;

	@NotNull(message = "未入力")
	@Range(min = 0, max = 23 )
	private Integer closeTime;

    //@Version
    //private Integer version;

    private LocalDateTime deleteDateTime;


}
