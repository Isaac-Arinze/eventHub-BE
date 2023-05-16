package com.decagon.eventhubbe.controller;

import com.decagon.eventhubbe.dto.request.ResetPasswordRequest;
import com.decagon.eventhubbe.dto.response.APIResponse;
import com.decagon.eventhubbe.dto.response.EventResponse;
import com.decagon.eventhubbe.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/password")
@RequiredArgsConstructor
public class PasswordController {
    private final PasswordService passwordService;
    @PostMapping("/reset")
    public ResponseEntity<APIResponse<String>> resetPassword(@RequestBody ResetPasswordRequest request){
        APIResponse<String> apiResponse = new APIResponse<>(passwordService.resetPassword(request));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}