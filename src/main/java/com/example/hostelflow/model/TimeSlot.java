package com.example.hostelflow.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "time_slots")
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate reportingDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private int bookedCount = 0;

    protected TimeSlot() {
    }

    public TimeSlot(
            LocalDate reportingDate,
            LocalTime startTime,
            LocalTime endTime,
            int capacity) {

        this.reportingDate = reportingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
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