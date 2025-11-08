package edu.upc.dsa.services;

import edu.upc.dsa.LlibresManager;
import edu.upc.dsa.LlibresManagerImpl;
import edu.upc.dsa.models.Lector;
import edu.upc.dsa.models.Llibre;
import edu.upc.dsa.models.Prestec;
import edu.upc.dsa.exceptions.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Api(value = "/Biblioteca", description = "Servei de gestió de llibres, lectors i préstecs")
@Path("/Biblioteca")
@Produces(MediaType.APPLICATION_JSON)
public class LlibresService {

    private final LlibresManager manager;
    private static final Logger logger = Logger.getLogger(LlibresService.class);

    public LlibresService() {
        this.manager = LlibresManagerImpl.getInstance();
        if(((LlibresManagerImpl) manager).getLectors().isEmpty() && ((LlibresManagerImpl) manager).getLlibresCatalogats().isEmpty()){
            Lector l1 = new Lector("L1", "Anna", "Martí", "123A", "1995-02-01", "Girona", "C/Sol 3");
            Lector l2 = new Lector("L2", "Pau", "Serra", "456B", "1997-07-15", "Tarragona", "C/Lluna 9");

            manager.afegirLector(l1);
            manager.afegirLector(l2);

            Llibre b1 = new Llibre("B1", "ISBN-001", "Clean Code", "Prentice Hall", "2008", "1", "Robert C. Martin", "Programacio");
            Llibre b2 = new Llibre("B2", "ISBN-002", "Effective Java", "Addison-Wesley", "2018", "3", "Joshua Bloch", "Programacio");
            Llibre b3 = new Llibre("B3", "ISBN-001", "Clean Code", "Prentice Hall", "2008", "1", "Robert C. Martin", "Programacio");

            this.manager.emmagatzemarLlibre(b1);
            this.manager.emmagatzemarLlibre(b2);
            this.manager.emmagatzemarLlibre(b3);

            this.manager.catalogarLlibre();
            this.manager.catalogarLlibre();
            this.manager.catalogarLlibre();

            this.manager.prestarLlibre("PR1", "L1", "ISBN-001", "2025-01-01", "2025-01-15");
            this.manager.prestarLlibre("PR2", "L1", "ISBN-002", "2025-01-02", "2025-01-20");
        }
    }


    @POST
    @ApiOperation(value = "Crear o actualitzar un lector")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Lector creat", response = Lector.class),
            @ApiResponse(code = 200, message = "Lector actualitzat", response = Lector.class),
            @ApiResponse(code = 500, message = "Validation Error")
    })
    @Path("/lector")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addOrUpdateLector(Lector l) {
        if (l == null || l.getId() == null || l.getId().trim().isEmpty()) {
            return Response.status(500).entity(l).build();
        }
        boolean previo = manager.getLector(l.getId()) != null;
        Lector out = manager.afegirLector(l);
        if (previo) {
            return Response.status(200).entity(out).build();
        }
        else{
            return Response.status(201).entity(out).build();
        }
    }


    @POST
    @ApiOperation(value = "Apilar un llibre al magatzem")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Llibre apilat"),
            @ApiResponse(code = 400, message = "Dades invàlides")
    })
    @Path("/magatzem/llibre")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response emmagatzemar(Llibre llibre) {
            if (llibre==null) {
                return Response.status(400).entity("llibre null").build();
            }
            manager.emmagatzemarLlibre(llibre);
            return Response.status(201).build();
    }

    @POST
    @ApiOperation(value = "Catalogar el següent llibre pendent")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Llibre catalogat", response = Llibre.class),
            @ApiResponse(code = 404, message = "No hi ha llibres per catalogar")
    })
    @Path("/catalogar")
    public Response catalogNext() {
        try {
            Llibre llibre = manager.catalogarLlibre();
            return Response.status(200).entity(llibre).build();
        } catch (NoBooksToCatalogException e) {
            return Response.status(409).entity("No hi ha llibres pendents de catalogar").build();
        }
    }


    @POST
    @Path("/prestec")
    @ApiOperation(value = "Crear un préstec i decrementar la disponibilitat")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Creat", response = Prestec.class),
            @ApiResponse(code = 404, message = "Lector o llibre inexistent"),
            @ApiResponse(code = 409, message = "Sense còpies disponibles"),
            @ApiResponse(code = 400, message = "Dades invàlides")
    })
    public Response prestar(Prestec prestec) {
        if (prestec == null || prestec.getIdPrestec() == null || prestec.getIdLector() == null || prestec.getIdLlibre() == null)
            return Response.status(400).entity("idPrestec/idLector/isbn requerits").build();
        try {
            Prestec p = manager.prestarLlibre(prestec.getIdPrestec(), prestec.getIdLector(), prestec.getIdLlibre(), prestec.getDataPrestec(), prestec.getDataDevoluciio());
            return Response.status(201).entity(p).build();
        } catch (LectorNoExisteix e) {
            return Response.status(404).entity("lector no trobat").build();
        } catch (LlibreNoCatalogatException e) {
            return Response.status(404).entity("llibre no catalogat").build();
        } catch (CopiesNoDisponiblesException e) {
            return Response.status(409).entity("no hi ha exemplars disponibles").build();
        }
    }

    @GET
    @Path("/lectors/{id}/prestecs")
    @ApiOperation(value = "Llistar tots els préstecs d’un lector", response = Prestec.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK")
    })
    public Response prestecsPerLector(@PathParam("id") String idLector) {
        List<Prestec> l = manager.prestecsLector(idLector);
        GenericEntity<List<Prestec>> entity = new GenericEntity<List<Prestec>>(l) {};
        return Response.status(200).entity(entity).build();
    }

    @GET
    @Path("/lectors")
    @ApiOperation(value = "Llistar tots els lectors", response = Lector.class, responseContainer = "List")
    public Response getLectors() {
        List<Lector> l = manager.getLectors();
        GenericEntity<List<Lector>> entity = new GenericEntity<List<Lector>>(l) {};
        return Response.status(200).entity(entity).build();
    }

    @GET
    @Path("/cataleg")
    @ApiOperation(value = "Llistar tots els llibres catalogats", response = Llibre.class, responseContainer = "List")
    public Response getCataleg() {
        List<Llibre> l = manager.getLlibresCatalogats();
        GenericEntity<List<Llibre>> entity = new GenericEntity<List<Llibre>>(l) {};
        return Response.status(200).entity(entity).build();
    }


}


