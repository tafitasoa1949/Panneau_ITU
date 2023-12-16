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
import java.time.LocalDate;
import java.util.Vector;

/**
 *
 * @author Tafitasoa-P15B-140
 */
public class Presence {
    private int id;
    private Salle salle;
    private int nombreEleve;
    private double consommation;
    private Time debut;
    private Time fin;
    private Date date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    public int getNombreEleve() {
        return nombreEleve;
    }

    public void setNombreEleve(int nombreEleve) {
        this.nombreEleve = nombreEleve;
    }

    public double getConsommation() {
        return consommation;
    }

    public void setConsommation(double consommation) {
        this.consommation = consommation;
    }

    public Time getDebut() {
        return debut;
    }

    public void setDebut(Time debut) {
        this.debut = debut;
    }

    public Time getFin() {
        return fin;
    }

    public void setFin(Time fin) {
        this.fin = fin;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    public Presence() {
    }
    public Presence getMatin(Date date,Connection con)throws Exception{
        String sql = " select * from presence where idsalle=? and date=? and fin <= '12:00'";
        Presence presence = null;
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, this.getSalle().getId());
            stmt.setDate(2, date);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                presence = new Presence();
                presence.setId(rs.getInt("id"));
                presence.setSalle(this.getSalle());
                presence.setNombreEleve(rs.getInt("nbre_eleve"));
                presence.setConsommation(rs.getDouble("consommation"));
                presence.setDebut(rs.getTime("debut"));
                presence.setFin(rs.getTime("fin"));
                presence.setDate(rs.getDate("date"));
            }
        }catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return presence;
    }
    public Presence getAprem(Date date,Connection con)throws Exception{
        String sql = " select * from presence where idsalle=? and date=? and fin > '12:00'";
        Presence presence = null;
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, this.getSalle().getId());
            stmt.setDate(2, date);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                presence = new Presence();
                presence.setId(rs.getInt("id"));
                presence.setSalle(this.getSalle());
                presence.setNombreEleve(rs.getInt("nbre_eleve"));
                presence.setConsommation(rs.getDouble("consommation"));
                presence.setDebut(rs.getTime("debut"));
                presence.setFin(rs.getTime("fin"));
                presence.setDate(rs.getDate("date"));
            }
        }catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return presence;
    }
    public Presence[] getDay(Date date,Connection con)throws Exception{
        Presence matin = this.getMatin(date, con);
        Presence aprem = this.getAprem(date, con);
        Presence[] day = new Presence[2];
        day[0] = matin;
        day[1] = aprem;
        return day;
    }
    
    //recupure tous le presence avant la date donnée
    public static Presence[] getAll_Presence_Avant_Date(Date date,Connection con)throws Exception{
        Presence[] list_presence = null;
        String sql = "select * from presence where date < ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setDate(1, date);
            ResultSet rs = stmt.executeQuery();
            int counteur = 0;
            while(rs.next()){
                counteur++;
            }
            list_presence = new Presence[counteur];
            rs = stmt.executeQuery();
            int index = 0;
            while(rs.next()){
                Presence presence = new Presence();
                presence.setId(rs.getInt("id"));
                String idSalle = rs.getString("idsalle");
                Salle salle = new Salle().getById(idSalle, con);
                presence.setSalle(salle);
                presence.setNombreEleve(rs.getInt("nbre_eleve"));
                presence.setConsommation(rs.getDouble("consommation"));
                presence.setDebut(rs.getTime("debut"));
                presence.setFin(rs.getTime("fin"));
                presence.setDate(rs.getDate("date"));
                list_presence[index] = presence;
                index++;
            }
        }catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return list_presence;
    }
    public static Vector<Presence> getResultat_MemeJour(Date date,Connection con)throws Exception{
        String NomJourDonne = Resultat.getNom_JourByday(date);
        Vector<Presence> listMemeJour = new Vector<Presence>();
        Presence[] list_avant = getAll_Presence_Avant_Date(date, con);
        LocalDate givenDate = date.toLocalDate();
        for (Presence presence : list_avant) {
            String nomJour = Resultat.getNom_JourByday(presence.getDate());
            if (presence.getDate() != null) {
                LocalDate presenceDate = presence.getDate().toLocalDate();
                // Vérifier si les LocalDate sont égaux (même jour)
                if (nomJour.equals(NomJourDonne)) { 
                    listMemeJour.add(presence);
                }
            }
        }
        return listMemeJour;
    }
    public static Vector<Presence> getMemeJourMatin(Date date,Connection con)throws Exception{
        Vector<Presence> listMemeJourMatin = new Vector<Presence>();
        Vector<Presence> list_MemeJour = getResultat_MemeJour(date, con);
        for (Presence presence : list_MemeJour) {
            if (presence.getFin() != null && presence.getFin().before(Time.valueOf("12:00:00"))) {
                listMemeJourMatin.add(presence);
            }
        }
        return listMemeJourMatin;
    }
    public static Vector<Presence> getMemeJourAprem(Date date,Connection con)throws Exception{
        Vector<Presence> listMemeJourMatin = new Vector<Presence>();
        Vector<Presence> list_MemeJour = getResultat_MemeJour(date, con);
        for (Presence presence : list_MemeJour) {
            if (presence.getFin() != null && presence.getFin().after(Time.valueOf("12:00:00"))) {
                listMemeJourMatin.add(presence);
            }
        }
        return listMemeJourMatin;
    }
    /*public Vector<String> getIdsalleDistinct(Date date,Connection con)throws Exception{
        Vector<String> list = new Vector<String>();
        Vector<Presence> listPresenceMatin = getMemeJourMatin(date,con);
        list.add(listPresenceMatin.get(0).getSalle().getId());
        for(int i=1 ; i <listPresenceMatin.size() ; i++ ){
            if(list.get(0).equals(listPresenceMatin.get(i))){
                list.add(e)
            }
        }
        return list;
    }*/
    public double getMoyenneMemeJour(Date date,Connection con)throws Exception{
        double moyenne = 0;
        double somme_matin = 0;
        Vector<Presence> listPresenceMatin = getMemeJourMatin(date,con);
        for (Presence presence : listPresenceMatin) {
            somme_matin += presence.getNombreEleve();
        }
        return moyenne;
    }
    //maka moyene mpianatr am donnée taloh fa meme jour am iny aby
    //indice 0 maraina
    //indice 1 aprem
    public static double[] getNbr_moyenne_Mp(Date date,Connection  con)throws Exception{
        double[] moyenne = new double[2];
        double moyenne_matin = 0;
        double moyenne_aprem = 0;
        double somme_matin = 0;
        double somme_aprem = 0;
        Vector<Presence> listPresenceMatin = getMemeJourMatin(date,con);
        for (Presence presence : listPresenceMatin) {
            somme_matin += presence.getNombreEleve();
        }
        moyenne_matin = somme_matin / listPresenceMatin.size();
        double arrodise_matin = Math.ceil(moyenne_matin);
        Vector<Presence> listPresenceAprem = getMemeJourAprem(date,con);
        for (Presence presence : listPresenceAprem) {
            somme_aprem += presence.getNombreEleve();
        }
        moyenne_aprem = somme_aprem / listPresenceAprem.size();
        double arrodise_aprem = Math.ceil(moyenne_aprem);
        moyenne[0] = arrodise_matin;
        moyenne[1] = arrodise_aprem;
        return moyenne;
    }
    public static void main(String args[]) throws Exception{
        Connection con = Connexion.getconnection();
        Salle sal = new Salle().getById("SAL001", con);
        Presence presence = new Presence();
        presence.setSalle(sal);
        Date date = Date.valueOf("2023-12-11");
        Presence[] pp = presence.getAll_Presence_Avant_Date(date, con);
        for(int i=0 ; i < pp.length ; i++){
            System.out.println("id "+pp[i].getId()+" date "+pp[i].getDate()+ "Jour "+Resultat.getNom_JourByday(pp[i].getDate()));
        }
        /*System.out.println("donnne date "+date+" jour "+Resultat.getNom_JourByday(date));
        System.out.println("le meme sont ");
        Vector<Presence> memejour = presence.getResultat_MemeJour(date, con);
        for(int i=0 ; i < memejour.size() ; i++){
            System.out.println("id "+memejour.get(i).getId()+" date "+memejour.get(i).getDate()+ "Jour "+Resultat.getNom_JourByday(memejour.get(i).getDate()));
        }
        System.out.println("Matin");
        Vector<Presence> matin = presence.getMemeJourMatin(date, con);
        for(int i=0 ; i < matin.size() ; i++){
            System.out.println("id "+matin.get(i).getId());
        }
        System.out.println("Aprem");
        Vector<Presence> aprem = presence.getMemeJourAprem(date, con);
        for(int i=0 ; i < aprem.size() ; i++){
            System.out.println("id "+aprem.get(i).getId());
        }*/
        double[] moyenne = getNbr_moyenne_Mp(date, con);
        System.out.println("Matin moy= "+moyenne[0]+" Aprem moy= "+moyenne[1]);
    }
}
