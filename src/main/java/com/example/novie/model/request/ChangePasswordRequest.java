package com.example.novie.model.request;


import lombok.Getter;

@Getter
public class ChangePasswordRequest {

    private String newPass;
    private String oldPass;
}
