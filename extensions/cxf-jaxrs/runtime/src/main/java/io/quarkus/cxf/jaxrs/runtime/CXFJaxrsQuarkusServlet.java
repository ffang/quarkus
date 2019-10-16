package io.quarkus.cxf.jaxrs.runtime;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @Override
    public void loadBus(ServletConfig servletConfig) {
        LOGGER.info("=======try to load bus");
        super.loadBus(servletConfig);

        // You could add the endpoint publish codes here
        Bus bus = getBus();
        BusFactory.setDefaultBus(bus);

        // You can als use the simple frontend API to do this
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBus(bus);

        /*
         * if (WEB_SERVICES.size() == 0) {
         * WEB_SERVICES.add(new WebServiceConfig("/cxf-jaxrs", "io.quarkus.cxf.jaxrs.it.CxfJaxrsResource"));
         * }
         */
        for (WebServiceConfig config : WEB_SERVICES) {
            try {
                Class<?> serviceClass = Thread.currentThread().getContextClassLoader().loadClass(config.getClassName());

                factory.setServiceClass(serviceClass);
                factory.setAddress(config.getPath());
                factory.create();
                LOGGER.info(config.toString() + " available.");
            } catch (ClassNotFoundException e) {
                LOGGER.error("Cannot initialize " + config.toString(), e);
            }
        }
    }

    public static void publish(String path, String webService) {
        LOGGER.info("=======service published " + webService);
        WEB_SERVICES.add(new WebServiceConfig(path, webService));
    }
}
