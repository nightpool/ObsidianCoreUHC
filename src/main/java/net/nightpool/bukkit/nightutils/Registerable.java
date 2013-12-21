package net.nightpool.bukkit.nightutils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Registerable {
    public String name();
    public String description();
    public String usage() default "";
    public String[] aliases() default {};
}
