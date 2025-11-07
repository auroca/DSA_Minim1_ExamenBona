package edu.upc.dsa;

import edu.upc.dsa.models.Lector;
import edu.upc.dsa.models.Llibre;
import edu.upc.dsa.models.Prestec;

import java.util.List;
import java.util.Map;

public interface LlibresManager {

    public Lector afegirLector (Lector l);

    public void emmagatzemarLlibre(Llibre llibre);

    public Llibre catalogarLlibre();

    public Prestec prestarLlibre (String idPrestec, String idLector, String idIsbn, String dataPrestec, String dataDevolucio);

    public List<Prestec> prestecsLector(String idLector);


    public Lector getLector (String id);

    public List getLectors();

    public Llibre getLlibreCatalogat(String isbn);

    public List getLlibresCatalogats();

    public Map<Integer, Integer> getMidaDeCadaStack();


    public void clear();


}
