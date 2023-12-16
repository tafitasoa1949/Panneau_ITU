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
public class Batterie {
    private String id;
    private String nom;
    private double puissance;
    private double pourcentage;
    
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

    public double getPourcentage() {
        return pourcentage;
    }

    public void setPourcentage(double pourcentage) {
        this.pourcentage = pourcentage;
    }
    
    public Batterie() {
    }
    public Batterie getById(String id,Connection con)throws Exception{
        String sql = "select * from batterie where id=?";
        Batterie batterie = null;
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                batterie = new Batterie();
                batterie.setId(id);
                batterie.setNom(rs.getString("nom"));
                batterie.setPuissance(rs.getDouble("puissance"));
                batterie.setPourcentage(rs.getDouble("pourcentage"));
            }
        }catch (Exception e) {
            throw new Exception("Erreur lors de la récupération du batterie : " + e.getMessage());
        }
        return batterie;
    }
    public double resteBatterieAvant50(double etat_initial_bat,double bat_avant_coupure){
        double reste_bat = 0;
        double moitie = etat_initial_bat / 2;
        reste_bat = bat_avant_coupure - moitie;
        return reste_bat;
    }
    public double getResteMinute(double etat_initial_bat,double bat_avant_coupure,double bat_coupure){
        double reste_minute = 0;
        double decale_watt = bat_avant_coupure - bat_coupure;
        double moitie_etat = etat_initial_bat * 0.5;
        double reste_watt = bat_avant_coupure - moitie_etat;
        reste_minute = (reste_watt * 60) / (decale_watt);
        //System.out.println("plus minute "+reste_minute);
        return reste_minute;
    }
    public double getRestePourcentage(double etat_initial_bat,double reste_bat){
        double reste_pourcentage = 0;
        reste_pourcentage = (reste_bat * 100) / (etat_initial_bat);
        return reste_pourcentage;
    }
    public static void main(String args[])throws Exception{
        Connection con = Connexion.getconnection();
        Batterie batterie = new Batterie().getById("BAT001", con);
        double reste = batterie.resteBatterieAvant50(19200,11730);
        //double reste_minute = batterie.getResteMinute(batterie.getPuissance(), 20200, 14500);
        //System.out.println(reste_minute);
        System.out.println("Reste batt "+reste);
        con.close();
    }
}
