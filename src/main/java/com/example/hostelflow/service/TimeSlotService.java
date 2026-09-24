package com.example.hostelflow.service;

import com.example.hostelflow.dto.TimeSlotResponse;
import com.example.hostelflow.mapper.TimeSlotMapper;
import com.example.hostelflow.model.TimeSlot;
import com.example.hostelflow.repository.TimeSlotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final TimeSlotMapper timeSlotMapper;
    public TimeSlotService(TimeSlotRepository timeSlotRepository, TimeSlotMapper timeSlotMapper) {
        this.timeSlotRepository = timeSlotRepository;
        this.timeSlotMapper=timeSlotMapper;
    }

    public List<TimeSlotResponse> getAvailableSlots() {
        LocalDate today = LocalDate.now();
        LocalTime  now = LocalTime.now();

        return timeSlotRepository.findAllByOrderByReportingDateAsc()
                .stream()
                .filter(slot -> slot.getBookedCount() < slot.getCapacity())
                .filter(slot -> isTimeSlotAvailable(slot,today,now))
                .map(timeSlotMapper::toResponse)
                .toList();
    }
    public boolean isTimeSlotAvailable(TimeSlot slot, LocalDate today, LocalTime now)
    {
        return slot.getReportingDate().isAfter(today) || slot.getReportingDate().equals(today) && slot.getStartTime().isAfter(now);
    }
}
