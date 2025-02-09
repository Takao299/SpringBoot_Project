package com.example.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AuthFacility {
   EDIT_FACILITY(AuthType.list[0]),
   VIEW_FACILITY(AuthType.list[1]),
   NO_FACILITY(AuthType.list[2]),
   ;

   private final String authName;
}

