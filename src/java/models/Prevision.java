/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.sql.Time;

/**
 *
 * @author Tafitasoa-P15B-140
 */
public class Prevision {
    private Secteur secteur;
    private Time heure_coupure = Time.valueOf("00:00:00");
    private String etat = "non coupure";

    public Secteur getSecteur() {
        return secteur;
    }

    public void setSecteur(Secteur secteur) {
        this.secteur = secteur;
    }

    public Time getHeure_coupure() {
        return heure_coupure;
    }

    public void setHeure_coupure(Time heure_coupure) {
        this.heure_coupure = heure_coupure;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }
    
    public Prevision() {
    }
    
}
