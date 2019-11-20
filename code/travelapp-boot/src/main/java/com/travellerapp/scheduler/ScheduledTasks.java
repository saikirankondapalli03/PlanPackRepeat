package com.travellerapp.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduledTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Will print the time per 2 seconds
     */
    @Scheduled(fixedRate = 10) // 2 seconds
    public void scheduleTaskWithFixedRate() {
        LOGGER.info("Fixed Rate Task: Execution Time {}", dateTimeFormatter.format(LocalDateTime.now()));
    }

    /**
     * Will print the time per 7 seconds
     * This is due to the initial 2 seconds settings delay of {@code fixedDelay}
     * And the additional 5 seconds of Thread sleep
     */
    @Scheduled(fixedDelay = 2000) // 2 seconds
    public void scheduleTaskWithFixedDelay() {
        LOGGER.info("Fixed Delay Task: Execution Time {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {
            TimeUnit.SECONDS.sleep(5); // 5 seconds thread sleep
        } catch (InterruptedException ex) {
            LOGGER.error("Caught some exception: {}", ex.getMessage());
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Will print the time per 2 seconds
     * But the first printing of time will be delayed by 5 seconds
     */
    @Scheduled(fixedRate = 2000, initialDelay = 5000)
    public void scheduleTaskWithInitialDelay() {
        LOGGER.info("Fixed Rate Task with Initial Delay: Execution Time {}", dateTimeFormatter.format(LocalDateTime.now()));
    }

    /**
     * Will print the time per 1 minute
     * MINUTE / HOUR / DAY OF MONTH / MONTH / DAY OF WEEK
     *
     * For more cron info https://www.pantz.org/software/cron/croninfo.html
     */
    @Scheduled(cron = "0 * * * * ?") // Every 1 minute
    public void scheduleTaskWithCronExpression() {
        LOGGER.info("Fixed Rate Task with Initial Delay: Execution Time {}", dateTimeFormatter.format(LocalDateTime.now()));
    }
}
