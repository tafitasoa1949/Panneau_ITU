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
public class Salle {
    private String id;
    private String nom;
    private Secteur secteur;

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

    public Secteur getSecteur() {
        return secteur;
    }

    public void setSecteur(Secteur secteur) {
        this.secteur = secteur;
    }
    
    public Salle() {
    }
    public Salle getById(String id,Connection con)throws Exception{
        String sql = "select * from salle where id=?";
        Salle salle = null;
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                salle = new Salle();
                salle.setId(id);
                salle.setNom(rs.getString("nom"));
                String idSecteur = rs.getString("idsecteur");
                Secteur secteur = new Secteur().getById(idSecteur, con);
                salle.setSecteur(secteur);
            }
        }catch (Exception e) {
            throw new Exception("Erreur lors de la récupération du salle : " + e.getMessage());
        }
        return salle;
    }
    public static Salle[] getBySecteur(Secteur secteur,Connection con)throws Exception{
        String sql = "select * from salle where idsecteur =?";
        Salle[] list_salle = null;
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, secteur.getId());
            ResultSet rs = stmt.executeQuery();
            int counteur = 0;
            while(rs.next()){
                counteur++;
            }
            list_salle = new Salle[counteur];
            rs = stmt.executeQuery();
            int index = 0;
            while(rs.next()){
                Salle salle = new Salle();
                salle.setId(rs.getString("id"));
                salle.setNom(rs.getString("nom"));
                salle.setSecteur(secteur);
                list_salle[index] = salle;
                index++;
            }
        }catch (Exception e) {
            //e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return list_salle;
    }
    public static void main(String args[])throws Exception{
        Connection con = Connexion.getconnection();
        Secteur secteur = new Secteur().getById("SEC001", con);
        Salle[] list = new Salle().getBySecteur(secteur, con);
        for(int i=0 ; i < list.length ; i++){
            System.out.println(list[i].getNom());
        }
        con.close();
    }
}
