package com.clubmanagement.controller;

import com.clubmanagement.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    /** GET /api/admin/dashboard — system-wide stats */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    /** GET /api/admin/users — list all users */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /** GET /api/admin/memberships/pending */
    @GetMapping("/memberships/pending")
    public ResponseEntity<?> getPendingMemberships() {
        return ResponseEntity.ok(adminService.getPendingMemberships());
    }

    /** PUT /api/admin/memberships/{id}/approve */
    @PutMapping("/memberships/{id}/approve")
    public ResponseEntity<String> approveMembership(@PathVariable Long id) {
        adminService.approveMembership(id);
        return ResponseEntity.ok("Membership approved successfully");
    }

    /** PUT /api/admin/memberships/{id}/reject */
    @PutMapping("/memberships/{id}/reject")
    public ResponseEntity<String> rejectMembership(@PathVariable Long id) {
        adminService.rejectMembership(id);
        return ResponseEntity.ok("Membership rejected");
    }

    /** PUT /api/admin/users/{id}/toggle — enable/disable a user */
    @PutMapping("/users/{id}/toggle")
    public ResponseEntity<String> toggleUserStatus(@PathVariable Long id) {
        adminService.toggleUserStatus(id);
        return ResponseEntity.ok("User status toggled");
    }

    /** PUT /api/admin/users/{id}/role?role=CLUB_HEAD — change role */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<String> promoteUser(@PathVariable Long id, @RequestParam String role) {
        adminService.promoteUser(id, role);
        return ResponseEntity.ok("User role updated to " + role);
    }
}
