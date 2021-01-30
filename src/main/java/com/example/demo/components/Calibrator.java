package com.example.demo.components;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import com.example.demo.configurations.Properties;
import com.example.demo.entities.EventMeasurement;
import com.example.demo.entities.RoadRank;
import com.example.demo.enums.EventTypeEnum;
import com.example.demo.enums.RankEnum;
import com.example.demo.repositories.RoadRankRepo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Calibrator {

    private final RoadRankRepo roadRankRepo;
    private final MongoTemplate mongoTemplate;

    private final Properties properties;

    public void calibrate() {
        log.info("Start calibrating");

        Map<String, RoadRank> roadRanksByRoadId = new HashMap<>();
        for (EventTypeEnum type : EventTypeEnum.values()) {
            properties.getPercentiles()
                      .forEach((rank, percentile) -> {
                          List<EventMeasurement> roadMeasurementsInRank = getMeasurementsForRankByType(type, rank,
                                  percentile.getFrom(), percentile.getTo());

                          roadMeasurementsInRank.forEach(measurement -> {
                              String roadId = measurement.getRoadId();
                              RoadRank roadRank = roadRanksByRoadId.computeIfAbsent(roadId, id -> {
                                  RoadRank roadRankNew = new RoadRank();
                                  roadRankNew.setRoadId(id);
                                  return roadRankNew;
                              });
                              roadRank.getRanks()
                                      .put(type, rank);
                          });
                      });
        }
        roadRankRepo.saveAll(roadRanksByRoadId.values());
    }

    private List<EventMeasurement> getMeasurementsForRankByType(EventTypeEnum type, RankEnum rank, float lower,
            float upper) {
        Direction direction = null;
        switch (type) {
            case EVENT_1:
            case EVENT_2: {
                direction = Direction.DESC;
                break;
            }
            case EVENT_3:
            case EVENT_4: {
                direction = Direction.ASC;
                break;
            }
        }

        Criteria fromCriteria = new Criteria("value");
        Criteria toCriteria = new Criteria("value");

        switch (rank) {
            case RANK_1: {
                fromCriteria = fromCriteria.gte(lower);
                toCriteria = toCriteria.lte(upper);
                break;
            }
            case RANK_2:
            case RANK_3:
            case RANK_4: {
                fromCriteria = fromCriteria.gt(lower);
                toCriteria = toCriteria.lte(upper);
                break;
            }
        }

        MatchOperation filterByType = match(new Criteria("type").is(type));
        GroupOperation groupAvgOperation = group("roadId").avg("value")
                                                          .as("avgValue");

        ProjectionOperation projectToMatchModel = project().andExpression("_id")
                                                           .as("roadId")
                                                           .andExpression("avgValue")
                                                           .as("value");
        SortOperation sortByAvgValue = sort(Sort.by(direction, "value"));
        MatchOperation filterByValueLover = match(fromCriteria);
        MatchOperation filterByValueUpper = match(toCriteria);

        Aggregation aggregation = newAggregation(filterByType, groupAvgOperation, projectToMatchModel, sortByAvgValue,
                filterByValueLover, filterByValueUpper);

        AggregationResults<EventMeasurement> result = mongoTemplate.aggregate(aggregation, EventMeasurement.TABLE_NAME,
                EventMeasurement.class);

        return result.getMappedResults();
    }

}
