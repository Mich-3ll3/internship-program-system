package mx.uv.internshipprogramsystem.test;

import mx.uv.internshipprogramsystem.logic.security.SecurityManager;

public class TestHash {

    public static void main(String[] args) {
        SecurityManager securityManager =
            new SecurityManager();

        String hash =
            securityManager.hashPassword("UnaContra33$");

        System.out.println(hash);
    }
}