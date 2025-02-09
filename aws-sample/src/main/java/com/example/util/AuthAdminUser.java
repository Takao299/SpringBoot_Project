package com.example.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AuthAdminUser {
   EDIT_ADMINUSER(AuthType.list[0]),
   VIEW_ADMINUSER(AuthType.list[1]),
   NO_ADMINUSER(AuthType.list[2]),
   ;

   private final String authName;
}

