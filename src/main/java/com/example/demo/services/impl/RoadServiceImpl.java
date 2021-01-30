package com.example.demo.services.impl;

import com.example.demo.dto.RoadRanksView;
import com.example.demo.repositories.RoadRankRepo;
import com.example.demo.services.RoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoadServiceImpl implements RoadService {

    private final RoadRankRepo roadRankRepo;

    @Override
    public RoadRanksView getRanks(String id) {
        return roadRankRepo.findById(id)
                           .map(entity -> {
                               RoadRanksView view = new RoadRanksView();
                               view.setRanks(entity.getRanks());
                               return view;
                           })
                           .orElse(null);
    }
}
