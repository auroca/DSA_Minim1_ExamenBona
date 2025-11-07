package edu.upc.dsa.exceptions;

public class NoBooksToCatalogException extends RuntimeException {
    public NoBooksToCatalogException(String message) {
        super(message);
    }
}
