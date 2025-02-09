package com.example.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AuthBusiness {
   EDIT_BUSINESS(AuthType.list[0]),
   VIEW_BUSINESS(AuthType.list[1]),
   NO_BUSINESS(AuthType.list[2]),
   ;

	private final String authName;
}

