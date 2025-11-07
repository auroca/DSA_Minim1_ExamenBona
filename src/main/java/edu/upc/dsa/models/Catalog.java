package edu.upc.dsa.models;

public class Catalog {

    String idLlibre;
    int ejemplaresTotales;
    int ejemplaresDisponibles;

    public Catalog(){

    }

    public Catalog(String idLlibre) {
        this.idLlibre = idLlibre;
        this.ejemplaresTotales = 1;
        this.ejemplaresDisponibles = 1;
    }


    public int getEjemplaresDisponibles() {
        return ejemplaresDisponibles;
    }

    public int getEjemplaresTotales() {
        return ejemplaresTotales;
    }

    public String getIdLlibre() {
        return idLlibre;
    }

    public void setIdLlibre(String idLlibre) {
        this.idLlibre = idLlibre;
    }

    public void setEjemplaresTotales(int ejemplaresTotales) {
        this.ejemplaresTotales = ejemplaresTotales;
    }

    public void setEjemplaresDisponibles(int ejemplaresDisponibles) {
        this.ejemplaresDisponibles = ejemplaresDisponibles;
    }


}
