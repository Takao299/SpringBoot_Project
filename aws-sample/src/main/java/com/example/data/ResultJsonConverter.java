package com.example.data;

import java.util.List;

import lombok.Data;

@Data
public class ResultJsonConverter {

	private String result;

	private List<Error> errors;
    @Data
    public static class Error {

        private String message;

    }
}
