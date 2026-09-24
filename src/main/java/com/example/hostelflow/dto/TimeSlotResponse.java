package com.example.hostelflow.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class TimeSlotResponse {

    private Long id;
    private LocalDate reportingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int capacity;
    private int bookedCount;

    public TimeSlotResponse(
            Long id,
            LocalDate reportingDate,
            LocalTime startTime,
            LocalTime endTime,
            int capacity,
            int bookedCount) {

        this.id = id;
        this.reportingDate = reportingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.bookedCount = bookedCount;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getReportingDate() {
        return reportingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getBookedCount() {
        return bookedCount;
    }
}