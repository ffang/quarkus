package io.quarkus.cxf.jaxrs.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletConfig;

import org.apache.cxf.Bus;
import org.apache.cxf.cdi.CXFCdiServlet;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CXFJaxrsQuarkusServlet extends CXFCdiServlet {

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

    public static void createServices() {
        LOGGER.info("=======try to createServices");
        /*
         * final BeanManager beanManager = null; //CDI.current().getBeanManager();
         * if (beanManager != null) {
         * final Set<Bean<?>> candidates = beanManager.getBeans("cxf");
         * 
         * if (!candidates.isEmpty()) {
         * final Bean<?> candidate = beanManager.resolve(candidates);
         * 
         * myBus = (Bus) beanManager.getReference(candidate, Bus.class,
         * beanManager.createCreationalContext(candidate));
         * }
         * }
         * 
         * if (myBus == null) {
         * myBus = BusFactory.newInstance().createBus();
         * }
         * 
         * BusFactory.setDefaultBus(myBus);
         * 
         * JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
         * factory.setBus(myBus);
         * 
         * for (WebServiceConfig config : WEB_SERVICES) {
         * try {
         * Class<?> serviceClass = Thread.currentThread().getContextClassLoader().loadClass(config.getClassName());
         * 
         * factory.setServiceClass(serviceClass);
         * factory.setAddress(config.getPath());
         * factory.create();
         * LOGGER.info(config.toString() + " available.");
         * } catch (ClassNotFoundException e) {
         * LOGGER.error("Cannot initialize " + config.toString(), e);
         * }
         * }
         */
    }

    @Override
    public void loadBus(ServletConfig servletConfig) {
        LOGGER.info("=======try to load bus");
        final BeanManager beanManager = CDI.current().getBeanManager();
        if (beanManager != null) {
            final Set<Bean<?>> candidates = beanManager.getBeans("cxf");

            if (!candidates.isEmpty()) {
                final Bean<?> candidate = beanManager.resolve(candidates);

                myBus = (Bus) beanManager.getReference(candidate, Bus.class,
                        beanManager.createCreationalContext(candidate));
            }
        }

        bus = myBus;
        super.loadBus(servletConfig);
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBus(bus);

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
