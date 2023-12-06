package com.iqera.cop.tsid;

import com.github.f4b6a3.tsid.TsidFactory;
import com.iqera.cop.config.TsidConfiguration;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class TsidFactoryInitializer {

    private final TsidConfiguration tsidConfiguration;

    public TsidFactoryInitializer(TsidConfiguration tsidConfiguration) {
        this.tsidConfiguration = tsidConfiguration;
    }

    @PostConstruct
    public void initDiscordSnowflakesFactory() {
        int node = getNode();
        // Discord Epoch starts in the first millisecond of 2015
        Instant customEpoch = Instant.parse("2015-01-01T00:00:00.000Z");
        // A factory that returns TSIDs similar to Discord Snowflakes
        TsidConfiguration.setDiscordSnowflakesFactory(TsidFactory.builder().withCustomEpoch(customEpoch).withNode(node).build());
    }

    private int getNode() {
        // Discord Snowflakes have 5 bits for worker ID and 5 bits for process ID
        if (isOutOfRange(tsidConfiguration.getDiscordSnowflakesWorkerId())) {
            throw new IllegalArgumentException(
                "The range of the Discord Snowflakes Worker Id is [0,31]." +
                " The actual value is " +
                tsidConfiguration.getDiscordSnowflakesWorkerId()
            );
        }
        if (isOutOfRange(tsidConfiguration.getDiscordSnowflakesProcessId())) {
            throw new IllegalArgumentException(
                "The range of the Discord Snowflakes Process Id is [0,31]." +
                " The actual value is " +
                tsidConfiguration.getDiscordSnowflakesProcessId()
            );
        }
        int worker = tsidConfiguration.getDiscordSnowflakesWorkerId(); // max: 2^5-1 = 31
        int process = tsidConfiguration.getDiscordSnowflakesProcessId(); // max: 2^5-1 = 31
        return ((worker << 5) | process);
    }

    private boolean isOutOfRange(int id) {
        return id < 0 || id > 31;
    }
}
