package org.cl.pdk.duel.annotation;

import org.cl.pdk.duel.DuelState;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StateHandler {
    DuelState state();
}
