package com.example.demo.dto;

import com.example.demo.enums.EventTypeEnum;
import com.example.demo.enums.RankEnum;
import java.util.Map;
import lombok.Data;

@Data
public class RoadRanksView {
    private Map<EventTypeEnum, RankEnum> ranks;
}
