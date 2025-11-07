package edu.upc.dsa.services;

import edu.upc.dsa.LlibresManager;
import edu.upc.dsa.LlibresManagerImpl;
import edu.upc.dsa.models.Lector;
import edu.upc.dsa.models.Llibre;
import edu.upc.dsa.models.Prestec;
import edu.upc.dsa.models.Catalog;
import edu.upc.dsa.exceptions.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;

import java.util.List;

@Api(value = "/llibres", description = "Servei de gestió de llibres, lectors i préstecs")
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class LlibresService {

    private final LlibresManager manager;
    private static final Logger logger = Logger.getLogger(LlibresService.class);

    public LlibresService() {
        this.manager = LlibresManagerImpl.getInstance();
    }


    @ApiOperation(value = "Crear o actualitzar un lector")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Lector creat", response = Lector.class),
            @ApiResponse(code = 200, message = "Lector actualitzat", response = Lector.class),
            @ApiResponse(code = 400, message = "Petició invàlida"),
            @ApiResponse(code = 500, message = "Error intern")
    })
    @POST
    @Path("/lectores")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addOrUpdateLector(Lector l) {
        logger.info("POST /lectores IN: " + l);
        try {
            if (l == null || l.getId() == null || l.getId().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Lector null o id faltant").build();
            }
            // Ajusta los getters según tu clase Lector (getName/getNombre, etc.)
            Lector previo = manager.getLector(l.getId());
            // Aquí uso los getters tal como los llamabas; si tu Lector usa otros nombres, cámbialos.
            Lector out = manager.afegirLector(l.getId(), l.getName(), l.getLastname(), l.getDni(),
                    l.getDataNaixement(), l.getLlocNaixement(), l.getAdreca());
            int status = (previo == null) ? Response.Status.CREATED.getStatusCode() : Response.Status.OK.getStatusCode();
            logger.info("POST /lectores OUT: status=" + status + " id=" + l.getId());
            return Response.status(status).entity(out).build();
        } catch (Exception e) {
            logger.error("POST /lectores error intern", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error intern").build();
        }
    }

    @ApiOperation(value = "Obtenir un lector per ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Lector trobat", response = Lector.class),
            @ApiResponse(code = 400, message = "Id invàlid"),
            @ApiResponse(code = 404, message = "Lector no trobat"),
            @ApiResponse(code = 500, message = "Error intern")
    })
    @GET
    @Path("/lectores/{id}")
    public Response getLector(@PathParam("id") String id) {
        logger.info("GET /lectores/" + id);
        if (id == null || id.trim().isEmpty()) return Response.status(Response.Status.BAD_REQUEST).entity("id invalid").build();
        try {
            Lector l = manager.getLector(id);
            if (l == null) return Response.status(Response.Status.NOT_FOUND).entity("Lector no trobat").build();
            return Response.ok(l).build();
        } catch (Exception e) {
            logger.error("GET /lectores/" + id + " error intern", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error intern").build();
        }
    }

    @ApiOperation(value = "Apilar un llibre al magatzem")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Llibre apilat"),
            @ApiResponse(code = 400, message = "Dades invàlides"),
            @ApiResponse(code = 500, message = "Error intern")
    })
    @POST
    @Path("/libros/store")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response storeLibro(Llibre body) {
        logger.info("POST /libros/store IN: " + body);
        try {
            if (body == null || body.getId() == null || body.getId().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Llibre null o id faltant").build();
            }
            manager.emmagatzemarLlibre(body);
            logger.info("POST /libros/store OUT: CREATED id=" + body.getId());
            return Response.status(Response.Status.CREATED).entity(body).build();
        } catch (Exception e) {
            logger.error("POST /libros/store error intern", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error intern").build();
        }
    }

    @ApiOperation(value = "Catalogar el següent llibre pendent")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Llibre catalogat", response = Catalog.class),
            @ApiResponse(code = 404, message = "No hi ha llibres per catalogar"),
            @ApiResponse(code = 500, message = "Error intern")
    })
    @POST
    @Path("/catalog/next")
    public Response catalogNext() {
        logger.info("POST /catalog/next IN");
        try {
            Catalog c = manager.catalogarLlibre();
            logger.info("POST /catalog/next OUT: OK isbn=" + c.getIdLlibre());
            return Response.ok(c).build();
        } catch (NoBooksToCatalogException e) {
            logger.warn("POST /catalog/next: no books", e);
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            logger.error("POST /catalog/next: error intern", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error intern").build();
        }
    }

    public static class PrestamoRequest {
        public String idPrestec;
        public String idLector;
        public String idLlibre;
        public String dataPrestec;
        public String dataDevolucio;
    }

    @ApiOperation(value = "Crear un préstec (només llibres catalogats)")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Préstec creat", response = Prestec.class),
            @ApiResponse(code = 400, message = "Dades invàlides"),
            @ApiResponse(code = 404, message = "Lector o llibre no existeix"),
            @ApiResponse(code = 409, message = "No hi ha còpies disponibles"),
            @ApiResponse(code = 500, message = "Error intern")
    })
    @POST
    @Path("/prestamos")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPrestamo(PrestamoRequest body) {
        logger.info("POST /prestamos IN: " + body);
        if (body == null || body.idPrestec == null || body.idPrestec.trim().isEmpty()
                || body.idLector == null || body.idLector.trim().isEmpty()
                || body.idLlibre == null || body.idLlibre.trim().isEmpty()
                || body.dataPrestec == null || body.dataDevolucio == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Dades de préstec invàlides").build();
        }
        try {
            Prestec p = manager.prestarLlibre(body.idPrestec, body.idLector, body.idLlibre, body.dataPrestec, body.dataDevolucio);
            return Response.status(Response.Status.CREATED).entity(p).build();
        } catch (LectorNoExisteix e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (LlibreNoCatalogatException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (CopiesNoDisponiblesException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (Exception e) {
            logger.error("POST /prestamos error intern", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error intern").build();
        }
    }


    @ApiOperation(value = "Llistar préstecs d'un lector")
    @GET
    @Path("/prestamos/lector/{idLector}")
    public Response prestamosByLector(@PathParam("idLector") String idLector) {
        logger.info("GET /prestamos/lector/" + idLector);
        if (idLector == null || idLector.trim().isEmpty()) return Response.status(Response.Status.BAD_REQUEST).entity("id invalid").build();
        try {
            Lector lector = manager.getLector(idLector);
            if (lector == null) return Response.status(Response.Status.NOT_FOUND).entity("Lector no trobat").build();
            List<Prestec> l = manager.prestecsLector(idLector);
            return Response.ok(l).build();
        } catch (Exception e) {
            logger.error("GET /prestamos/lector/" + idLector + " error intern", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error intern").build();
        }
    }

    @ApiOperation(value = "Obtenir info de catàleg per id llibre")
    @GET
    @Path("/catalog/{idLlibre}")
    public Response getCatalog(@PathParam("idLlibre") String idLlibre) {
        logger.info("GET /catalog/" + idLlibre);
        if (idLlibre == null || idLlibre.trim().isEmpty()) return Response.status(Response.Status.BAD_REQUEST).entity("id invalid").build();
        try {
            Catalog c = manager.getCatalog(idLlibre);
            if (c == null) return Response.status(Response.Status.NOT_FOUND).entity("No catalog entry").build();
            return Response.ok(c).build();
        } catch (Exception e) {
            logger.error("GET /catalog/" + idLlibre + " error intern", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error intern").build();
        }
    }

}
