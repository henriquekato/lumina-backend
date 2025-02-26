package com.luminabackend.models.user;

public enum Role {
    ADMIN,
    PROFESSOR,
    STUDENT;

    public static Role getRoleFromString(String role){
        return Role.valueOf(role.toUpperCase());
    }
}
