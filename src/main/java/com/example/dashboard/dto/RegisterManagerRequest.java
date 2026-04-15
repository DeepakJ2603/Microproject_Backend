package com.example.dashboard.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterManagerRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    public RegisterManagerRequest() {}

    public String getFullName()           { return fullName; }
    public void   setFullName(String f)   { this.fullName = f; }

    public String getEmail()              { return email; }
    public void   setEmail(String e)      { this.email = e; }

    public String getPassword()           { return password; }
    public void   setPassword(String p)   { this.password = p; }
}