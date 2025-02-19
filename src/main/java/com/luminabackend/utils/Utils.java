package com.luminabackend.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Utils {
    public static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    
    public static String hashPassword(String password) {
        return encoder.encode(password);
    }
}
