package com.example.demo.configurations;

import com.example.demo.dto.Percentile;
import com.example.demo.enums.RankEnum;
import java.util.EnumMap;
import java.util.Map;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Component
@Validated
@ConfigurationProperties("demo")
public class Properties {

    //Can be overrided in application.properties
    @Min(1)
    private Integer roadsCount = 1000;
    @Min(1)
    private Integer daysCount = 7;

    @NotEmpty
    private Map<RankEnum, Percentile> percentiles = new EnumMap<>(RankEnum.class);
}
