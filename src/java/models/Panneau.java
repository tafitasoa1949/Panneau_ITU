/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Tafitasoa-P15B-140
 */
public class Panneau {
    private String id;
    private String nom;
    private double puissance;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getPuissance() {
        return puissance;
    }

    public void setPuissance(double puissance) {
        this.puissance = puissance;
    }

    public Panneau() {
    }
    public Panneau getById(String id,Connection con)throws Exception{
        String sql = "select * from panneau where id=?";
        Panneau panneau = null;
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                panneau = new Panneau();
                panneau.setId(id);
                panneau.setNom(rs.getString("nom"));
                panneau.setPuissance(rs.getDouble("puissance"));
            }
        }catch (Exception e) {
            throw new Exception("Erreur lors de la récupération du panneau : " + e.getMessage());
        }
        return panneau;
    }
    public static void main(String args[])throws Exception{
        Connection con = Connexion.getconnection();
        Panneau panneau = new Panneau().getById("PAN002", con);
        System.out.println(panneau.getPuissance());
        con.close();
    }
}
