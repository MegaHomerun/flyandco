package com.fly.andco.repository.places;

import com.fly.andco.model.places.TypePlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypePlaceRepository extends JpaRepository<TypePlace, Long> {
}
