package com.example.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PermissionHour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	private Integer nonReservableTime;

	private Integer nonCancellableTime;
}
