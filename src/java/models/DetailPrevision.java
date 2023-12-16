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
public class DetailPrevision {
    private double nombre_eleve;
    private double besoin;
    private Time heureDebut;
    private Time heureFin;
    private double consommation;
    private double pourcentage_lum;
    private double luminosite;
    private Secteur secteur;
    private double pourcentage_bat;
    private double batterie;
    private String etat = "non coupure";

    public double getNombre_eleve() {
        return nombre_eleve;
    }

    public void setNombre_eleve(double nombre_eleve) {
        this.nombre_eleve = nombre_eleve;
    }

    public double getBesoin() {
        return besoin;
    }

    public void setBesoin(double besoin) {
        this.besoin = besoin;
    }
    
    public Time getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(Time heureDebut) {
        this.heureDebut = heureDebut;
    }

    public Time getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(Time heureFin) {
        this.heureFin = heureFin;
    }

    public double getConsommation() {
        return consommation;
    }

    public void setConsommation(double consommation) {
        this.consommation = consommation;
    }

    public double getPourcentage_lum() {
        return pourcentage_lum;
    }

    public void setPourcentage_lum(double pourcentage_lum) {
        this.pourcentage_lum = pourcentage_lum;
    }
    
    public double getLuminosite() {
        return luminosite;
    }

    public void setLuminosite(double luminosite) {
        this.luminosite = luminosite;
    }

    public Secteur getSecteur() {
        return secteur;
    }

    public void setSecteur(Secteur secteur) {
        this.secteur = secteur;
    }

    public double getPourcentage_bat() {
        return pourcentage_bat;
    }

    public void setPourcentage_bat(double pourcentage_bat) {
        this.pourcentage_bat = pourcentage_bat;
    }

    public double getBatterie() {
        return batterie;
    }

    public void setBatterie(double batterie) {
        this.batterie = batterie;
    }
    
    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }
    
    public DetailPrevision() {
    }
    
}
