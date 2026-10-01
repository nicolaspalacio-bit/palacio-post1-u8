package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HallazgoJpaRepository extends JpaRepository<HallazgoJpaEntity, String> {

    @Query("SELECT h.severidad AS categoria, COUNT(h) AS total FROM HallazgoJpaEntity h GROUP BY h.severidad")
    List<ConteoProjection> contarPorSeveridad();

    @Query("SELECT h.estado AS categoria, COUNT(h) AS total FROM HallazgoJpaEntity h GROUP BY h.estado")
    List<ConteoProjection> contarPorEstado();

    // Consulta derivada: evita DATEDIFF (funcion propia de H2) y la comparacion enum = 'texto',
    // que Hibernate 6 rechaza al validar la consulta durante el arranque.
    List<HallazgoJpaEntity> findByEstado(EstadoHallazgo estado);

    interface ConteoProjection {
        String getCategoria();
        Long getTotal();
    }
}