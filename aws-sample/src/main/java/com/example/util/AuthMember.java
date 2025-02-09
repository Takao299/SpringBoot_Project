package com.example.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AuthMember {
   EDIT_MEMBER(AuthType.list[0]),
   VIEW_MEMBER(AuthType.list[1]),
   NO_MEMBER(AuthType.list[2]),
   ;

   private final String authName;
}

