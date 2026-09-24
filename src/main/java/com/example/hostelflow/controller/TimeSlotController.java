package com.example.hostelflow.controller;

import com.example.hostelflow.dto.TimeSlotResponse;
import com.example.hostelflow.service.TimeSlotService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/time-slots")
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    public TimeSlotController(TimeSlotService timeSlotService) {
        this.timeSlotService = timeSlotService;
    }

    @GetMapping
    public List<TimeSlotResponse> getAvailableSlots()
    {
        return timeSlotService.getAvailableSlots();
    }
}
