package edu.upc.dsa;

import edu.upc.dsa.models.Lector;
import edu.upc.dsa.models.Llibre;
import edu.upc.dsa.models.Prestec;
import edu.upc.dsa.models.Catalog;

import java.util.List;

public interface LlibresManager {

    public Lector afegirLector (String id, String name, String lastname, String dni, String dataNaixement, String llocNaixement, String adreca);

    public void emmagatzemarLlibre(Llibre llibre);

    public Catalog catalogarLlibre();

    public Prestec prestarLlibre (String idPrestec, String idLector, String idLlibre, String dataPrestec, String dataDevolucio);

    public List<Prestec> prestecsLector(String idLector);

    public Catalog getCatalog(String idLlibre);

    public Lector getLector (String id);

    public void clear();


}
