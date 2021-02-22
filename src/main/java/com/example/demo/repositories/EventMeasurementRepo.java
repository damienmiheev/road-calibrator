package com.example.demo.repositories;

import com.example.demo.entities.EventMeasurement;
import org.springframework.stereotype.Repository;

@Repository
public interface EventMeasurementRepo extends IncrementableRepo<EventMeasurement, Long> {}
