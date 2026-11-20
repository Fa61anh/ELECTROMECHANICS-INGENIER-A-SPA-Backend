
package com.electrotrack.actividades.repository;

import com.electrotrack.actividades.model.Evidencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EvidenciaRepository extends JpaRepository<Evidencia, UUID> {

    List<Evidencia> findByActividadId(UUID actividadId);

}
