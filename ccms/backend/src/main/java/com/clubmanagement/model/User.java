package com.clubmanagement.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.STUDENT;

    private String department;
    private String phone;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Membership> memberships = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (role == null) role = Role.STUDENT;
    }

    public User() {}

    // Builder pattern manual
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final User user = new User();
        public Builder name(String v)       { user.name = v; return this; }
        public Builder email(String v)      { user.email = v; return this; }
        public Builder password(String v)   { user.password = v; return this; }
        public Builder role(Role v)         { user.role = v; return this; }
        public Builder department(String v) { user.department = v; return this; }
        public Builder phone(String v)      { user.phone = v; return this; }
        public Builder active(boolean v)    { user.active = v; return this; }
        public User build()                 { return user; }
    }

    // Getters
    public Long getId()            { return id; }
    public String getName()        { return name; }
    public String getEmail()       { return email; }
    public String getPassword()    { return password; }
    public Role getRole()          { return role; }
    public String getDepartment()  { return department; }
    public String getPhone()       { return phone; }
    public String getProfileImage(){ return profileImage; }
    public boolean isActive()      { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id)                   { this.id = id; }
    public void setName(String name)             { this.name = name; }
    public void setEmail(String email)           { this.email = email; }
    public void setPassword(String password)     { this.password = password; }
    public void setRole(Role role)               { this.role = role; }
    public void setDepartment(String department) { this.department = department; }
    public void setPhone(String phone)           { this.phone = phone; }
    public void setProfileImage(String p)        { this.profileImage = p; }
    public void setActive(boolean active)        { this.active = active; }
    public void setCreatedAt(LocalDateTime t)    { this.createdAt = t; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    @Override public String getUsername()               { return email; }
    @Override public boolean isAccountNonExpired()      { return true; }
    @Override public boolean isAccountNonLocked()       { return active; }
    @Override public boolean isCredentialsNonExpired()  { return true; }
    @Override public boolean isEnabled()                { return active; }

    public enum Role { ADMIN, CLUB_HEAD, STUDENT }
}
