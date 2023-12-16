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
public class Meteo {
    private int id;
    private double luminosite;
    private Time heuredebut;
    private Time heurefin;
    private Date date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLuminosite() {
        return luminosite;
    }

    public void setLuminosite(double luminosite) {
        this.luminosite = luminosite;
    }

    public Time getHeuredebut() {
        return heuredebut;
    }

    public void setHeuredebut(Time heuredebut) {
        this.heuredebut = heuredebut;
    }

    public Time getHeurefin() {
        return heurefin;
    }

    public void setHeurefin(Time heurefin) {
        this.heurefin = heurefin;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    public Meteo() {
    }
    public Meteo[] getMatin(Date date,Connection con)throws Exception{
        Meteo[] list_Meteo = null;
        String sql = "select * from meteo where date=? and heurefin<='12:00'";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setDate(1, date);
            ResultSet rs = stmt.executeQuery();
            int counteur = 0;
            while(rs.next()){
                counteur++;
            }
            list_Meteo = new Meteo[counteur];
            rs = stmt.executeQuery();
            int index = 0;
            while(rs.next()){
                Meteo meteo = new Meteo();
                meteo.setId(rs.getInt("id"));
                meteo.setLuminosite(rs.getDouble("luminosite"));
                meteo.setHeuredebut(rs.getTime("heuredebut"));
                meteo.setHeurefin(rs.getTime("heurefin"));
                meteo.setDate(rs.getDate("date"));
                list_Meteo[index] = meteo;
                index++;
            }
        }catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return list_Meteo;
    }
    public Meteo[] getAprem(Date date,Connection con)throws Exception{
        Meteo[] list_Meteo = null;
        String sql = "select * from meteo where date=? and heurefin > '12:00'";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setDate(1, date);
            ResultSet rs = stmt.executeQuery();
            int counteur = 0;
            while(rs.next()){
                counteur++;
            }
            list_Meteo = new Meteo[counteur];
            rs = stmt.executeQuery();
            int index = 0;
            while(rs.next()){
                Meteo meteo = new Meteo();
                meteo.setId(rs.getInt("id"));
                meteo.setLuminosite(rs.getDouble("luminosite"));
                meteo.setHeuredebut(rs.getTime("heuredebut"));
                meteo.setHeurefin(rs.getTime("heurefin"));
                meteo.setDate(rs.getDate("date"));
                list_Meteo[index] = meteo;
                index++;
            }
        }catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return list_Meteo;
    }
    public Meteo[] getDay(Date date,Connection con)throws Exception{
        Meteo[] matin = getMatin(date, con);
        Meteo[] aprem = getAprem(date, con);
        int length = matin.length + aprem.length;
        Meteo[] day = new Meteo[length];
        System.arraycopy(matin, 0, day, 0, matin.length);
        System.arraycopy(aprem, 0, day, matin.length, aprem.length);
        return day;
    }
    //get list date avant date données dans le table meteo
    public Date[] getListDate_avant(Date date,Connection con)throws Exception{
        Date[] list_date = null;
        String sql = "select * from v_listdate_avant where date < ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setDate(1, date);
            ResultSet rs = stmt.executeQuery();
            int counteur = 0;
            while(rs.next()){
                counteur++;
            }
            list_date = new Date[counteur];
            rs = stmt.executeQuery();
            int index = 0;
            while(rs.next()){
                Date date_exist = rs.getDate("date");
                list_date[index] = date_exist;
                index++;
            }
        }catch(Exception e){
            throw e;
        }
        return list_date;
    }
    public static void main(String args[])throws Exception{
        Connection con = Connexion.getconnection();
        Date dd = Date.valueOf("2023-12-01");
        Panneau pan = new Panneau().getById("PAN001", con);
        Meteo me = new Meteo();
        /*Meteo[] oneday = me.getDay(dd, con);
        for(int i=0 ; i < oneday.length ; i++){
            System.out.println("Date "+oneday[i].getDate()+" Luminosite : "+oneday[i].getLuminosite()+" Heuredebut : "
            +oneday[i].getHeuredebut()+" Heurefin :"+oneday[i].getHeurefin()+" Date : "+oneday[i].getDate());
        }*/
        Date[] list_date = me.getListDate_avant(dd, con);
        for(int i=0 ; i < list_date.length ; i++){
            System.out.println(list_date[i]);
        }
        con.close();
    }
}
