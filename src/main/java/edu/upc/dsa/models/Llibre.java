package edu.upc.dsa.models;

public class Llibre {

    String id;
    String isbn;
    String titol;
    String editorial;
    String anyPublicacio;
    String numEdicio;
    String autor;
    String tematica;

    public Llibre() {

    }

    public Llibre (String id, String isbn, String titol, String editorial, String anyPublicacio, String numEdicio, String autor, String tematica) {
        this.id = id;
        this.isbn = isbn;
        this.titol = titol;
        this.editorial = editorial;
        this.anyPublicacio = anyPublicacio;
        this.numEdicio = numEdicio;
        this.autor = autor;
        this.tematica = tematica;
    }

    public String getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitol() {
        return titol;
    }

    public String getEditorial() {
        return editorial;
    }

    public String getAnyPublicacio() {
        return anyPublicacio;
    }

    public String getNumEdicio() {
        return numEdicio;
    }

    public String getAutor() {
        return autor;
    }

    public String getTematica() {
        return tematica;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setTitol(String titol) {
        this.titol = titol;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public void setAnyPublicacio(String anyPublicacio) {
        this.anyPublicacio = anyPublicacio;
    }

    public void setNumEdicio(String numEdicio) {
        this.numEdicio = numEdicio;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setTematica(String tematica) {
        this.tematica = tematica;
    }
}
