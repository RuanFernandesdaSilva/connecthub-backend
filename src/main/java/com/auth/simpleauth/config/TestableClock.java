package com.auth.simpleauth.config;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

@Component
public class TestableClock extends Clock {

    private Clock baseClock = Clock.systemDefaultZone();

    @Override
    public ZoneId getZone() {
        return baseClock.getZone();
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return baseClock.withZone(zone);
    }

    @Override
    public Instant instant() {
        return baseClock.instant();
    }


    public void adiantarHoras(long horas) {
        this.baseClock = Clock.offset(this.baseClock, Duration.ofHours(horas));
    }


    public void adiantarMinutos(long minutos) {
        this.baseClock = Clock.offset(this.baseClock, Duration.ofMinutes(minutos));
    }


    public void resetar() {
        this.baseClock = Clock.systemDefaultZone();
    }
}