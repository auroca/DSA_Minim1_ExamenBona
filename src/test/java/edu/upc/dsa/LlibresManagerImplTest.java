package edu.upc.dsa;

import edu.upc.dsa.exceptions.*;
import edu.upc.dsa.models.Catalog;
import edu.upc.dsa.models.Llibre;
import edu.upc.dsa.models.Lector;
import edu.upc.dsa.models.Prestec;
import org.junit.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;

public class LlibresManagerImplTest {

    private LlibresManagerImpl manager;

    @Before
    public void setUp() throws Exception {
        manager = (LlibresManagerImpl) LlibresManagerImpl.getInstance();
        manager.clear();
    }

    @After
    public void tearDown() {
        manager.clear();
    }

    private Llibre makeLlibre(String id) throws Exception {
        Class<?> cls = Class.forName("edu.upc.dsa.models.Llibre");
        try {
            Constructor<?> c = cls.getConstructor(String.class);
            Object o = c.newInstance(id);
            return (Llibre) o;
        } catch (NoSuchMethodException ignored) {}

        try {
            Constructor<?> c = cls.getConstructor(String.class, String.class, String.class, int.class, int.class, String.class, String.class);
            Object o = c.newInstance(id, "Titol " + id, "Editorial X", 2020, 1, "Autor", "Tematica");
            return (Llibre) o;
        } catch (NoSuchMethodException ignored) {}

        Object o = cls.getDeclaredConstructor().newInstance();
        try {
            Method m = cls.getMethod("setId", String.class);
            m.invoke(o, id);
            return (Llibre) o;
        } catch (NoSuchMethodException ignored) {}

        try {
            Method m = cls.getMethod("setIsbn", String.class);
            m.invoke(o, id);
            return (Llibre) o;
        } catch (NoSuchMethodException ignored) {}

        try {
            Field f = cls.getDeclaredField("id");
            f.setAccessible(true);
            f.set(o, id);
            return (Llibre) o;
        } catch (NoSuchFieldException ex) {
            throw new RuntimeException("No es pot inicialitzar Llibre amb id: afegeix constructor(String) o setId(String).");
        }
    }

    @Test
    public void storeAndCatalogMultipleStacksAndLoan() throws Exception {
        manager.afegirLector("L1", "Nom", "Cognom", "DNI1", "1990-01-01", "Local", "Adreça");

        for (int i = 1; i <= 12; i++) {
            Llibre l = makeLlibre("ISBN" + i);
            manager.emmagatzemarLlibre(l);
        }

        for (int i = 1; i <= 12; i++) {
            manager.catalogarLlibre();
        }

        for (int i = 1; i <= 12; i++) {
            String isbn = "ISBN" + i;
            Prestec p = manager.prestarLlibre("P" + i, "L1", isbn, "2025-03-01", "2025-03-10");
            assertNotNull("Prestec creat per " + isbn + " ha de ser no null", p);
        }

        boolean thrown = false;
        try {
            manager.catalogarLlibre();
        } catch (NoBooksToCatalogException e) {
            thrown = true;
        }
        assertTrue("Esperàvem NoBooksToCatalogException quan no hi ha llibres a catalogar", thrown);
    }

    @Test
    public void catalogIncrementCopiesAndMultipleLoans() throws Exception {
        final String isbn = "SAME-ISBN";

        Llibre l1 = makeLlibre(isbn);
        Llibre l2 = makeLlibre(isbn + "-TMP");
        try {
            Method m = l2.getClass().getMethod("setId", String.class);
            m.invoke(l2, isbn);
        } catch (NoSuchMethodException ex) {
            Field f = l2.getClass().getDeclaredField("id");
            f.setAccessible(true);
            f.set(l2, isbn);
        }

        manager.emmagatzemarLlibre(l1);
        manager.emmagatzemarLlibre(l2);

        manager.catalogarLlibre();
        manager.catalogarLlibre();

        Catalog cat = manager.getCatalog(isbn);
        assertNotNull("Catalog entry must exist after cataloging twice", cat);
        assertEquals(2, cat.getEjemplaresTotales());
        assertEquals(2, cat.getEjemplaresDisponibles());

        // Afegim lector
        manager.afegirLector("L2", "Anna", "Perez", "DNI2", "1985-05-05", "Local", "Adreça");

        // Fem dos préstecs correctes
        Prestec p1 = manager.prestarLlibre("PR1", "L2", isbn, "2025-04-01", "2025-04-10");
        assertNotNull(p1);
        Prestec p2 = manager.prestarLlibre("PR2", "L2", isbn, "2025-04-11", "2025-04-20");
        assertNotNull(p2);

        // Tercer préstec ha de fallar per falta de còpies
        try {
            manager.prestarLlibre("PR3", "L2", isbn, "2025-04-21", "2025-04-30");
            fail("S'esperava CopiesNoDisponiblesException al intentar un tercer préstec");
        } catch (CopiesNoDisponiblesException e) {
            // ok
        }
    }

    @Test(expected = LlibreNoCatalogatException.class)
    public void loanFailsIfNotCatalogued() throws Exception {
        // Afegim lector i emmagatzenem un llibre però sense catalogar
        manager.afegirLector("L3", "Nom", "Cognom", "DNI3", "1991-01-01", "Local", "Adreça");
        Llibre l = makeLlibre("NOTCAT-ISBN");
        manager.emmagatzemarLlibre(l);

        // Intentem prestar (ha de llançar LlibreNoCatalogatException)
        manager.prestarLlibre("PX", "L3", "NOTCAT-ISBN", "2025-05-01", "2025-05-10");
    }

    @Test
    public void loanSucceedsAfterCatalogAndPrestecsByLector() throws Exception {
        // Afegim lector
        manager.afegirLector("L4", "Pere", "Garcia", "DNI4", "1980-03-03", "Local", "Adreça");

        // Emmagatzemem i cataloguem
        String isbn = "CAT-ISBN";
        Llibre l = makeLlibre(isbn);
        manager.emmagatzemarLlibre(l);
        manager.catalogarLlibre();

        // Prestar
        Prestec p = manager.prestarLlibre("PRX", "L4", isbn, "2025-06-01", "2025-06-10");
        assertNotNull(p);

        // Comprovem que prestecsLector retorna aquest préstec
        List<Prestec> llista = manager.prestecsLector("L4");
        assertEquals(1, llista.size());
        assertEquals("PRX", llista.get(0).getIdPrestec());
    }
}