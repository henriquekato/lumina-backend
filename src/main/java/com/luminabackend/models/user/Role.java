package com.luminabackend.models.user;

import java.util.*;

public enum Role {
    ADMIN,
    PROFESSOR,
    STUDENT;

    public static Optional<Role> getRoleFromString(String role){
        return Arrays.stream(Role.values())
                .filter(r -> r.toString().equalsIgnoreCase(role))
                .findFirst();
    }

    public static List<Role> parseRoles(List<String> roles){
        if (roles == null) return new ArrayList<>();
        return roles.stream()
                .map(Role::getRoleFromString)
                .flatMap(Optional::stream)
                .toList();
    }
}
