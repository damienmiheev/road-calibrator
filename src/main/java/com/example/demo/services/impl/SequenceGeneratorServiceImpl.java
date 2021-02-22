package com.example.demo.services.impl;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.example.demo.entities.DBSequence;
import com.example.demo.services.SequenceGeneratorService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SequenceGeneratorServiceImpl implements SequenceGeneratorService {
    private final MongoOperations mongoOperations;

    @Override
    public long generateSequence(String seqName) {
        DBSequence counter = mongoOperations.findAndModify(query(where("_id").is(seqName)), new Update().inc("seq", 1),
                options().returnNew(true)
                         .upsert(true), DBSequence.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }
}
