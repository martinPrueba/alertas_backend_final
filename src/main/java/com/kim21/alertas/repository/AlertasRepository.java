package com.kim21.alertas.repository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kim21.alertas.model.AlertasModel;

@Repository
public interface AlertasRepository extends JpaRepository<AlertasModel, Integer> 
{
        
    @Query("SELECT DISTINCT a.grupoLocal FROM AlertasModel a WHERE a.grupoLocal IS NOT NULL")
    List<String> obtenerGruposLocalesUnicos();


    @Query("SELECT a FROM AlertasModel a WHERE a.grupoLocal IN :grupos AND a.valida IS NULL")
    List<AlertasModel> findAllAlertsByGroupUser(@Param("grupos") List<String> grupos);

    @Query("SELECT a FROM AlertasModel a WHERE a.grupoLocal IN :grupos AND a.valida IS NOT NULL")
    List<AlertasModel> findAllAlertsByGroupUserLeidas(@Param("grupos") List<String> grupos);



        @Query("SELECT a FROM AlertasModel a " +
        "WHERE (:proceso IS NULL OR a.proceso = :proceso) " +
        "AND (:activo IS NULL OR a.nombreActivo = :activo) " +
        "AND (:grupos IS NULL OR a.grupoLocal IN :grupos) " +
        "AND a.valida IS NOT NULL " +
        "AND (:initDate IS NULL OR a.inicioevento >= :initDate) " +
        "AND (:endDate IS NULL OR a.inicioevento <= :endDate)")
        List<AlertasModel> findByProcesoAndGruposAndDateRangeLeidas(
                @Param("proceso") String proceso,
                @Param("activo") String activo,
                @Param("grupos") List<String> grupos,
                @Param("initDate") OffsetDateTime initDate,
                @Param("endDate") OffsetDateTime endDate
        );



        @Query("SELECT a FROM AlertasModel a " +
        "WHERE (:proceso IS NULL OR a.proceso = :proceso) " +
        "AND (:activo IS NULL OR a.nombreActivo = :activo) " +
        "AND (:grupos IS NULL OR a.grupoLocal IN :grupos) " +
        "AND a.valida IS NULL " +
        "AND (:initDate IS NULL OR a.inicioevento >= :initDate) " +
        "AND (:endDate IS NULL OR a.inicioevento <= :endDate)")
        List<AlertasModel> findByProcesoAndGruposAndDateRange(
                @Param("proceso") String proceso,
                @Param("activo") String activo,
                @Param("grupos") List<String> grupos,
                @Param("initDate") OffsetDateTime initDate,
                @Param("endDate") OffsetDateTime endDate
        );



    @Query("SELECT a FROM AlertasModel a WHERE a.valida IS NOT NULL")
    List<AlertasModel> findAllReadAlerts();



@Query("SELECT COUNT(a) " +
       "FROM AlertasModel a " +
       "WHERE a.valida IS NULL AND a.grupoLocal IN :grupos")
Long countAlertasActivasByGrupoLocal(@Param("grupos") List<String> grupos);


@Query("SELECT a.proceso, COUNT(a) " +
       "FROM AlertasModel a " +
       "WHERE a.grupoLocal IN :grupos " +
       "GROUP BY a.proceso")
List<Object[]> countAlertasPorProcesoByGrupoLocal(@Param("grupos") List<String> grupos);


@Query("SELECT a.tipoServicio, COUNT(a) " +
       "FROM AlertasModel a " +
       "WHERE a.grupoLocal IN :grupos " +
       "GROUP BY a.tipoServicio")
List<Object[]> countAlertasPorServicioByGrupoLocal(@Param("grupos") List<String> grupos);


@Query("SELECT a.severidad, COUNT(a) " +
       "FROM AlertasModel a " +
       "WHERE a.grupoLocal IN :grupos " +
       "GROUP BY a.severidad")
List<Object[]> countAlertasPorCriticidadByGrupoLocal(@Param("grupos") List<String> grupos);


    @Query("SELECT DISTINCT a.proceso FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.proceso IS NOT NULL")
    List<String> findDistinctProcesosByGrupoLocal(List<String> grupoLocal);

    @Query("SELECT DISTINCT a.nombreActivo FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.nombreActivo IS NOT NULL")
    List<String> findAllDistintActivosAndGrupoLocal(List<String> grupoLocal);

    List<AlertasModel> findAllByAlertaid(Integer alertaId);


    List<AlertasModel> findByValidaIsNullAndGrupoLocalIn(List<String> gruposCoincidentesParaBuscar);




    @Query(value = "SELECT DISTINCT CAST(:columnName AS VARCHAR) FROM alertas WHERE grupo_local IN (:grupos)", 
        nativeQuery = true)
    List<String> findDistinctDynamic(
            @Param("columnName") String columnName,
            @Param("grupos") List<String> grupos
    );


        @Query(value = """
        SELECT COLUMN_NAME 
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_NAME = 'alertas'
        """, nativeQuery = true)
    List<String> obtenerColumnasDeAlertas();


    @Query(value = "SELECT * FROM alertas WHERE alertaid = :id", nativeQuery = true)
    Map<String, Object> findRawAlertById(@Param("id") Integer id);
}