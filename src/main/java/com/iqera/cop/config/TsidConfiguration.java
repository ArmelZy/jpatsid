package com.iqera.cop.config;

import com.github.f4b6a3.tsid.Tsid;
import com.github.f4b6a3.tsid.TsidFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TsidConfiguration implements WebMvcConfigurer {

    //@Value("${application.tsid.discord-snowflakes-worker}")
    private int discordSnowflakesWorkerId = 1;

    //@Value("${application.tsid.discord-snowflakes-process}")
    private int discordSnowflakesProcessId = 2;

    private static TsidFactory discordSnowflakesFactory;

    public int getDiscordSnowflakesWorkerId() {
        return discordSnowflakesWorkerId;
    }

    public int getDiscordSnowflakesProcessId() {
        return discordSnowflakesProcessId;
    }

    public static TsidFactory getDiscordSnowflakesFactory() {
        return discordSnowflakesFactory;
    }

    public static void setDiscordSnowflakesFactory(TsidFactory discordSnowflakesFactory) {
        TsidConfiguration.discordSnowflakesFactory = discordSnowflakesFactory;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        Converter<String, Tsid> converter = s -> {
            Tsid tsid = null;
            if (StringUtils.hasText(s)) {
                tsid = Tsid.from(s);
            }
            return tsid;
        };

        registry.addConverter(String.class, Tsid.class, converter);
    }
}
