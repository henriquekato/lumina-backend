package com.luminabackend.models.user;

import java.util.ArrayList;
import java.util.List;

public enum Role {
    ADMIN,
    PROFESSOR,
    STUDENT;

    public static Role getRoleFromString(String role){
        return Role.valueOf(role.toUpperCase());
    }

    public static List<Role> parseRoles(List<String> roles){
        if (roles == null) return new ArrayList<>();
        return roles.stream().map(Role::getRoleFromString).toList();
    }
}
