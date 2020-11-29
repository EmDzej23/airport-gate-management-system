package com.mj.airport.repository;

import com.mj.airport.model.Availability;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marko
 */
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByGateId(Long id);
    void deleteByGateId(Long id);
}
