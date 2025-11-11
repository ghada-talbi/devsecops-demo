package com.demo;

import java.sql.*;

public class SecurityIssues {
    
    // ❌ SECRET: Mot de passe en dur
    private String dbPassword = "supersecret123";
    
    // ❌ VULN: Injection SQL possible
    public void getUserData(String username) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db", "user", dbPassword);
            Statement stmt = conn.createStatement();
            
            // ❌ INJECTION SQL
            String query = "SELECT * FROM users WHERE username = '" + username + "'";
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                System.out.println(rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // ❌ VULN: Division par zéro
    public void divideNumbers(int a, int b) {
        int result = a / b;
        System.out.println("Result: " + result);
    }
    
    // ❌ SECRET: Clé API exposée
    private String apiKey = "AKIAIOSFODNN7EXAMPLE";
}
// Test Jenkins trigger - Tue Nov 11 15:01:57 UTC 2025
// Test Jenkins trigger - Tue Nov 11 15:04:37 UTC 2025
// Jenkins Webhook Test - 2025-11-11 15:12:49
