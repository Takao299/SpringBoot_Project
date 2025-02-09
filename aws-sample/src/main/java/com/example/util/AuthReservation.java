package com.example.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AuthReservation {
   EDIT_RESERVATION(AuthType.list[0]),
   VIEW_RESERVATION(AuthType.list[1]),
   NO_RESERVATION(AuthType.list[2]),
   ;

	private final String authName;
}

