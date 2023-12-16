/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 *
 * @author Tafitasoa-P15B-140
 */
public class Resultat {
    private int id;
    private Secteur secteur;
    private double consom_mp;
    private Date date;
    private String nom_jour;

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

    public double getConsom_mp() {
        return consom_mp;
    }

    public void setConsom_mp(double consom_mp) {
        this.consom_mp = consom_mp;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNom_jour() {
        return nom_jour;
    }

    public void setNom_jour(String nom_jour) {
        this.nom_jour = nom_jour;
    }
    
    public Resultat() {
    }
    public void inserer(Connection con) throws Exception{
        String sql = "INSERT INTO resultat (idsecteur, consom_mp, date, nom_jour) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, this.getSecteur().getId());
            stmt.setDouble(2, this.getConsom_mp());
            stmt.setDate(3, this.getDate());
            stmt.setString(4, this.getNom_jour());
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Erreur lors de l'insertion d'un resultat : " + e.getMessage());
        }
    }
    public static String getNom_JourByday(Date date){
        java.util.Date utilDate = new java.util.Date(date.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String nomJour = sdf.format(utilDate);
        //System.out.println("Le jour est : " + nomJour);
        return nomJour;
    }
    public static void main(String args[])throws Exception{
        Connection con = Connexion.getconnection();
        Date date = Date.valueOf("2023-12-04");
        Resultat re = new Resultat();
        System.out.println(re.getNom_JourByday(date));
        con.close();
        
    }
}
