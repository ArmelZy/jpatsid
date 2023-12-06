package com.iqera.cop.config;

import com.github.f4b6a3.tsid.TsidFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TsidConfiguration {

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
}
