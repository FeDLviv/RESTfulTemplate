package net.omisoft.rest.configuration.actuator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceEndpoint;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static net.omisoft.rest.ApplicationConstants.API_V1_BASE_PATH;

@Component
@Endpoint(id = "apiTrace")
public class ApiTraceEndpoint {

    @Autowired
    HttpTraceEndpoint httpTrace;

    @ReadOperation
    public List<HttpTrace> getAll() {
        return httpTrace.traces().getTraces().stream()
                .filter(x -> x.getRequest().getUri().toString().contains(API_V1_BASE_PATH))
                .collect(Collectors.toList());
    }

}
