package io.quarkus.cxf.jaxrs.runtime;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

public class CXFJaxrsQuarkusServlet extends CXFNonSpringServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(CXFJaxrsQuarkusServlet.class);

    public static class WebServiceConfig {
        private String path;
        private String className;

        public WebServiceConfig(String path, String className) {
            super();
            this.path = path;
            this.className = className;
        }

        public String getClassName() {
            return className;
        }

        public String getPath() {
            return path;
        }

        @Override
        public String toString() {
            return "Web Service " + className + " on " + path;
        }

    }

    private static final long serialVersionUID = 1L;

    private static final List<WebServiceConfig> WEB_SERVICES = new ArrayList<>();

    private static Bus myBus;

    public static void createServices(String springBus) {
        LOGGER.info("=======try to createServices");
        if (springBus != null && springBus.length() > 0) {
            SpringBusFactory bf = new SpringBusFactory();
            URL busFile = CXFJaxrsQuarkusServlet.class.getClassLoader().getResource(springBus);
            myBus = bf.createBus(busFile.toString());
        } else {
            myBus = BusFactory.newInstance().createBus();
        }
        BusFactory.setDefaultBus(myBus);

        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBus(myBus);

        for (WebServiceConfig config : WEB_SERVICES) {
            try {
                Class<?> serviceClass = Thread.currentThread().getContextClassLoader().loadClass(config.getClassName());

                factory.setServiceClass(serviceClass);
                factory.setAddress(config.getPath());
                factory.setProvider(new JacksonJsonProvider());
                factory.create();
                LOGGER.info(config.toString() + " available.");
            } catch (ClassNotFoundException e) {
                LOGGER.error("Cannot initialize " + config.toString(), e);
            }
        }
    }

    @Override
    public void loadBus(ServletConfig servletConfig) {
        LOGGER.info("=======try to load bus");
        bus = myBus;
    }

    public static void publish(String path, String webService) {
        LOGGER.info("=======service published " + webService);
        WEB_SERVICES.add(new WebServiceConfig(path, webService));
    }
}
