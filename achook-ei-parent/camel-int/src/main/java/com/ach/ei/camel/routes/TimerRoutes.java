package com.ach.ei.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class TimerRoutes extends RouteBuilder {

    public static final String ROUTE_NAME = "TIMER_ROUTE";

    @Override
    public void configure() throws Exception {
        from("timer:initial//start?period=10000")
                .routeId(ROUTE_NAME)
                .to("log:executed");
    }
}