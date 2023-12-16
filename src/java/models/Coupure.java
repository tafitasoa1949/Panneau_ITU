/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;

/**
 *
 * @author Tafitasoa-P15B-140
 */
public class Coupure {
    private int id;
    private Secteur secteur;
    private Date date;
    private Time heure;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Secteur getSecteur() {
        return secteur;
    }

    public void setSecteur(Secteur secteur) {
        this.secteur = secteur;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getHeure() {
        return heure;
    }

    public void setHeure(Time heure) {
        this.heure = heure;
    }

    public Coupure() {
    }
    public static Coupure getBySec_ByDate(Date date,String idsecteur,Connection con)throws Exception{
        Coupure coupure = null;
        String sql = "select * from coupure where date=? and idsecteur=?";
        Panneau panneau = null;
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setDate(1, date);
            stmt.setString(2, idsecteur);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                coupure = new Coupure();
                coupure.setId(rs.getInt("id"));
                Secteur secteur = new Secteur().getById(idsecteur, con);
                coupure.setSecteur(secteur);
                coupure.setDate(date);
                coupure.setHeure(rs.getTime("heure"));
            }
        }catch (Exception e) {
            throw new Exception("Erreur lors de la récupération du coupure : " + e.getMessage());
        }
        return coupure;
    }
    public static void main(String args[])throws Exception{
        Connection con = Connexion.getconnection();
        Date dd = Date.valueOf("2023-11-27");
        Coupure coupure = Coupure.getBySec_ByDate(dd,"SEC001", con);
        System.out.println(coupure.getHeure());
        con.close();
    }
}
