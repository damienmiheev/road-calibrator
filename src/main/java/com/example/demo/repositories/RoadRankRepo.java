package com.example.demo.repositories;

import com.example.demo.entities.RoadRank;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadRankRepo extends MongoRepository<RoadRank, String> {}
