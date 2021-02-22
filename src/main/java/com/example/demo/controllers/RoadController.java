package com.example.demo.controllers;

import com.example.demo.dto.RoadRanksView;
import com.example.demo.services.RoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("road")
@RequiredArgsConstructor
public class RoadController {
    private final RoadService roadService;

    @GetMapping("{id}/ranks")
    public RoadRanksView getRanks(@PathVariable("id") String id) {
        return roadService.getRanks(id);
    }

}
