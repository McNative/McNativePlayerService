package org.mcnative.service.player.util;

import io.github.cdimascio.dotenv.Dotenv;

public class Environment {

    public static final Dotenv DOTENV = Dotenv.configure().ignoreIfMissing().load();

    public static String getVariable(String name) {
        String value = getVariableOrNull(name);
        if(value == null) throw new IllegalArgumentException("Can't load environment variable " + name);
        return value;
    }

    public static String getVariableOrNull(String name) {
        if (System.getenv(name) != null) {
            return System.getenv(name);
        } else if (DOTENV.get(name) != null) {
            return DOTENV.get(name);
        }
        return null;
    }

    public static String getVariable(String name,String default0) {
        String value = getVariableOrNull(name);
        if(value == null) return default0;
        return value;
    }

}
