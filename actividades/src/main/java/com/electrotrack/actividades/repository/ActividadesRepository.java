
package com.electrotrack.actividades.repository;

import com.electrotrack.actividades.model.Actividades;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActividadesRepository extends JpaRepository<Actividades, UUID> {

    List<Actividades> findByServicioId(UUID servicioId);

    List<Actividades> findByTecnicoId(UUID tecnicoId);
}
