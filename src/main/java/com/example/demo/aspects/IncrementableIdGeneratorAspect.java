package com.example.demo.aspects;

import com.example.demo.entities.IncrementableEntity;
import com.example.demo.services.SequenceGeneratorService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class IncrementableIdGeneratorAspect {

    private final SequenceGeneratorService sequenceGeneratorService;

    @Pointcut("execution(* *..saveAll*(Iterable))")
    public void saveAllMethodPointcut() {
    }

    @Pointcut("execution(* *..save*(com.example.demo.entities.IncrementableEntity))")
    public void saveMethodPointcut() {
    }

    @Pointcut("target(com.example.demo.repositories.IncrementableRepo)")
    public void incrementableRepoPointCut() {
    }

    @SuppressWarnings("unchecked")
    @Before("incrementableRepoPointCut() && saveAllMethodPointcut()")
    public void incrementIdOnSaveAll(JoinPoint jp) {
        List<IncrementableEntity> entities = null;
        try {
            entities = (List<IncrementableEntity>) jp.getArgs()[0];
        } catch (Exception ex) {
            log.error("Failed to cast arg into list of incrementable entity. Miss match aspect?", ex);
        }

        if (entities != null) {
            entities.forEach(entity -> {
                if (entity.getId() == null) {
                    long incrementedId = sequenceGeneratorService.generateSequence(entity.getSequenceName());
                    entity.setId(incrementedId);
                }
            });
        }
    }

    @Before("incrementableRepoPointCut() && saveMethodPointcut()")
    public void incrementIdOnSave(JoinPoint jp) {
        IncrementableEntity entity = null;
        try {
            entity = (IncrementableEntity) jp.getArgs()[0];
        } catch (Exception ex) {
            log.error("Failed to cast arg into incrementable entity. Miss match aspect?", ex);
        }

        if (entity != null) {
            if (entity.getId() == null) {
                long incrementedId = sequenceGeneratorService.generateSequence(entity.getSequenceName());
                entity.setId(incrementedId);
            }
        }
    }
}
