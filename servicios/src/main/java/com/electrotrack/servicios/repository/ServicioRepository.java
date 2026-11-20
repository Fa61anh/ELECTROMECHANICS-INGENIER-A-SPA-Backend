package com.electrotrack.servicios.repository;

import com.electrotrack.servicios.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ServicioRepository extends JpaRepository<Servicio, UUID> {
}
