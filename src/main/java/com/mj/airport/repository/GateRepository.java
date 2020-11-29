/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.repository;

import com.mj.airport.model.Gate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author marko
 */
@Repository
public interface GateRepository extends JpaRepository<Gate, Long> {

    Optional<Gate> findByNumber(String number);

    @Query("SELECT g FROM Gate g LEFT JOIN Availability a "
            + "ON g.id = a.gate.id WHERE g.available = TRUE "
            + "AND ("
            + "a.startTime < (:date) AND a.endTime > (:date) "
            + "OR "
            + "(SELECT COUNT(a) from Availability a WHERE a.gate.id = g.id) = 0"
            + ")")
    List<Gate> findAvailableGate(@Param("date") LocalDateTime date);
}
