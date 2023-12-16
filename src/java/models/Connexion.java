/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Tafitasoa-P15B-140
 */
public class Connexion {
    public static Connection getconnection() throws Exception{
        Connection con = null;
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/coupure_courant", "postgres", "admin");
        }catch (Exception c){
            c.printStackTrace();
            c.getMessage();
        }
        return con;
    }
}
