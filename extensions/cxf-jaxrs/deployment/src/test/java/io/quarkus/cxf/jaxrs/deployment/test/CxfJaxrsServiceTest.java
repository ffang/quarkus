package io.quarkus.cxf.jaxrs.deployment.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Supplier;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusDevModeTest;

public class CxfJaxrsServiceTest {

    @RegisterExtension
    public static final QuarkusDevModeTest test = new QuarkusDevModeTest()
            .setArchiveProducer(new Supplier<JavaArchive>() {
                @Override
                public JavaArchive get() {
                    return ShrinkWrap.create(JavaArchive.class)
                            .addClass(HelloServiceImpl.class)
                            .addClass(HelloService.class)
                            .addAsResource("application.properties");
                }
            });

    private HelloServiceImpl helloImpl;
    private HelloService helloProxy;

    {

        final String endpointAddress = "http://localhost:8080/hello";
        helloProxy = JAXRSClientFactory.create(endpointAddress, HelloService.class);
    }

    @BeforeEach
    public void reinstantiateInstances() {
        helloImpl = new HelloServiceImpl();
        //helloProxy = service.getPort(PORT_NAME, HelloService.class);
    }

    @Test
    public void testJaxrs() {
        final String endpointResponse = helloProxy.sayHello("Quarkus");
        final String localResponse = helloImpl.sayHello("Quarkus");
        assertEquals(localResponse, "Hello Quarkus, Welcome to CXF RS Quarkus World!!!");
        assertEquals(endpointResponse, "Hello Quarkus, Welcome to CXF RS Quarkus World!!!");
    }
}
