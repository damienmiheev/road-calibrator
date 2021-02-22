package com.example.demo.entities;

import com.example.demo.enums.EventTypeEnum;
import com.example.demo.enums.RankEnum;
import java.util.EnumMap;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoadRank extends BaseEntity<String> {
    @Id
    private String roadId;
    private Map<EventTypeEnum, RankEnum> ranks = new EnumMap<>(EventTypeEnum.class);

    @Override
    public String getId() {
        return roadId;
    }
}
