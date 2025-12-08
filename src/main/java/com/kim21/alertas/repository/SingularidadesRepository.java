package com.kim21.alertas.repository;

import com.kim21.alertas.model.SingularidadModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface SingularidadesRepository extends JpaRepository<SingularidadModel, Integer>
{
    @Query("SELECT DISTINCT s.grupoLocal FROM SingularidadModel s WHERE s.grupoLocal IS NOT NULL")
    List<String> obtenerGruposLocalesUnicos();

    @Query("SELECT s FROM SingularidadModel s WHERE s.grupoLocal IN :grupos AND s.fechaReconocimiento IS NULL")
    List<SingularidadModel> findAllByGrupoLocal(@Param("grupos") List<String> grupos);

    @Query("SELECT s FROM SingularidadModel s WHERE s.grupoLocal IN :grupos AND s.fechaReconocimiento IS NOT NULL")
    List<SingularidadModel> findAllLeidasByGrupoLocal(@Param("grupos") List<String> grupos);

    @Query("SELECT s FROM SingularidadModel s " +
            "WHERE (:proceso IS NULL OR s.proceso = :proceso) " +
            "AND (:activo IS NULL OR s.nombreActivo = :activo) " +
            "AND (:grupos IS NULL OR s.grupoLocal IN :grupos) " +
            "AND s.fechaReconocimiento IS NOT NULL " +
            "AND (:initDate IS NULL OR s.fechaSingularidad >= :initDate) " +
            "AND (:endDate IS NULL OR s.fechaSingularidad <= :endDate)")
    List<SingularidadModel> findByProcesoAndGruposAndDateRangeLeidas(
            @Param("proceso") String proceso,
            @Param("activo") String activo,
            @Param("grupos") List<String> grupos,
            @Param("initDate") OffsetDateTime initDate,
            @Param("endDate") OffsetDateTime endDate
    );

    @Query("SELECT s FROM SingularidadModel s " +
            "WHERE (:proceso IS NULL OR s.proceso = :proceso) " +
            "AND (:activo IS NULL OR s.nombreActivo = :activo) " +
            "AND (:grupos IS NULL OR s.grupoLocal IN :grupos) " +
            "AND s.fechaReconocimiento IS NULL " +
            "AND (:initDate IS NULL OR s.fechaSingularidad >= :initDate) " +
            "AND (:endDate IS NULL OR s.fechaSingularidad <= :endDate)")
    List<SingularidadModel> findByProcesoAndGruposAndDateRange(
            @Param("proceso") String proceso,
            @Param("activo") String activo,
            @Param("grupos") List<String> grupos,
            @Param("initDate") OffsetDateTime initDate,
            @Param("endDate") OffsetDateTime endDate
    );

    @Query("SELECT DISTINCT s.proceso FROM SingularidadModel s WHERE s.grupoLocal IN :grupoLocal AND s.proceso IS NOT NULL")
    List<String> findDistinctProcesosByGrupoLocal(List<String> grupoLocal);

    @Query("SELECT DISTINCT s.nombreActivo FROM SingularidadModel s WHERE s.grupoLocal IN :grupoLocal AND s.nombreActivo IS NOT NULL")
    List<String> findAllDistinctActivosAndGrupoLocal(List<String> grupoLocal);

    @Query("SELECT s FROM SingularidadModel s WHERE s.id = :id AND s.grupoLocal IN :grupos")
    List<SingularidadModel> findByIdAndGrupoLocal(@Param("id") Integer id, @Param("grupos") List<String> grupos);

    @Query(value = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'singularidades'", nativeQuery = true)
    List<String> obtenerColumnasDeSingularidades();
}
