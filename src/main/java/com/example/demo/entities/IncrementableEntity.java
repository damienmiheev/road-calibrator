package com.example.demo.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class IncrementableEntity extends BaseEntity<Long> {
    @Id
    private Long id;

    public abstract String getSequenceName();
}
