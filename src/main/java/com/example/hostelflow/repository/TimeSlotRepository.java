package com.example.hostelflow.repository;

import com.example.hostelflow.dto.TimeSlotResponse;
import com.example.hostelflow.model.TimeSlot;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlot,Long> {

    public List<TimeSlot> findAllByOrderByReportingDateAsc();
    @Modifying
    @Transactional
    @Query("UPDATE TimeSlot t SET t.bookedCount = t.bookedCount + 1" +
            " WHERE t.id = :slotId AND t.bookedCount < t.capacity")
    int tryBookSeat(@Param("slotId") Long slotId);
    //The method returns the number of database rows that were actually updated.

}
