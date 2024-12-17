package cz.cervenka.parallelizationissues.database.repositories;

import cz.cervenka.parallelizationissues.database.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
}