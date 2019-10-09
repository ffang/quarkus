package io.quarkus.cxf.jaxrs.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class CxfJaxrsTest {

    @Test
    void testEndpoint() {
        given()
                .when().get("/cxf-jaxrs/sayHello/freeman")
                .then()
                .statusCode(200)
                .body(containsString("Hello , Welcome to CXF RS Quarkus World!!!"));
    }

}
