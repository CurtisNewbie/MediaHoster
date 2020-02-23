package com.curtisnewbie.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.logging.Logger;

@ApplicationScoped
public class LoggerProducer {

    @Produces
    public Logger produceLogger(InjectionPoint injectPoint) {
        return Logger.getLogger(injectPoint.getBean().getClass().getName());
    }
}