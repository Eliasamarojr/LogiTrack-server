package com.logitrack.dto.auth;

public class MeResponseDTO {

    private String username;

    public MeResponseDTO() {
    }

    public MeResponseDTO(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
