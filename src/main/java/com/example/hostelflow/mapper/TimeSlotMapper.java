package com.example.hostelflow.mapper;

import com.example.hostelflow.dto.TimeSlotResponse;
import com.example.hostelflow.model.TimeSlot;
import org.springframework.stereotype.Component;

@Component
public class TimeSlotMapper {
    public TimeSlotResponse toResponse(TimeSlot slot)
    {
        return new TimeSlotResponse(
                slot.getId(),
                slot.getReportingDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getCapacity(),
                slot.getBookedCount()
        );
    }
}
