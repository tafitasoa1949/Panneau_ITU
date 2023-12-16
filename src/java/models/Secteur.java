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
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Vector;

/**
 *
 * @author Tafitasoa-P15B-140
 */
public class Secteur {
    private String id;
    private String nom;

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

    public Secteur() {
    }
    public static Secteur[] getAll(Connection con)throws Exception{
        Secteur[] list_secteur = null;
        String sql = "select * from secteur";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            int counteur = 0;
            while(rs.next()){
                counteur++;
            }
            list_secteur = new Secteur[counteur];
            rs = stmt.executeQuery();
            int index = 0;
            while(rs.next()){
                Secteur secteur = new Secteur();
                secteur.setId(rs.getString("id"));
                secteur.setNom(rs.getString("nom"));
                list_secteur[index] = secteur;
                index++;
            }
        }catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return list_secteur;
    }
    public Secteur getById(String id,Connection con)throws Exception{
        String sql = "select * from secteur where id=?";
        Secteur secteur = null;
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                secteur = new Secteur();
                secteur.setId(id);
                secteur.setNom(rs.getString("nom"));
            }
        }catch (Exception e) {
            throw new Exception("Erreur lors de la récupération du secteur : " + e.getMessage());
        }
        return secteur;
    }
    //indice 0 matin,indice 1 aprem
    public int[] getNbrEleve(Date date,Connection con)throws Exception{
        int[] nbr_eleve = new int[2];
        int matin = 0;
        int aprem = 0;
        Salle[] salles = Salle.getBySecteur(this, con);
        for (int i = 0; i < salles.length ; i++) {
            Presence pr = new Presence();
            pr.setSalle(salles[i]);
            //
            Presence presence_matin = pr.getMatin(date, con);
            matin += presence_matin.getNombreEleve();
            Presence presence_aprem = pr.getAprem(date, con);
            aprem += presence_aprem.getNombreEleve();
        }
        nbr_eleve[0] = matin;
        nbr_eleve[1] = aprem;
        return nbr_eleve;
    }
    //indice 0 total_consommation matin ,indice 1 aprem
    public double[] getConsom_Total_Heure(double consom_Mp,Date date,Connection con)throws Exception{
        double[] consom_day = new double[2];
        double consom_matin = 0;
        double consom_aprem = 0;
        int[] nbr_eleve = this.getNbrEleve(date, con);
        consom_matin = nbr_eleve[0] * consom_Mp;
        consom_aprem = nbr_eleve[1] * consom_Mp;
        consom_day[0] = consom_matin;
        consom_day[1] = consom_aprem;
        return consom_day;
    }
    //gettime coupure une fois <= 50% batterie
    public Time getTimeCoupure(double consom_Mp,Date date,Connection con)throws Exception{
        Time result = Time.valueOf("00:00:00");
        try {
            //source
            Source source = Source.getBySecteur(this, con);
            double etat_initial_batt = source.getBatterie().getPuissance();
            double reste_batterie_moinsUn = 0;
            //meteo
            Meteo meteo = new Meteo();
            Meteo[] meteo_day = meteo.getDay(date, con);
            //consommation
            double[] consom_Total_Heure = this.getConsom_Total_Heure(consom_Mp,date, con);
            //
            for(int i=0 ; i < meteo_day.length ; i++){
                //
                reste_batterie_moinsUn = source.getBatterie().getPuissance();
                //
                double pourcentage_luminosite = meteo_day[i].getLuminosite() * 10;
                double energie_solaire = (pourcentage_luminosite * source.getPanneau().getPuissance()) / 100;
                double reste = 0;
                double consommation_total_heure = 0;
                if(meteo_day[i].getHeurefin().compareTo(Time.valueOf("12:00:00")) <= 0) {
                    consommation_total_heure = consom_Total_Heure[0];
                    reste = energie_solaire - consom_Total_Heure[0];
                }else{
                    consommation_total_heure = consom_Total_Heure[1];
                    reste = energie_solaire - consom_Total_Heure[1];
                }
                if(reste < 0){
                    double reste_batterie = source.getBatterie().getPuissance() - Math.abs(reste);
                    double moitie_bat = etat_initial_batt / 2;
                    //coupure
                    if(reste_batterie <= moitie_bat){
                        double reste_bat_avant_moitie = source.getBatterie().resteBatterieAvant50(etat_initial_batt, reste_batterie_moinsUn);
                        double consommation = energie_solaire + reste_bat_avant_moitie;
                        double reste_minute = source.getResteMinute(consommation_total_heure, consommation);
                        LocalTime heureCoupureLocalTime = meteo_day[i].getHeuredebut().toLocalTime();
                        LocalTime heure_coupure_precis = heureCoupureLocalTime.plusMinutes((long) reste_minute);
                        Time heure_coupure = Time.valueOf(heure_coupure_precis);
                        source.getBatterie().setPuissance(moitie_bat);
                        source.getBatterie().setPourcentage(50);
                        return heure_coupure;
                    }else{
                        source.getBatterie().setPuissance(reste_batterie);
                        double pourcentage = source.getBatterie().getRestePourcentage(etat_initial_batt, reste_batterie);
                        source.getBatterie().setPourcentage(pourcentage);
                    }
                }
                else{
                    if(source.getBatterie().getPourcentage() < 100){
                        double puissance_bat = source.getBatterie().getPuissance();
                        double new_puissance = puissance_bat + reste;
                        source.getBatterie().setPuissance(new_puissance);
                        double new_pourcentage = source.getBatterie().getRestePourcentage(etat_initial_batt, new_puissance);
                        source.getBatterie().setPourcentage(new_pourcentage);
                    } 
                }
                /*System.out.println("HeureDebut : "+meteo_day[i].getHeuredebut()+" HeureFin : "+meteo_day[i].getHeurefin()+" Luminosite : "+meteo_day[i].getLuminosite()
                +" Pourcentage lum :"+pourcentage_luminosite+" Energie avoka : "+energie_solaire+" Reste tsy ampy "+reste+" Batterie : "
                +source.getBatterie().getPuissance()+" Pourcentage bat : "+source.getBatterie().getPourcentage());*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    //getConsomation_Mp par methode d'iteration 
    public double getConsom_Mp_Iteration_Avec_Tolerances(Date date,Connection con)throws Exception{
        double consommationParEleve = 0.1;
        Coupure coupure = Coupure.getBySec_ByDate(date,this.getId(), con);
        Time heureEstimee = this.getTimeCoupure(consommationParEleve, date, con);
        final double toleranceMinutes = 2;
        while(true){
            long differenceTemps = Math.abs(heureEstimee.getTime() - coupure.getHeure().getTime());
            if (differenceTemps <= toleranceMinutes * 60 * 1000) {
                break;
            }
            consommationParEleve += 0.01;
            heureEstimee = this.getTimeCoupure(consommationParEleve, date, con);
            System.out.println("cons euh "+consommationParEleve);
            System.out.println(heureEstimee);
        }
        System.out.println("Consom_final "+consommationParEleve);
        return consommationParEleve;
    }
    public double getConsom_Mp_Iteration_Sans_Tolerances(Date date, Connection con) throws Exception {
        double consom_Mp = 0.1;
        Coupure coupure = Coupure.getBySec_ByDate(date, this.getId(), con);
        Time heure_estime = this.getTimeCoupure(consom_Mp, date, con);

        while (true) {
            if (heure_estime.equals(coupure.getHeure())) {
                break;
            }
            consom_Mp += 0.1;
            heure_estime = this.getTimeCoupure(consom_Mp, date, con);
            System.out.println(heure_estime);
        }
       System.out.println("Consom_final " + consom_Mp);
       return consom_Mp;   
    }


    //methode par dichotomie
    public double getConsom_Mp_Dichotomie(Date date,Connection con)throws Exception{
        double consom_Mp = 120;
        double moitie = 120;
        Coupure coupure = Coupure.getBySec_ByDate(date, this.getId(), con);
        Time heure_estime = this.getTimeCoupure(consom_Mp, date, con);
        double epsilon = 0.001;
        int maxIterations = 120;
        int iterations = 0;
        while (Math.abs(heure_estime.getTime() - coupure.getHeure().getTime()) > epsilon && iterations < maxIterations) {
            moitie /= 2;
            if (heure_estime.before(coupure.getHeure())) {
                consom_Mp -= moitie;
            } else {
                consom_Mp += moitie;
            }
            heure_estime = this.getTimeCoupure(consom_Mp, date, con);
            iterations++;
        }
        if (iterations >= maxIterations) {
            System.out.println("La limite d'itérations a été atteinte sans convergence.");
            return -1; // Ou une valeur par défaut pour indiquer un échec de convergence
        }
        System.out.println("Consom_final "+consom_Mp);
        return consom_Mp;
    }
    public double getDico(Date date,Connection con)throws Exception{
        double consom_Mp = 0;
        double consommation_bas = 0;
        double consommation_haut = 120;
        double consommation_provisoire = 60;
        double meilleur_consommation = 0;
        long difference_de_minutes_temp = (long) 0;
        LocalTime heure_a_ajuster = LocalTime.of(0,0,0);
        LocalTime heure_null = LocalTime.of(0,0,0);
        LocalTime meilleur_heure = LocalTime.of(0,0,0);
        Coupure coupure = Coupure.getBySec_ByDate(date, this.getId(), con);
        long difference_de_minutes = (long) Math.abs(coupure.getHeure().toLocalTime().until(LocalTime.of(0,0,0), java.time.temporal.ChronoUnit.MINUTES));
        meilleur_heure = heure_a_ajuster;
        meilleur_consommation = consommation_provisoire;
        Time heure_estime = this.getTimeCoupure(consom_Mp, date, con);
        heure_a_ajuster = heure_estime.toLocalTime();
        LocalTime heure_coupure = coupure.getHeure().toLocalTime();
        //System.out.println(heure_estime);
        while(heure_a_ajuster != heure_coupure){
            consommation_provisoire = (consommation_bas+consommation_haut)/2;
            heure_a_ajuster = this.getTimeCoupure(consommation_provisoire, date, con).toLocalTime();
            if(heure_a_ajuster.equals(heure_coupure)){
                meilleur_heure = heure_a_ajuster;
                meilleur_consommation = consommation_provisoire;
                break;
            }
            if (consommation_provisoire == consommation_bas || consommation_provisoire == consommation_haut) {
                break;
            }
            if(heure_a_ajuster.equals(heure_null)){
                System.out.println("consommation provisoire2: " + consommation_provisoire);
                consommation_bas = consommation_provisoire;
                //consommation_provisoire = (consommation_bas+consommation_haut)/2;
                continue;
            }else if(heure_a_ajuster.isBefore(heure_coupure)){
                System.out.println("consommation provisoire1: " + consommation_provisoire);
                System.out.println("consommation provisoire1: " + consommation_provisoire);
                consommation_haut = consommation_provisoire;
                //conso_rollback = consommation_provisoire;
                //consommation_provisoire = (consommation_bas+consommation_haut)/2;
            }else{
                System.out.println("consommation provisoire3: " + consommation_provisoire);
                consommation_bas = consommation_provisoire;
                //conso_rollback = consommation_provisoire;
                //consommation_provisoire = (consommation_bas+consommation_haut)/2;
            }
            //diff_minute = Math.abs(coupure.getHeure().until(heure_estime, java.time.temporal.ChronoUnit.MINUTES));
            difference_de_minutes_temp = Math.abs(heure_coupure.until(heure_a_ajuster, java.time.temporal.ChronoUnit.MINUTES));
            if(meilleur_heure == heure_a_ajuster){
                continue;
            }
            if(difference_de_minutes_temp < difference_de_minutes){
                difference_de_minutes = difference_de_minutes_temp;
                meilleur_heure = heure_a_ajuster;
                meilleur_consommation = (consommation_provisoire);
            }
        }
        consom_Mp = meilleur_consommation;
        return meilleur_consommation;
    }
    //inserer le resultat de estime de consommation pour chaque date de coupure
    public void insertResultat(Date date,Connection con)throws Exception{
        double consom_Mp = 0;
        try {
            Secteur[] list_secteur = Secteur.getAll(con);
            Date[] list_date_avant = new Meteo().getListDate_avant(date, con);
            for(int i=0 ; i < list_secteur.length ; i++){
                for(int k=0 ; k < list_date_avant.length ; k++){
                    consom_Mp = list_secteur[i].getDico(list_date_avant[k], con);
                    double arrondise = Math.ceil(consom_Mp);
                    Resultat resultat = new Resultat();
                    resultat.setSecteur(list_secteur[i]);
                    resultat.setConsom_mp(consom_Mp);
                    resultat.setDate(list_date_avant[k]);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
                    String nomJour = dateFormat.format(list_date_avant[k]);
                    resultat.setNom_jour(nomJour);
                    resultat.inserer(con);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    //recupure tous le resultat de consommation avant la date donnée
    public Resultat[] getAll_Resultat_Avant_Date(Date date,Connection con)throws Exception{
        Resultat[] list_resultat = null;
        String sql = "select * from resultat where date < ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setDate(1, date);
            ResultSet rs = stmt.executeQuery();
            int counteur = 0;
            while(rs.next()){
                counteur++;
            }
            list_resultat = new Resultat[counteur];
            rs = stmt.executeQuery();
            int index = 0;
            while(rs.next()){
                Resultat resultat = new Resultat();
                resultat.setId(rs.getInt("id"));
                resultat.setSecteur(this);
                resultat.setConsom_mp(rs.getDouble("consom_mp"));
                resultat.setDate(rs.getDate("date"));
                list_resultat[index] = resultat;
                index++;
            }
        }catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return list_resultat;
    }
    //maka moyene consommation am resultat ny date taloh rehetra
    public double getConsom_Moyenne(Date date,Connection con)throws Exception{
        double moyenne = 0;
        double somme = 0;
        Resultat[] resultat_avant = this.getAll_Resultat_Avant_Date(date, con);
        for (int i = 0; i < resultat_avant.length ; i++) {
            somme += resultat_avant[i].getConsom_mp();
        }
        moyenne = somme / resultat_avant.length;
        return moyenne;
    }
    
    // detail resultat_estime
    public Vector<DetailPrevision> estime_detail_prevision(Date date,Connection con)throws Exception{
        Vector<DetailPrevision> estim_detail_prevision = new Vector<DetailPrevision>();
        //
        double[] nbr_eleve_estime = Presence.getNbr_moyenne_Mp(date, con);
        double consom_M_Mp = this.getConsom_Moyenne(date, con);      
        //        
        try {
            //source
            Source source = Source.getBySecteur(this, con);
            double etat_initial_batt = source.getBatterie().getPuissance();
            double reste_batterie_moinsUn = 0;
            //meteo
            Meteo meteo = new Meteo();
            Meteo[] meteo_day = meteo.getDay(date, con);
            //consommation
            //
            for(int i=0 ; i < meteo_day.length ; i++){
                //
                reste_batterie_moinsUn = source.getBatterie().getPuissance();
                //
                double pourcentage_luminosite = meteo_day[i].getLuminosite() * 10;
                double energie_solaire = (pourcentage_luminosite * source.getPanneau().getPuissance()) / 100;
                double reste = 0;
                double consommation_total_heure = 0;
                DetailPrevision detailPrevision = new DetailPrevision();
                detailPrevision.setHeureDebut(meteo_day[i].getHeuredebut());
                detailPrevision.setHeureFin(meteo_day[i].getHeurefin());
                detailPrevision.setPourcentage_lum(pourcentage_luminosite);
                detailPrevision.setLuminosite(energie_solaire);
                detailPrevision.setSecteur(this);
                if(meteo_day[i].getHeurefin().compareTo(Time.valueOf("12:00:00")) <= 0) {
                    double consom_heure_matin = nbr_eleve_estime[0] * consom_M_Mp;
                    consommation_total_heure = consom_heure_matin;
                    reste = energie_solaire - consom_heure_matin;
                    detailPrevision.setNombre_eleve(nbr_eleve_estime[0]);
                    detailPrevision.setBesoin(consom_heure_matin);
                    detailPrevision.setConsommation(consom_heure_matin);
                }else{
                    double consom_heure_aprem = nbr_eleve_estime[1] * consom_M_Mp;
                    consommation_total_heure = consom_heure_aprem;
                    reste = energie_solaire - consom_heure_aprem;
                    detailPrevision.setNombre_eleve(nbr_eleve_estime[1]);
                    detailPrevision.setBesoin(consom_heure_aprem);
                    detailPrevision.setConsommation(consom_heure_aprem);
                }
                if(reste < 0){
                    double reste_batterie = source.getBatterie().getPuissance() - Math.abs(reste);
                    double moitie_bat = etat_initial_batt / 2;
                    //coupure
                    if(reste_batterie <= moitie_bat){
                        double reste_bat_avant_moitie = source.getBatterie().resteBatterieAvant50(etat_initial_batt, reste_batterie_moinsUn);
                        double consommation = energie_solaire + reste_bat_avant_moitie;
                        double reste_minute = source.getResteMinute(consommation_total_heure, consommation);
                        LocalTime heureCoupureLocalTime = meteo_day[i].getHeuredebut().toLocalTime();
                        LocalTime heure_coupure_precis = heureCoupureLocalTime.plusMinutes((long) reste_minute);
                        Time heure_coupure = Time.valueOf(heure_coupure_precis);
                        source.getBatterie().setPuissance(moitie_bat);
                        source.getBatterie().setPourcentage(50);
                        detailPrevision.setBatterie(source.getBatterie().getPuissance());
                        detailPrevision.setPourcentage_bat(source.getBatterie().getPourcentage());
                        detailPrevision.setEtat("coupure");
                    }else{
                        source.getBatterie().setPuissance(reste_batterie);
                        double pourcentage = source.getBatterie().getRestePourcentage(etat_initial_batt, reste_batterie);
                        source.getBatterie().setPourcentage(pourcentage);
                        detailPrevision.setBatterie(source.getBatterie().getPuissance());
                        detailPrevision.setPourcentage_bat(source.getBatterie().getPourcentage());
                    }
                }else{
                    if(source.getBatterie().getPourcentage()+reste  < 100){
                        System.out.println("reste "+reste);
                        double puissance_bat = source.getBatterie().getPuissance();
                        double new_puissance = puissance_bat + reste;
                        source.getBatterie().setPuissance(new_puissance);
                        double new_pourcentage = source.getBatterie().getRestePourcentage(etat_initial_batt, new_puissance);
                        source.getBatterie().setPourcentage(new_pourcentage);
                    }
                }
                detailPrevision.setConsommation(consom_M_Mp);
                estim_detail_prevision.add(detailPrevision);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return estim_detail_prevision;
    }
    //heure de coupure estime
    public Prevision estime_prevision(Date date,Connection con)throws Exception{
        Prevision prevision = new Prevision();
        prevision.setSecteur(this);
        //
        double[] nbr_eleve_estime = Presence.getNbr_moyenne_Mp(date, con);
        double consom_M_Mp = this.getConsom_Moyenne(date, con);      
        //        
        try {
            //source
            Source source = Source.getBySecteur(this, con);
            double etat_initial_batt = source.getBatterie().getPuissance();
            double reste_batterie_moinsUn = 0;
            //meteo
            Meteo meteo = new Meteo();
            Meteo[] meteo_day = meteo.getDay(date, con);
            //consommation
            //
            for(int i=0 ; i < meteo_day.length ; i++){
                //
                reste_batterie_moinsUn = source.getBatterie().getPuissance();
                //
                double pourcentage_luminosite = meteo_day[i].getLuminosite() * 10;
                double energie_solaire = (pourcentage_luminosite * source.getPanneau().getPuissance()) / 100;
                double reste = 0;
                double consommation_total_heure = 0;
                if(meteo_day[i].getHeurefin().compareTo(Time.valueOf("12:00:00")) <= 0) {
                    double consom_heure_matin = nbr_eleve_estime[0] * consom_M_Mp;
                    consommation_total_heure = consom_heure_matin;
                    reste = energie_solaire - consom_heure_matin;
                }else{
                    double consom_heure_aprem = nbr_eleve_estime[1] * consom_M_Mp;
                    consommation_total_heure = consom_heure_aprem;
                    reste = energie_solaire - consom_heure_aprem;
                }
                if(reste < 0){
                    double reste_batterie = source.getBatterie().getPuissance() - Math.abs(reste);
                    double moitie_bat = etat_initial_batt / 2;
                    //coupure
                    if(reste_batterie <= moitie_bat){
                        double reste_bat_avant_moitie = source.getBatterie().resteBatterieAvant50(etat_initial_batt, reste_batterie_moinsUn);
                        double consommation = energie_solaire + reste_bat_avant_moitie;
                        double reste_minute = source.getResteMinute(consommation_total_heure, consommation);
                        LocalTime heureCoupureLocalTime = meteo_day[i].getHeuredebut().toLocalTime();
                        LocalTime heure_coupure_precis = heureCoupureLocalTime.plusMinutes((long) reste_minute);
                        Time heure_coupure = Time.valueOf(heure_coupure_precis);
                        source.getBatterie().setPuissance(moitie_bat);
                        source.getBatterie().setPourcentage(50);
                        prevision.setHeure_coupure(heure_coupure);
                        prevision.setEtat("coupure");
                        break;
                    }else{
                        source.getBatterie().setPuissance(Math.max(reste_batterie, 0));
                        double pourcentage = source.getBatterie().getRestePourcentage(etat_initial_batt, reste_batterie);
                        source.getBatterie().setPourcentage(pourcentage);
                    }
                }else{
                    if(source.getBatterie().getPourcentage()+reste < 100){
                        double puissance_bat = source.getBatterie().getPuissance();
                        double new_puissance = puissance_bat + reste;
                        source.getBatterie().setPuissance(new_puissance);
                        double new_pourcentage = source.getBatterie().getRestePourcentage(etat_initial_batt, new_puissance);
                        source.getBatterie().setPourcentage(new_pourcentage);
                    } 
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prevision;
    }
    public static void main(String args[]) throws Exception{
        Connection con = Connexion.getconnection();
        Secteur secteur = new Secteur().getById("SEC001", con);
        Date date = Date.valueOf("2023-12-13");
        double consom_Mp = 73;
        int[] nbr_eleve = secteur.getNbrEleve(date, con);
        System.out.println("Nbr eleve matin "+nbr_eleve[0]+" aprem "+nbr_eleve[1]);
        
        double[] consom_total = secteur.getConsom_Total_Heure(consom_Mp, date, con);
        System.out.println("Consom_total matin "+consom_total[0]+" aprem "+consom_total[1]);
        //double conso = secteur.getDico(date, con);
        //System.out.println("ioioiioio "+conso);
        System.out.println("Precis "+secteur.getTimeCoupure(consom_Mp, date, con));
        
       
        //ho avy
        Date date_hoavy = Date.valueOf("2023-12-27");
        //Secteur ss = new Secteur();
        //ss.insertResultat(date_hoavy, con);
        /*Resultat[] resultat_avant = secteur.getAll_Resultat_Avant_Date(date_hoavy, con);
        for (int i = 0; i < resultat_avant.length ; i++) {
            System.out.println("Date "+resultat_avant[i].getDate()+" consom "+resultat_avant[i].getConsom_mp());
        }
        double consom_moy = secteur.getConsom_Moyenne(date_hoavy, con);
        System.out.println("consommation Moyenne "+consom_moy);
        double[] moyenne = Presence.getNbr_moyenne_Mp(date_hoavy, con);
        System.out.println("Matin moy= "+moyenne[0]+" Aprem moy= "+moyenne[1]);*/
        //secteur.estime_detail_prevision( date_hoavy, con);
        /*Vector<DetailPrevision> detail = secteur.estime_detail_prevision(date_hoavy, con);
        for(int i=0 ; i < detail.size() ; i++){
            System.err.println("Pour "+detail.get(i).getPourcentage_bat()+" Bat "+detail.get(i).getBatterie());
        }*/
        con.close();
    }
}
