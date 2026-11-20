package com.electrotrack.jornada.repository;

import com.electrotrack.jornada.model.Jornada;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JornadaRepository extends JpaRepository<Jornada, UUID> {
}
