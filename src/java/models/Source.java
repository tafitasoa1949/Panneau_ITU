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
public class Source {
    private Secteur secteur;
    private Panneau panneau;
    private Batterie batterie;

    public Secteur getSecteur() {
        return secteur;
    }

    public void setSecteur(Secteur secteur) {
        this.secteur = secteur;
    }

    public Panneau getPanneau() {
        return panneau;
    }

    public void setPanneau(Panneau panneau) {
        this.panneau = panneau;
    }

    public Batterie getBatterie() {
        return batterie;
    }

    public void setBatterie(Batterie batterie) {
        this.batterie = batterie;
    }
    
    public static Source getBySecteur(Secteur secteur,Connection con)throws Exception{
        Source source = null;
        String sql = "select * from source where idsecteur=?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, secteur.getId());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                source = new Source();
                source.setSecteur(secteur);
                String idPanneau = rs.getString("idpanneau");
                Panneau panneau = new Panneau().getById(idPanneau, con);
                source.setPanneau(panneau);
                String idBatterie = rs.getString("idbatterie");
                Batterie batterie = new Batterie().getById(idBatterie, con);
                source.setBatterie(batterie);
            }
        }catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return source;
    }
    public double getResteMinute(double consommation_Total_Mp,double consommation_misy){
        double reste_minute = 0;
        reste_minute = (consommation_misy * 60)/consommation_Total_Mp;
        return reste_minute;
    }
    public Boolean isCoupure(){
        Boolean result = false;
        double pourcetnage_bat = this.getBatterie().getPourcentage();
        if (pourcetnage_bat <= 50) {
            result = true;
        }
        return result;
    }
    public static void main(String args[]) throws Exception{
        Connection con = Connexion.getconnection();
        Secteur sec = new Secteur().getById("SEC001", con);
        Source source = Source.getBySecteur(sec, con);
        source.getBatterie().setPourcentage(50);
        System.out.println("Panneau :"+source.getPanneau().getNom()+" Bat : "+source.getBatterie().getPuissance());
        double etat_initial = 40000;
        System.out.println(source.isCoupure());
    }
}
