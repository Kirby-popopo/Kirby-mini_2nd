package com.example.Kirby_mini_2nd.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
public class ResponseModel<T> {
    private String message;
    private T obj;

    // Constructor for message and object
    public ResponseModel(String message, T obj) {
        this.message = message;
        this.obj = obj;
    }

    // 메시지와 객체를 모두 만들어준다.
    public static <T> ResponseEntity<ResponseModel> MakeResponse(String message, T obj, HttpStatus state) {
        ResponseModel<T> responseModel = new ResponseModel<>(message, obj);
        return new ResponseEntity<>(responseModel, state); // You can change HttpStatus as needed
    }

    // 객체만 만들어서 리턴해준다.
    public static <T> ResponseEntity<ResponseModel> MakeResponse(T obj, HttpStatus state) {
        ResponseModel<T> responseModel = new ResponseModel<>("", obj);
        return new ResponseEntity<>(responseModel, state); // You can change HttpStatus as needed
    }

    // 메시지만 만들어서 리턴해준다.
    public static <T> ResponseEntity<ResponseModel> MakeResponse(String message, HttpStatus state) {
        ResponseModel<T> responseModel = new ResponseModel<>(message, null);
        return new ResponseEntity<>(responseModel, state); // You can change HttpStatus as needed
    }

    // Getters
    public T getObj() {
        return obj;
    }

    public String getMessage() {
        return message;
    }
}
