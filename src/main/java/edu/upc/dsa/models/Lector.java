package edu.upc.dsa.models;

public class Lector {

    String id;
    String name;
    String lastname;
    String dni;
    String dataNaixement;
    String llocNaixement;
    String adreca;

    public Lector() {

    }

    public Lector(String id, String name, String lastname, String dni, String dataNaixement, String llocNaixement, String adreca) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.dni = dni;
        this.dataNaixement = dataNaixement;
        this.llocNaixement = llocNaixement;
        this.adreca = adreca;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getDni() {
        return dni;
    }

    public String getDataNaixement() {
        return dataNaixement;
    }

    public String getLlocNaixement() {
        return llocNaixement;
    }

    public String getAdreca() {
        return adreca;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setDataNaixement(String dataNaixement) {
        this.dataNaixement = dataNaixement;
    }

    public void setLlocNaixement(String llocNaixement) {
        this.llocNaixement = llocNaixement;
    }

    public void setAdreca(String adreca) {
        this.adreca = adreca;
    }
}
