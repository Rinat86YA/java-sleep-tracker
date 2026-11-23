package ru.yandex.practicum.sleeptracker.function;

import ru.yandex.practicum.sleeptracker.SleepingSession;
import ru.yandex.practicum.sleeptracker.exception.FunctionException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;

public class CountSleeplessNights extends BasicFunction<Long> {
    private static final LocalTime MIDNIGHT = LocalTime.of(0, 0);
    private static final LocalTime MORNING_6AM = LocalTime.of(6, 0);

    public CountSleeplessNights(String resultDescription) {
        super(resultDescription);
        super.setFunction(getFunction());
    }

    private Function<List<SleepingSession>, Long> getFunction() {
        return list -> {
            if (list == null || list.isEmpty()) {
                throw new FunctionException("Ошибка - нет данных для анализа");
            }

            LocalDateTime periodStart = list.get(0).getSleepStart();
            LocalDateTime periodEnd = list.get(0).getSleepEnd();
            for (SleepingSession session : list) {
                if (session.getSleepStart().isBefore(periodStart)) {
                    periodStart = session.getSleepStart();
                }
                if (session.getSleepEnd().isAfter(periodEnd)) {
                    periodEnd = session.getSleepEnd();
                }
            }

            long sleeplessNights = 0;
            LocalDate date = periodStart.toLocalDate();
            LocalDate lastDate = periodEnd.toLocalDate();

            while (!date.isAfter(lastDate)) {
                if (isNightInPeriod(date, periodStart, periodEnd) && !hasSleepDuringNight(list, date)) {
                    sleeplessNights++;
                }
                date = date.plusDays(1);
            }

            return sleeplessNights;
        };
    }

    private boolean isNightInPeriod(LocalDate date, LocalDateTime periodStart, LocalDateTime periodEnd) {
        LocalDateTime nightStart = LocalDateTime.of(date, MIDNIGHT);
        LocalDateTime nightEnd = LocalDateTime.of(date, MORNING_6AM);

        return nightEnd.isAfter(periodStart) && nightStart.isBefore(periodEnd);
    }

    private boolean hasSleepDuringNight(List<SleepingSession> list, LocalDate date) {
        LocalDateTime nightStart = LocalDateTime.of(date, MIDNIGHT);
        LocalDateTime nightEnd = LocalDateTime.of(date, MORNING_6AM);

        for (SleepingSession session : list) {
            boolean startedBeforeNightEnd = session.getSleepStart().isBefore(nightEnd);
            boolean endedAfterNightStart = session.getSleepEnd().isAfter(nightStart);

            if (startedBeforeNightEnd && endedAfterNightStart) {
                return true;
            }
        }

        return false;
    }
}
