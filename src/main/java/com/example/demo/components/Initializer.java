package com.example.demo.components;

import com.example.demo.configurations.Properties;
import com.example.demo.entities.EventMeasurement;
import com.example.demo.enums.EventTypeEnum;
import com.example.demo.repositories.EventMeasurementRepo;
import com.example.demo.utilities.ListHelper;
import de.vandermeer.asciitable.AsciiTable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Initializer {
    private static final Integer MAX_EVENT_COUNT = 1000;
    private static final String TEMP_CELL = "...";

    private final Properties properties;
    private final Calibrator calibrator;
    private final EventMeasurementRepo eventMeasurementRepo;

    @PostConstruct
    public void generateData() {
        Integer roadsCount = properties.getRoadsCount();
        Integer daysCount = properties.getDaysCount();

        List<EventMeasurement> measurementList = new ArrayList<>();

        //generate 1000 (roadCount) documents of measurement for for each day for each event type
        for (int daysSinceToday = 0; daysSinceToday < daysCount; daysSinceToday++) {
            LocalDate measurementDate = LocalDate.now()
                                                 .minusDays(daysSinceToday);

            List<EventMeasurement> dailyMeasurements = new ArrayList<>();

            for (int roadId = 0; roadId < roadsCount; roadId++) {
                int totalCalls = RandomUtils.nextInt(1, MAX_EVENT_COUNT);
                int totalDataEvents = RandomUtils.nextInt(1, MAX_EVENT_COUNT);
                int droppedCalls = RandomUtils.nextInt(totalCalls / 2, totalCalls);
                int dataLeakageEvents = RandomUtils.nextInt(totalDataEvents / 2, totalDataEvents);

                for (EventTypeEnum type : EventTypeEnum.values()) {
                    EventMeasurement eventMeasurement = generateMeasurement(Integer.toString(roadId), measurementDate,
                            type, totalCalls, droppedCalls, totalDataEvents, dataLeakageEvents);
                    dailyMeasurements.add(eventMeasurement);
                }
            }

            measurementList.addAll(dailyMeasurements);
            printQualityEventsTable(measurementDate, dailyMeasurements);
        }

        log.info("Data successfully initialized");
        eventMeasurementRepo.saveAll(measurementList);
        calibrator.calibrate();
    }

    @PreDestroy
    public void cleanUpData() {
        //no need to clean up, because embedded DB
    }

    private EventMeasurement generateMeasurement(String roadId, LocalDate date, EventTypeEnum type, int totalCalls,
            int droppedCalls, int totalDataEvents, int dataLeakageEvents) {
        EventMeasurement retVal = new EventMeasurement();
        retVal.setMeasurementDate(date);
        retVal.setRoadId(roadId);
        retVal.setType(type);

        switch (type) {
            case EVENT_1:
            case EVENT_3: {
                setData(retVal, totalCalls, droppedCalls);
                break;
            }
            case EVENT_2:
            case EVENT_4: {
                setData(retVal, totalDataEvents, dataLeakageEvents);
                break;
            }
        }

        return retVal;
    }

    private void setData(EventMeasurement measurement, int totalCount, int failedCount) {
        measurement.setTotalCount(totalCount);
        measurement.setFailedCount(failedCount);
        measurement.setValue(calculateValue(totalCount, failedCount, measurement.getType()));
    }

    private Float calculateValue(int totalCount, int failedCount, EventTypeEnum type) {
        Float retVal = null;

        switch (type) {
            case EVENT_1:
            case EVENT_2: {
                retVal = calculateRate(totalCount, failedCount);
                break;
            }
            case EVENT_3:
            case EVENT_4: {
                retVal = calculateScore(totalCount, failedCount);
                break;
            }
        }

        return retVal;
    }

    private float calculateRate(int totalCount, int failedCount) {
        return (float) failedCount / totalCount;
    }

    private Float calculateScore(int totalCount, int failedCount) {
        return 1 - calculateRate(totalCount, failedCount);
    }

    private void printQualityEventsTable(LocalDate date, List<EventMeasurement> measurementList) {
        AsciiTable at = new AsciiTable();
        at.addRule();

        List<String> headers = new ArrayList<>();
        headers.add("Road #");

        EventTypeEnum[] types = EventTypeEnum.values();
        for (EventTypeEnum type : types) {
            headers.add(type.name());
        }

        at.addRow(headers);
        at.addRule();

        Map<Integer, List<EventMeasurement>> measurementsPerRoad = ListHelper.breakToChunksByMaxChunkSize(
                measurementList, types.length);

        //print only first three roads
        for (int i = 1; i <= 3; i++) {
            List<EventMeasurement> eventMeasurements = measurementsPerRoad.get(i);
            addRowToQualityEventsTable(at, i, eventMeasurements);
        }

        //print temporary row
        at.addRow(TEMP_CELL, TEMP_CELL, TEMP_CELL, TEMP_CELL, TEMP_CELL);
        at.addRule();

        //print last road
        int lastRoadId = measurementsPerRoad.size() - 1;
        List<EventMeasurement> lastEventMeasurements = measurementsPerRoad.get(lastRoadId);
        addRowToQualityEventsTable(at, lastRoadId, lastEventMeasurements);
        log.info("Day: {}\n{}", date, at.render());
    }

    private void addRowToQualityEventsTable(AsciiTable at, int roadId, List<EventMeasurement> eventMeasurements) {
        List<String> values = new ArrayList<>();
        values.add(Integer.toString(roadId));
        eventMeasurements.forEach(eventMeasurement -> {
            Integer totalCount = eventMeasurement.getTotalCount();
            Integer failedCount = eventMeasurement.getFailedCount();

            String value = null;
            switch (eventMeasurement.getType()) {
                case EVENT_1:
                case EVENT_2: {
                    value = failedCount + "/" + totalCount;
                    break;
                }
                case EVENT_3:
                case EVENT_4: {
                    value = totalCount - failedCount + "/" + totalCount;
                    break;
                }
            }

            values.add(value);
        });

        at.addRow(values);
        at.addRule();
    }
}