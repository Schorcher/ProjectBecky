package com.becky.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    private static final List<String> USED_TOKENS = new ArrayList<>();

    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String generateRandomAuthString() {
        final StringBuilder builder = new StringBuilder();
        for(int i = 0; i < 19; i++) {
            if(i % 5 == 4) {
                builder.append('-');
            }
            else {
                final int rnd = (int)(Math.random() * 62);
                builder.append(CHARACTERS.charAt(rnd));
            }
        }
        return builder.toString();
    }

    public static String generateUniqueAuthenticationString() {
        String token = generateRandomAuthString();
        while(USED_TOKENS.contains(token)) {
            token = generateRandomAuthString();
        }
        return token;
    }

    private static final String[] GENERIC_NAMES = { "unnamed", "Bob", "Tank", "WarFighter", "GenericSuperhero",
        "SomeGuyOnAHorse", "KimJongUn", "NickelbackFan", "Bryan", "John", "LeeroyJenkins"};
    public static String generateRandomUsername() {
        return GENERIC_NAMES[(int)(Math.random() * GENERIC_NAMES.length)] + (int)(Math.random() * 999);
    }
}
