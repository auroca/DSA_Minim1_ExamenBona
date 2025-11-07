package edu.upc.dsa;

import edu.upc.dsa.exceptions.IllegalArgumentException;
import edu.upc.dsa.models.Lector;
import edu.upc.dsa.models.Llibre;
import edu.upc.dsa.models.Prestec;

import edu.upc.dsa.exceptions.*;


import java.util.*;

import org.apache.log4j.Logger;

public class LlibresManagerImpl implements LlibresManager {
    private static LlibresManager lm;

    private Map<String, Lector> lectors;
    private Map<String, Llibre> cataleg;
    private Map<String, List<Prestec>> prestecsPerLector;

    private Queue<Stack<Llibre>> magatzem;

    final static Logger logger = Logger.getLogger(LlibresManagerImpl.class);

    private LlibresManagerImpl() {

        this.lectors = new HashMap<>();
        this.cataleg = new HashMap<>();
        this.prestecsPerLector = new HashMap<>();
        this.magatzem = new ArrayDeque<>();

    }

    public static LlibresManager getInstance() {
        if (lm==null) lm = new LlibresManagerImpl();
        return lm;
    }

    public Lector afegirLector (Lector l){
        logger.info("afegir lector: " + l.getId());
        if(l==null||l.getId()==null) {
            logger.error("lector o id null");
            throw new IllegalArgumentException("lector o id null");
        }
            if(lectors.containsKey(l.getId())) {
                Lector lector = lectors.get(l.getId());
                lector.setAdreca(l.getAdreca());
                lector.setName(l.getName());
                lector.setLastname(l.getLastname());
                lector.setDni(l.getDni());
                lector.setDataNaixement(l.getDataNaixement());
                lector.setLlocNaixement(l.getLlocNaixement());
                lector.setAdreca(l.getAdreca());
                logger.info("lector actualitzat: " + l.getId());
                return lector;
            }
            else{
                lectors.put(l.getId(),l);
                logger.info("lector creat: " + l.getId());
                return l;
            }
    }

    public void emmagatzemarLlibre(Llibre llibre){
        logger.info("emmagatzemar llibre: " + llibre);

        if (llibre == null) {
            logger.error("llibre null");
            throw new IllegalArgumentException("llibre null");
        }

        if (magatzem.isEmpty()||((ArrayDeque<Stack<Llibre>>) magatzem).getLast().size()>=10) {
            magatzem.add(new Stack<>());
        }

        Stack<Llibre> ultimStack = ((ArrayDeque<Stack<Llibre>>) magatzem).getLast();
        ultimStack.push(llibre);
        logger.info("llibre apilat amb un pila de tamany= "+ultimStack.size());
    }

    public Llibre catalogarLlibre(){
        logger.info("catalogar llibre");
        while (!magatzem.isEmpty() && magatzem.peek().isEmpty()) {
            magatzem.poll();
        }
        if (magatzem.isEmpty()) {
            logger.error("No hi han llibres al magatzem per catalogar");
            throw new NoBooksToCatalogException("No hay libros pendientes de catalogar");
        }

        Stack<Llibre> primerStack = magatzem.peek();
        Llibre l = primerStack.pop();
        if (primerStack.isEmpty()){
            magatzem.poll();
        }

        String isbn = l.getIsbn();
        Llibre llibreAlCataleg = cataleg.get(isbn);

        if(llibreAlCataleg==null){
            l.setQuantitatDisponible(1);
            cataleg.put(isbn,l);
            logger.info("cataleg creat: " + l.getId());
            return l;
        }
        else{
            int q = llibreAlCataleg.getQuantitatDisponible();
            llibreAlCataleg.setQuantitatDisponible(q+1);
            logger.info("cataleg actualitzat: " + l.getId());
            return llibreAlCataleg;
        }

    }

    public Prestec prestarLlibre (String idPrestec, String idLector, String idIsbn, String dataPrestec, String dataDevolucio){
        logger.info("prestar llibre: " + idIsbn);

        if(idPrestec == null || idLector == null || idIsbn == null){
            logger.error("parametres null");
            throw new IllegalArgumentException("id null");
        }

        Lector lector = this.lectors.get(idLector);
        if (lector == null) {
            logger.error("lector no trobat");
            throw new LectorNoExisteix("lector no trobat");
        }

        Llibre l = cataleg.get(idIsbn);
        if (l == null) {
            logger.error("llibre no catalogat");
            throw new LlibreNoCatalogatException("llibre no catalogat");
        }
        int disp = l.getQuantitatDisponible();
        if(disp <= 0){
            logger.error("llibre sense copies disponibles");
            throw new CopiesNoDisponiblesException("llibre sense copies disponibles");
        }
        l.setQuantitatDisponible(disp-1);

        Prestec p = new Prestec(idPrestec, idLector, idIsbn, dataPrestec, dataDevolucio, "En tràmit");
        prestecsPerLector.computeIfAbsent(idLector, k -> new ArrayList<>()).add(p);
        logger.info("prestec creat: " + idIsbn);
        return p;
    }

    public List<Prestec> prestecsLector(String idLector){
        logger.info("prestecs del lector: " + idLector);
        List<Prestec> l = new ArrayList<>();
        l = prestecsPerLector.getOrDefault(idLector, Collections.emptyList());
        logger.info("llista de prestecs creada: " + l.size());
        return l;
    }



    public Lector getLector (String id){
        logger.info("getLector: " + id);
        return this.lectors.get(id);
    }

    public List getLectors(){
        logger.info("get tots els lectors");
        return new ArrayList<>(this.lectors.values());
    }

    public Llibre getLlibreCatalogat(String isbn) {
        logger.info("get llibre catalogat: " + isbn);
        return cataleg.get(isbn);
    }

    public List getLlibresCatalogats(){
        logger.info("get tots llibres catalogats");
        return new ArrayList<>(cataleg.values());
    }


    public Map<Integer, Integer> getMidaDeCadaStack() {
        logger.info("llista de els stacks de llibres amb la seva mida");
        Map<Integer, Integer> result = new LinkedHashMap<>();
        int i = 1;
        for (Stack<Llibre> munt : magatzem) {
            result.put(i++, munt.size());
        }
        return result;
    }


    public void clear() {
        lectors.clear();
        cataleg.clear();
        prestecsPerLector.clear();
        magatzem.clear();
    }

}