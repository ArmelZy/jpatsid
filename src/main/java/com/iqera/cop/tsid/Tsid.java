package com.iqera.cop.tsid;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import com.iqera.cop.config.TsidConfiguration;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.annotations.IdGeneratorType;

/**
 * @author Armel Bobda
 */
@IdGeneratorType(TsidGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ FIELD, METHOD })
public @interface Tsid {
    enum Generator {
        DISCORD_SNOWFLAKES {
            @Override
            com.github.f4b6a3.tsid.Tsid random() {
                return TsidConfiguration.getDiscordSnowflakesFactory().create();
            }
        };

        abstract com.github.f4b6a3.tsid.Tsid random();
    }

    Generator generator() default Generator.DISCORD_SNOWFLAKES;
}
