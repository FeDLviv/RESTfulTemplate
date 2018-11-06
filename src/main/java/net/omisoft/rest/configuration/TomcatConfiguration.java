package net.omisoft.rest.configuration;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfiguration {

    @Bean
    public ServletWebServerFactory servletContainer(@Autowired(required = false) Connector redirectConnector) {
        TomcatServletWebServerFactory factory;
        if (redirectConnector != null) {
            factory = new TomcatServletWebServerFactory() {

                @Override
                protected void postProcessContext(Context context) {
                    SecurityConstraint securityConstraint = new SecurityConstraint();
                    securityConstraint.setUserConstraint("CONFIDENTIAL");
                    SecurityCollection securityCollection = new SecurityCollection();
                    securityCollection.addPattern("/*");
                    securityConstraint.addCollection(securityCollection);
                    context.addConstraint(securityConstraint);
                }

            };
            factory.addAdditionalTomcatConnectors(redirectConnector());
        } else {
            factory = new TomcatServletWebServerFactory();
        }
        factory.addConnectorCustomizers(customizer());
        return factory;
    }

    public TomcatConnectorCustomizer customizer() {
        return new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(-1);
            }
        };
    }

    @Bean
    @Conditional(HTTPSConditional.class)
    public Connector redirectConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(80);
        connector.setSecure(false);
        connector.setRedirectPort(443);
        return connector;
    }

}