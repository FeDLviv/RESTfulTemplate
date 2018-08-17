package net.omisoft.rest.configuration;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class HTTPSConditional implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        boolean sslData = (env.getProperty("server.ssl.key-store") != null &&
                env.getProperty("server.ssl.key-store-password") != null &&
                env.getProperty("server.ssl.key-store-type") != null &&
                env.getProperty("server.ssl.key-alias") != null);
        boolean sslPort = (env.getProperty("server.port") != null &&
                env.getProperty("server.port").equals("443"));
        boolean sslActuator = (env.getProperty("management.server.port") == null ||
                env.getProperty("management.server.port").equals("443") ||
                env.getProperty("management.server.port").equals("-1"));
        return sslData && sslPort && sslActuator;
    }

}
