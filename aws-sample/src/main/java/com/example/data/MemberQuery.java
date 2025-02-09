package com.example.data;

import lombok.Data;

@Data
public class MemberQuery {

    private Long id;

    private String email;

	//@Size(max=10)
    private String name;

    public MemberQuery() {
    	//id = 1L;
    	name = "";
    }

}
