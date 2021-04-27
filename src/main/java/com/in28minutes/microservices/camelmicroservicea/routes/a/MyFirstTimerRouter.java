package com.in28minutes.microservices.camelmicroservicea.routes.a;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//@Component
public class MyFirstTimerRouter extends RouteBuilder {

    @Autowired
    private GetCurrentTimeBean getCurrentTimeBean;

    @Autowired
    private SimpleLoggingProcessingComponent loggingComponent;

    @Override
    public void configure() throws Exception {
        // timer
        //transformation
        //log
//        Exchange[ExchangePattern: InOnly, BodyType: null, Body: [Body is null]]
        from("timer:first-timer") //como se fosse uma queue  //nesse ponto o body é null        //endpoint
                .log("${body}") //null
                .transform().constant("My Constant Message") // transformando o body na minha string
                .log("${body}") //My Constant Message
//                .transform().constant("Time now is " + LocalDateTime.now())

                //Processing -- loggingComponent
                //Transformation -- getCurrentTimeBean

                .bean(getCurrentTimeBean, "getCurrentTime")
                .log("${body}")  //Time now is 2021-04-27T15:00:02.406624500
                .bean(loggingComponent)
                .log("${body}")
                .process(new SimpleLoggingProcessor())
                .to("log:first-timer"); //como se fosse um database
    }
}

@Component
class GetCurrentTimeBean {
    public String getCurrentTime() {
        return "Time now is " + LocalDateTime.now(); // criando um Bean para atualizar o time que está sendo passado na mensagem
    }
}

@Component
class SimpleLoggingProcessingComponent {

    private Logger logger = LoggerFactory.getLogger(SimpleLoggingProcessingComponent.class);

    public void process(String message) {

        logger.info("class SimpleLoggingProcessingComponent {}", message);
    }
}

class SimpleLoggingProcessor implements Processor {

    private Logger logger = LoggerFactory.getLogger(SimpleLoggingProcessingComponent.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        logger.info("class SimpleLoggingProcessor {}", exchange.getMessage().getBody());

    }
}
