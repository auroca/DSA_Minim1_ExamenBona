package edu.upc.dsa;

import edu.upc.dsa.exceptions.CopiesNoDisponiblesException;
import edu.upc.dsa.exceptions.LectorNoExisteix;
import edu.upc.dsa.exceptions.LlibreNoCatalogatException;
import edu.upc.dsa.exceptions.NoBooksToCatalogException;
import edu.upc.dsa.models.Lector;
import edu.upc.dsa.models.Llibre;
import edu.upc.dsa.models.Prestec;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class LlibresManagerImplTest {

    private LlibresManagerImpl manager;

    @Before
    public void setUp() {
        manager = (LlibresManagerImpl) LlibresManagerImpl.getInstance();
        manager.clear();
    }

    private Lector lector(String id) {
        return new Lector(id, "Nom", "Cognom", "DNI"+id, "2000-01-01", "BCN", "Carrer 1");
    }

    private Llibre llibre(String id, String isbn) {
        return new Llibre(id, isbn, "Titol "+isbn, "Ed", "2020", "1", "Autor", "Tematica");
    }

    @Test
    public void testAfegirLectorAltaIActualitzacio() {
        Lector l1 = lector("L1");
        manager.afegirLector(l1);
        assertNotNull(manager.getLector("L1"));
        Lector l1b = lector("L1");
        l1b.setName("NouNom");
        manager.afegirLector(l1b);
        assertEquals("NouNom", manager.getLector("L1").getName());
        assertEquals(1, manager.getLectors().size());
    }

    @Test
    public void testEmmagatzemarMuntsDeuILastStack() {
        for (int i = 1; i <= 25; i++) {
            manager.emmagatzemarLlibre(llibre("ID"+i, "ISBN"+i));
        }
        Map<Integer,Integer> mida = manager.getMidaDeCadaStack();
        assertEquals(3, mida.size());
        assertEquals(Integer.valueOf(10), mida.get(1));
        assertEquals(Integer.valueOf(10), mida.get(2));
        assertEquals(Integer.valueOf(5), mida.get(3));
    }

    @Test
    public void testCatalogarFIFOdeMuntsYLIFOdins() {
        manager.emmagatzemarLlibre(llibre("A","ISBN-A"));
        manager.emmagatzemarLlibre(llibre("B","ISBN-B"));
        manager.emmagatzemarLlibre(llibre("C","ISBN-C"));
        Llibre c = manager.catalogarLlibre();
        assertEquals("ISBN-C", c.getIsbn());
        Llibre b = manager.catalogarLlibre();
        assertEquals("ISBN-B", b.getIsbn());
        Llibre a = manager.catalogarLlibre();
        assertEquals("ISBN-A", a.getIsbn());
    }

    @Test(expected = NoBooksToCatalogException.class)
    public void testCatalogarSenseLlibresLlança() {
        manager.catalogarLlibre();
    }

    @Test
    public void testCatalogarAgregaQuantitatsPerISBN() {
        manager.emmagatzemarLlibre(llibre("X1","ISBN-X"));
        manager.emmagatzemarLlibre(llibre("X2","ISBN-X"));
        Llibre x1 = manager.catalogarLlibre();
        assertEquals(1, x1.getQuantitatDisponible());
        Llibre x2 = manager.catalogarLlibre();
        assertEquals(2, x2.getQuantitatDisponible());
        assertSame(x2, manager.getLlibreCatalogat("ISBN-X"));
    }

    @Test
    public void testPrestarFluxCorrecteIDecrementa() {
        manager.afegirLector(lector("L1"));
        manager.emmagatzemarLlibre(llibre("P1","ISBN-P"));
        manager.catalogarLlibre();
        assertEquals(1, manager.getLlibreCatalogat("ISBN-P").getQuantitatDisponible());
        Prestec p = manager.prestarLlibre("PR1","L1","ISBN-P","2025-01-01","2025-01-10");
        assertNotNull(p);
        assertEquals(0, manager.getLlibreCatalogat("ISBN-P").getQuantitatDisponible());
        List<Prestec> dels = manager.prestecsLector("L1");
        assertEquals(1, dels.size());
        assertEquals("PR1", dels.get(0).getIdPrestec());
    }

    @Test(expected = LectorNoExisteix.class)
    public void testPrestarLectorInexistent() {
        manager.emmagatzemarLlibre(llibre("Q1","ISBN-Q"));
        manager.catalogarLlibre();
        manager.prestarLlibre("PRX","NO","ISBN-Q","2025-01-01","2025-01-10");
    }

    @Test(expected = LlibreNoCatalogatException.class)
    public void testPrestarLlibreNoCatalogat() {
        manager.afegirLector(lector("L1"));
        manager.prestarLlibre("PRX","L1","ISBN-NO","2025-01-01","2025-01-10");
    }

    @Test(expected = CopiesNoDisponiblesException.class)
    public void testPrestarSenseCopies() {
        manager.afegirLector(lector("L1"));
        manager.emmagatzemarLlibre(llibre("R1","ISBN-R"));
        manager.catalogarLlibre();
        manager.prestarLlibre("PR1","L1","ISBN-R","2025-01-01","2025-01-10");
        manager.prestarLlibre("PR2","L1","ISBN-R","2025-01-02","2025-01-12");
    }
}
