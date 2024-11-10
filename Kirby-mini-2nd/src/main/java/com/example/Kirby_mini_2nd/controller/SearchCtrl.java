package com.example.Kirby_mini_2nd.controller;

import com.example.Kirby_mini_2nd.model.ResponseModel;
import com.example.Kirby_mini_2nd.service.SearchSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class SearchCtrl {

    SearchSvc searchSvc;

    @Autowired
    public SearchCtrl(SearchSvc searchSvc){
        this.searchSvc = searchSvc;
    }

    @PostMapping("/Search")
    public ResponseEntity<ResponseModel> SearchUser(@RequestBody Map<String, String> requestData){
        String userId = requestData.get("userId");
        try {
            return ResponseModel.MakeResponse(searchSvc.SearchUser(userId), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseModel.MakeResponse("SearchErr",  HttpStatus.OK);
        }
    }
}