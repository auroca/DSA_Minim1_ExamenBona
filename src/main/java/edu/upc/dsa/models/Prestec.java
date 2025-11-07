package edu.upc.dsa.models;

public class Prestec {

    String idPrestec;
    String idLector;
    String idLlibre;
    String dataPrestec;
    String dataDevoluciio;
    String estat;

    public Prestec(){

    }

    public Prestec (String idPrestec, String idLector, String idLlibre, String dataPrestec, String dataDevolucio, String estat) {
        this.idPrestec = idPrestec;
        this.idLector = idLector;
        this.idLlibre = idLlibre;
        this.dataPrestec = dataPrestec;
        this.dataDevoluciio = dataDevolucio;
        this.estat = estat;
    }

    public String getIdPrestec() {
        return idPrestec;
    }

    public String getIdLector() {
        return idLector;
    }

    public String getIdLlibre() {
        return idLlibre;
    }

    public String getDataPrestec() {
        return dataPrestec;
    }

    public String getDataDevoluciio() {
        return dataDevoluciio;
    }

    public String getEstat() {
        return estat;
    }

    public void setIdPrestec(String idPrestec) {
        this.idPrestec = idPrestec;
    }

    public void setIdLector(String idLector) {
        this.idLector = idLector;
    }

    public void setIdLlibre(String idLlibre) {
        this.idLlibre = idLlibre;
    }

    public void setDataPrestec(String dataPrestec) {
        this.dataPrestec = dataPrestec;
    }

    public void setDataDevoluciio(String dataDevoluciio) {
        this.dataDevoluciio = dataDevoluciio;
    }

    public void setEstat(String estat) {
        this.estat = estat;
    }
}
