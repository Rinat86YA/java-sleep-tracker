package ru.yandex.practicum.sleeptracker.function;

import ru.yandex.practicum.sleeptracker.SleepingSession;
import ru.yandex.practicum.sleeptracker.exception.FunctionException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;

public class DetermineChronotype extends BasicFunction<String> {
    private static final LocalTime time600 = LocalTime.of(6, 0);
    private static final LocalTime time2300 = LocalTime.of(23, 0);
    private static final LocalTime time900 = LocalTime.of(9, 0);
    private static final LocalTime time2200 = LocalTime.of(22, 0);
    private static final LocalTime time700 = LocalTime.of(7, 0);

    public DetermineChronotype(String resultDescription) {
        super(resultDescription);
        super.setFunction(getFunction());
    }

    private Function<List<SleepingSession>, String> getFunction() {
        return list -> {
            if (list == null || list.isEmpty()) {
                throw new FunctionException("Ошибка - нет данных для анализа");
            }

            long owlCount = 0;
            long larkCount = 0;
            long nightSessionsCount = 0;

            for (SleepingSession session : list) {
                if (!isNightSession(session)) {
                    continue;
                }
                nightSessionsCount++;

                LocalTime sleepStartTime = session.getSleepStart().toLocalTime();
                LocalTime sleepEndTime = session.getSleepEnd().toLocalTime();

                if (sleepStartTime.isAfter(time2300) && sleepEndTime.isAfter(time900)) {
                    owlCount++;
                }
                if (sleepStartTime.isBefore(time2200) && sleepEndTime.isBefore(time700)) {
                    larkCount++;
                }
            }

            long pegionCount = nightSessionsCount - owlCount - larkCount;

            if (owlCount > larkCount && owlCount > pegionCount) {
                return "Сова";
            }

            if (larkCount > owlCount && larkCount > pegionCount) {
                return "Жаворонок";
            }

            return "Голубь";
        };
    }

    private boolean isNightSession(SleepingSession session) {
        LocalDateTime sleepStart = session.getSleepStart();
        LocalDateTime sleepEnd = session.getSleepEnd();

        boolean startsBeforeMorning = sleepStart.toLocalTime().isBefore(time600);
        boolean spansTwoDates = sleepStart.toLocalDate().isBefore(sleepEnd.toLocalDate());

        return startsBeforeMorning || spansTwoDates;
    }
}
