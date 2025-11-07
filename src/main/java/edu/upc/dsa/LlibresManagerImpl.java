package edu.upc.dsa;

import edu.upc.dsa.models.Lector;
import edu.upc.dsa.models.Llibre;
import edu.upc.dsa.models.Prestec;
import edu.upc.dsa.models.Catalog;

import edu.upc.dsa.exceptions.*;


import java.util.*;

import org.apache.log4j.Logger;

public class LlibresManagerImpl implements LlibresManager {
    private static LlibresManager lm;

    private Map<String, Lector> lectors;
    private Map<String, Catalog> catalog;//Llibres disponibles per a fer un prestec
    private List<Stack<Llibre>> magatzem;
    private List<Prestec> prestecs;
    private Map<String, Llibre> llibresTotals;

    final static Logger logger = Logger.getLogger(LlibresManagerImpl.class);

    private LlibresManagerImpl() {
        this.lectors = new HashMap<>();
        this.catalog = new HashMap<>();
        this.magatzem = new ArrayList<>();
        this.prestecs = new ArrayList<>();
        this.llibresTotals = new HashMap<>();

        this.magatzem.add(new Stack<>());
    }

    public static LlibresManager getInstance() {
        if (lm==null) lm = new LlibresManagerImpl();
        return lm;
    }

    public Lector afegirLector (String id, String name, String lastname, String dni, String dataNaixement, String llocNaixement, String adreca){
        logger.info("afegir lector: " + id);
        Lector l = new Lector(id, name, lastname, dni, dataNaixement, llocNaixement, adreca);

            if(lectors.containsKey(id)){
                Lector lector = lectors.get(id);
                lector.setAdreca(adreca);
                lector.setName(name);
                lector.setLastname(lastname);
                lector.setDni(dni);
                lector.setDataNaixement(dataNaixement);
                lector.setLlocNaixement(llocNaixement);
                logger.info("lector actualitzat: " + id);
                return lector;
            }
            else{
                lectors.put(id,l);
                logger.info("lector creat: " + id);
                return l;
            }
    }

    public void emmagatzemarLlibre(Llibre llibre){
        logger.info("emmagatzemar llibre: " + llibre);
        if (llibre == null) {
            logger.error("llibre null");
        }

        llibresTotals.put(llibre.getId(), llibre);

        Stack<Llibre> ultimStack = new Stack<>();
        ultimStack = magatzem.get(magatzem.size()-1);
        if(ultimStack.size() >= 10){
            ultimStack = new Stack<>();
            magatzem.add(ultimStack);//creem un nou stack a l'ultima posició de la llista magatzem
        }
        ultimStack.push(llibre);
        logger.info("llibre apilat al munt = "+(magatzem.size()-1)+" amb un pila de tamany= "+ultimStack.size());
    }

    public Catalog catalogarLlibre(){
        while (!magatzem.isEmpty() && magatzem.get(0).isEmpty()) {
            magatzem.remove(0);
        }
        if (magatzem.isEmpty()) {
            logger.error("No hi han llibres al magatzem per catalogar");
            throw new NoBooksToCatalogException("No hay libros pendientes de catalogar");
        }

        Stack<Llibre> primerStack = magatzem.get(0);
        Llibre l = primerStack.pop();
        if (primerStack.isEmpty()){
            magatzem.remove(0);
        }

        if(catalog.containsKey(l.getId())){
            Catalog c = catalog.get(l.getId());
            c.setEjemplaresTotales(c.getEjemplaresTotales()+1);
            c.setEjemplaresDisponibles(c.getEjemplaresDisponibles()+1);
            logger.info("catalog actualitzat: " + l.getId());
            return c;
        }
        else{
            Catalog catalog = new Catalog(l.getId());
            this.catalog.put(l.getId(), catalog);
            logger.info("catalog creat: " + l.getId());
            return catalog;
        }
    }

    public Prestec prestarLlibre (String idPrestec, String idLector, String idLlibre, String dataPrestec, String dataDevolucio){
        logger.info("prestar llibre: " + idLlibre);
        Lector lector = this.lectors.get(idLector);
        if (lector == null) {
            logger.error("lector no trobat");
            throw new LectorNoExisteix("lector no trobat");
        }

        Catalog c = this.catalog.get(idLlibre);
        if (c == null) {
            logger.error("libro no catalogado");
            throw new LlibreNoCatalogatException("libro no catalogado");
        }
        int disp = c.getEjemplaresDisponibles();
        if(disp <= 0){
            logger.error("libro sin copias disponibles");
            throw new CopiesNoDisponiblesException("libro sin copias disponibles");
        }
        c.setEjemplaresDisponibles(disp-1);

        Prestec p = new Prestec(idPrestec, idLector, idLlibre, dataPrestec, dataDevolucio, "En tràmit");
        prestecs.add(p);
        logger.info("prestac creat: " + idLlibre);
        return p;
    }

    public List<Prestec> prestecsLector(String idLector){
        logger.info("prestecs del lector: " + idLector);
        List<Prestec> l = new ArrayList<>();
        for(Prestec p : prestecs){
            if (p.getIdLector().equals(idLector)){
                l.add(p);
            }
        }
        logger.info("llista de prestecs creada: " + l.size());
        return l;
    }

    public Catalog getCatalog(String idLlibre){
        if (idLlibre == null) return null;
        return this.catalog.get(idLlibre);
    }

    public Lector getLector (String id){
        return this.lectors.get(id);
    }


    public void clear() {
        logger.info("clear IN");
        synchronized (lectors) { lectors.clear(); }
        synchronized (catalog) {
            catalog.clear();
            llibresTotals.clear();
        }
        synchronized (magatzem) {
            magatzem.clear();
            magatzem.add(new Stack<>());
        }
        synchronized (prestecs) { prestecs.clear(); }
        logger.info("clear OUT");
    }

}