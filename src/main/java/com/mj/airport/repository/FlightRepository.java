/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.repository;

import com.mj.airport.model.Flight;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author marko
 */
@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    Optional<Flight> assignGateToFlight(String number);
}
