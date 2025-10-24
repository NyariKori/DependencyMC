package me.nyarikori.commons.annotation.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author NyariKori
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NCommand {
    CommandType commandType();

    String commandName() default "";

    String[] aliases() default {};
}
