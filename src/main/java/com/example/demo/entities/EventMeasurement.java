package com.example.demo.entities;

import com.example.demo.enums.EventTypeEnum;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = EventMeasurement.TABLE_NAME)
@EqualsAndHashCode(callSuper = true)
public class EventMeasurement extends IncrementableEntity {
    @Transient
    public static final String TABLE_NAME = "event_measurements";
    @Transient
    public static final String SEQUENCE_NAME = "event_measurements_sequence";

    private String roadId;
    private EventTypeEnum type;
    private LocalDate measurementDate;
    private Float value;

    //Keep those fields for print to the log perspective
    private Integer totalCount;
    private Integer failedCount;

    @Override
    public String getSequenceName() {
        return SEQUENCE_NAME;
    }
}
