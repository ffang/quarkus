package io.quarkus.cxf.jaxrs.it;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/sayHello")
@ApplicationScoped
public class CxfJaxrsResource {

    @GET
    @Path("/{a}")
    @Produces(MediaType.TEXT_PLAIN)
    public String sayHello(String a) {
        return "Hello " + a + ", Welcome to CXF RS Quarkus World!!!";
    }

}
