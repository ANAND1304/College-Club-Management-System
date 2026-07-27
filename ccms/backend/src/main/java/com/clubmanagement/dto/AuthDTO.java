package com.clubmanagement.dto;

import com.clubmanagement.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDTO {

    public static class RegisterRequest {
        @NotBlank(message = "Name is required")
        private String name;
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;
        private String department;
        private String phone;
        private String role;
        private String adminSecret;

        public String getName()        { return name; }
        public String getEmail()       { return email; }
        public String getPassword()    { return password; }
        public String getDepartment()  { return department; }
        public String getPhone()       { return phone; }
        public String getRole()        { return role; }
        public String getAdminSecret() { return adminSecret; }
        public void setName(String v)        { this.name = v; }
        public void setEmail(String v)       { this.email = v; }
        public void setPassword(String v)    { this.password = v; }
        public void setDepartment(String v)  { this.department = v; }
        public void setPhone(String v)       { this.phone = v; }
        public void setRole(String v)        { this.role = v; }
        public void setAdminSecret(String v) { this.adminSecret = v; }
    }

    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;
        @NotBlank(message = "Password is required")
        private String password;

        public String getEmail()    { return email; }
        public String getPassword() { return password; }
        public void setEmail(String v)    { this.email = v; }
        public void setPassword(String v) { this.password = v; }
    }

    public static class AuthResponse {
        private String token;
        private String type = "Bearer";
        private Long id;
        private String name;
        private String email;
        private User.Role role;

        public AuthResponse(String token, Long id, String name, String email, User.Role role) {
            this.token = token;
            this.id = id;
            this.name = name;
            this.email = email;
            this.role = role;
        }

        public String getToken()    { return token; }
        public String getType()     { return type; }
        public Long getId()         { return id; }
        public String getName()     { return name; }
        public String getEmail()    { return email; }
        public User.Role getRole()  { return role; }
    }
}
