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



        // 1. Cantidad de alertas activas
    @Query("SELECT COUNT(a) FROM AlertasModel a WHERE a.valida IS NULL")
    Long countAlertasActivas();

    // 2. Cantidad de alertas agrupadas por proceso
    @Query("SELECT a.proceso, COUNT(a) FROM AlertasModel a GROUP BY a.proceso")
    List<Object[]> countAlertasPorProceso();

    // 3. Cantidad de alertas agrupadas por servicio
    @Query("SELECT a.tipoServicio, COUNT(a) FROM AlertasModel a GROUP BY a.tipoServicio")
    List<Object[]> countAlertasPorServicio();

    // 4. Cantidad de alertas agrupadas por criticidad (severidad)
    @Query("SELECT a.severidad, COUNT(a) FROM AlertasModel a GROUP BY a.severidad")
    List<Object[]> countAlertasPorCriticidad();
    
    @Query("SELECT DISTINCT a.proceso FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.proceso IS NOT NULL")
    List<String> findDistinctProcesosByGrupoLocal(List<String> grupoLocal);

    @Query("SELECT DISTINCT a.nombreActivo FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.nombreActivo IS NOT NULL")
    List<String> findAllDistintActivosAndGrupoLocal(List<String> grupoLocal);

    List<AlertasModel> findAllByAlertaid(Integer alertaId);


    List<AlertasModel> findByValidaIsNullAndGrupoLocalIn(List<String> gruposCoincidentesParaBuscar);




    @Query("SELECT DISTINCT a.codalerta FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.codalerta IS NOT NULL")
    List<String> findDistinctCodalertaByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.nombre FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.nombre IS NOT NULL")
    List<String> findDistinctNombreByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.sentenciaId FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.sentenciaId IS NOT NULL")
    List<Integer> findDistinctSentenciaIdByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.identificacionalerta FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.identificacionalerta IS NOT NULL")
    List<String> findDistinctIdentificacionAlertaByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.latencia FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.latencia IS NOT NULL")
    List<Double> findDistinctLatenciaByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.tipoServicio FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.tipoServicio IS NOT NULL")
    List<String> findDistinctTipoServicioByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.CI FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.CI IS NOT NULL")
    List<String> findDistinctCiByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.Subtiposervicio FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.Subtiposervicio IS NOT NULL")
    List<String> findDistinctSubtipoServicioByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.jitter FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.jitter IS NOT NULL")
    List<Double> findDistinctJitterByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.disponibilidad FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.disponibilidad IS NOT NULL")
    List<Double> findDistinctDisponibilidadByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.packetlost FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.packetlost IS NOT NULL")
    List<Double> findDistinctPacketLostByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.rssi FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.rssi IS NOT NULL")
    List<Double> findDistinctRssiByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.nsr FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.nsr IS NOT NULL")
    List<Double> findDistinctNsrByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.PLM FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.PLM IS NOT NULL")
    List<String> findDistinctPlmByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.tipoExWa FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.tipoExWa IS NOT NULL")
    List<String> findDistinctTipoExWaByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.codigoEvento FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.codigoEvento IS NOT NULL")
    List<String> findDistinctCodigoEventoByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.descripcionevento FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.descripcionevento IS NOT NULL")
    List<String> findDistinctDescripcionEventoByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.Origen FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.Origen IS NOT NULL")
    List<String> findDistinctOrigenByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.tipodocumento FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.tipodocumento IS NOT NULL")
    List<String> findDistinctTipoDocumentoByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.estado FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.estado IS NOT NULL")
    List<String> findDistinctEstadoByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.resumen FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.resumen IS NOT NULL")
    List<String> findDistinctResumenByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.titulo FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.titulo IS NOT NULL")
    List<String> findDistinctTituloByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.numero FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.numero IS NOT NULL")
    List<String> findDistinctNumeroByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.razonestado FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.razonestado IS NOT NULL")
    List<String> findDistinctRazonEstadoByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.gpsx FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.gpsx IS NOT NULL")
    List<Double> findDistinctGpsxByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.gpsy FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.gpsy IS NOT NULL")
    List<Double> findDistinctGpsyByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.gpsz FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.gpsz IS NOT NULL")
    List<Double> findDistinctGpszByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.gpsh FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.gpsh IS NOT NULL")
    List<Double> findDistinctGpshByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.radio FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.radio IS NOT NULL")
    List<String> findDistinctRadioByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.severidad FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.severidad IS NOT NULL")
    List<String> findDistinctSeveridadByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.comentario FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.comentario IS NOT NULL")
    List<String> findDistinctComentarioByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.OT FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.OT IS NOT NULL")
    List<String> findDistinctOtByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.ticket FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.ticket IS NOT NULL")
    List<String> findDistinctTicketByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.prediccion FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.prediccion IS NOT NULL")
    List<String> findDistinctPrediccionByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

    @Query("SELECT DISTINCT a.tiempoReconocimiento FROM AlertasModel a WHERE a.grupoLocal IN :grupoLocal AND a.tiempoReconocimiento IS NOT NULL")
    List<Long> findDistinctTiempoReconocimientoByGrupoLocal(@Param("grupoLocal") List<String> grupoLocal);

        @Query(value = """
        SELECT COLUMN_NAME 
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_NAME = 'alertas'
        """, nativeQuery = true)
    List<String> obtenerColumnasDeAlertas();


    @Query(value = "SELECT * FROM alertas WHERE alertaid = :id", nativeQuery = true)
    Map<String, Object> findRawAlertById(@Param("id") Integer id);
}