package com.kim21.alertas.service;

import com.kim21.alertas.dto.SingularidadMarcarLeidaDTO;
import com.kim21.alertas.dto.SingularidadReportDTO;
import com.kim21.alertas.model.ProcessAssociateIconModel;
import com.kim21.alertas.model.SingularidadModel;
import com.kim21.alertas.model.SingularidadesVisibleFieldModel;
import com.kim21.alertas.repository.ProcessAssociateIconRepository;
import com.kim21.alertas.repository.SingularidadEstadisticasVisibleFieldRepository;
import com.kim21.alertas.repository.SingularidadesEstadisticasRepository;
import com.kim21.alertas.repository.SingularidadesRepository;
import com.kim21.alertas.repository.SingularidadesVisibleFieldFilterRepository;
import com.kim21.alertas.repository.SingularidadesVisibleFieldRepository;
import com.kim21.alertas.util.GroupUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class SingularidadesServiceImpl implements SingularidadesService
{

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SingularidadesRepository singularidadesRepository;

    @Autowired
    private SingularidadesVisibleFieldRepository visibleFieldRepository;

    @Autowired
    private SingularidadEstadisticasVisibleFieldRepository singularidadEstadisticasVisibleFieldRepository;

    @Autowired
    private SingularidadesVisibleFieldFilterRepository singularidadesVisibleFieldFilterRepository;

    @Autowired
    private SingularidadesEstadisticasRepository singularidadesEstadisticasRepository;

    @Autowired
    private ProcessAssociateIconRepository processAssociateIconRepository;

    private static final Map<String, String> COLUMN_TO_FIELD = Map.of(
            "fecha_reconocimiento", "fechaReconocimiento",
            "tiempo_reconocimiento", "tiempoReconocimiento",
            "grupolocal", "grupoLocal",
            "fecha_alerta", "fechaAlerta",
            "fechasingularidad", "fechaSingularidad"
    );

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public ResponseEntity<?> findAllSingularidades()
    {
        try
        {
            List<String> gruposCoincidentesParaBuscar = obtenerGruposCoincidentesConSingularidades();
            if (gruposCoincidentesParaBuscar.isEmpty())
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No existen grupos asociados al usuario."));
            }

            List<SingularidadModel> singularidades = singularidadesRepository.findAllByGrupoLocal(gruposCoincidentesParaBuscar);
            List<String> camposVisibles = visibleFieldRepository.findAll()
                    .stream()
                    .filter(SingularidadesVisibleFieldModel::getVisible)
                    .map(SingularidadesVisibleFieldModel::getFieldName)
                    .collect(Collectors.toList());

            List<Map<String, Object>> singularidadesVisibles = new ArrayList<>();
            for (SingularidadModel singularidad : singularidades)
            {
                Map<String, Object> visibleData = buildVisibleData(camposVisibles, singularidad);
                visibleData.put("id", singularidad.getId());

                //Agregar icono 
                //agregamos campo de iconAssocieteFromProceso en ProcessAssociateIconModel
                Optional<ProcessAssociateIconModel> findIconUrl = processAssociateIconRepository.findByProceso(singularidad.getProceso());

                if(!findIconUrl.isPresent())
                {
                    //alerta.setIconAssocieteFromProceso("No existe un icono asociado al proceso.");
                    visibleData.put("IconAssocieteFromProceso", "No existe un icono asociado al proceso.");
                }
                else
                {
                    //buscar la url asociada al proceso en 
                    //alerta.setIconAssocieteFromProceso(findIconUrl.get().getIconUrl());

                    visibleData.put("IconAssocieteFromProceso", findIconUrl.get().getIconUrl());
                }  
                singularidadesVisibles.add(visibleData);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("singularidades", singularidadesVisibles);
            response.put("singularidadesLeidas", getSingularidadesLeidas());

            return ResponseEntity.ok(List.of(response));
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al obtener singularidades", "details", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> findSingularidadById(Integer id)
    {
        try
        {
            List<String> gruposCoincidentesParaBuscar = obtenerGruposCoincidentesConSingularidades();
            if (gruposCoincidentesParaBuscar.isEmpty())
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No existen grupos asociados al usuario."));
            }

            List<SingularidadModel> singularidades = singularidadesRepository.findByIdAndGrupoLocal(id, gruposCoincidentesParaBuscar);
            if (singularidades.isEmpty())
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No existe la singularidad"));
            }

            List<String> camposVisibles = visibleFieldRepository.findAll()
                    .stream()
                    .filter(SingularidadesVisibleFieldModel::getVisible)
                    .map(SingularidadesVisibleFieldModel::getFieldName)
                    .collect(Collectors.toList());

            camposVisibles = new ArrayList<>(camposVisibles);
            camposVisibles.sort(String.CASE_INSENSITIVE_ORDER);

            List<Map<String, Object>> singularidadesVisibles = new ArrayList<>();
            for (SingularidadModel singularidad : singularidades)
            {
                Map<String, Object> visibleData = new LinkedHashMap<>();
                for (String campo : camposVisibles)
                {
                    try
                    {
                        String fieldName = COLUMN_TO_FIELD.getOrDefault(campo, campo);
                        String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

                        Method getter = SingularidadModel.class.getMethod(getterName);
                        Object valor = getter.invoke(singularidad);

                        if (("fecha_reconocimiento".equalsIgnoreCase(campo) || "fecha_alerta".equalsIgnoreCase(campo) || "fechasingularidad".equalsIgnoreCase(campo)) && valor != null)
                        {
                            OffsetDateTime fecha = (OffsetDateTime) valor;
                            valor = fecha.format(FORMATTER);
                        }

                        visibleData.put(campo, valor);
                    }
                    catch (Exception e)
                    {
                        System.err.println("Error accediendo al campo: " + campo + " ƒÅ' " + e.getMessage());
                    }
                }

                Optional<ProcessAssociateIconModel> findIconUrl = processAssociateIconRepository.findByProceso(singularidad.getProceso());

                if(!findIconUrl.isPresent())
                {
                    visibleData.put("IconAssocieteFromProceso", "No existe un icono asociado al proceso.");
                }
                else
                {
                    visibleData.put("IconAssocieteFromProceso", findIconUrl.get().getIconUrl());
                }

                singularidadesVisibles.add(visibleData);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("singularidades", singularidadesVisibles);

            return ResponseEntity.ok(List.of(response));
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al obtener singularidad", "details", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> marcarSingularidadComoLeida(SingularidadMarcarLeidaDTO dto)
    {
        try
        {
            Integer singularidadId = dto.getId() != null ? dto.getId() : dto.getSingularidadId();
            Optional<SingularidadModel> singularidadOpt = singularidadesRepository.findById(singularidadId);
            if (singularidadOpt.isEmpty())
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No se encontró la singularidad"));
            }

            SingularidadModel singularidad = singularidadOpt.get();

            if (singularidad.getFechaReconocimiento() != null)
            {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "La singularidad ya fue marcada como leída anteriormente"));
            }

            String usuario = System.getProperty("user.name");
            if (usuario == null || usuario.isBlank())
            {
                usuario = "desconocido";
            }

            if (dto.getComentario() != null && dto.getComentario().length() > 80)
            {
                return ResponseEntity.badRequest().body(Map.of("message", "El comentario no puede exceder los 80 caracteres."));
            }

            OffsetDateTime fechaReconocimiento = OffsetDateTime.now();
            OffsetDateTime fechaEvento = singularidad.getFechaSingularidad();
            long tiempoReconocimiento = 0;
            if (fechaEvento != null)
            {
                tiempoReconocimiento = Duration.between(fechaEvento, fechaReconocimiento).toMinutes();
            }

            singularidad.setUserId(usuario);
            singularidad.setComentario(dto.getComentario());
            singularidad.setValida(dto.isValida());
            singularidad.setCodigo1(dto.getCodigo1());
            singularidad.setCodigo2(dto.getCodigo2());
            singularidad.setFechaReconocimiento(fechaReconocimiento);
            singularidad.setTiempoReconocimiento(tiempoReconocimiento);

            singularidadesRepository.save(singularidad);

            return ResponseEntity.ok(Map.of(
                    "message", "La singularidad fue marcada como leída correctamente.",
                    "id", singularidad.getId(),
                    "usuario", usuario,
                    "fecha_reconocimiento", fechaReconocimiento,
                    "tiempo_reconocimiento_min", tiempoReconocimiento
            ));

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al marcar la singularidad como leída", "details", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> getProcesos()
    {
        try
        {
            List<String> gruposCoincidentesParaBuscar = obtenerGruposCoincidentesConSingularidades();
            if (gruposCoincidentesParaBuscar.isEmpty())
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No existen grupos asociados al usuario."));
            }

            List<String> procesos = singularidadesRepository.findDistinctProcesosByGrupoLocal(gruposCoincidentesParaBuscar);

            if (procesos.isEmpty())
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No hay procesos asociados al usuario."));
            }

            return ResponseEntity.ok(procesos);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Ha ocurrido un error interno"));
        }
    }

    @Override
    public ResponseEntity<?> getActivos()
    {
        try
        {
            List<String> gruposCoincidentesParaBuscar = obtenerGruposCoincidentesConSingularidades();
            if (gruposCoincidentesParaBuscar.isEmpty())
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No existen grupos asociados al usuario."));
            }

            List<String> activos = singularidadesRepository.findAllDistinctActivosAndGrupoLocal(gruposCoincidentesParaBuscar);

            if (activos.isEmpty())
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No hay activos asociados al usuario."));
            }
            return ResponseEntity.ok(activos);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Ha ocurrido un error interno"));
        }
    }


    public List<Object> getDistinctValuesDynamicSingularidades(String columnName, List<String> grupos)
    {
        String sql = "SELECT DISTINCT " + columnName + " FROM singularidades WHERE grupolocal IN :grupos AND " + columnName + " IS NOT NULL";

        return entityManager.createNativeQuery(sql)
                .setParameter("grupos", grupos)
                .getResultList();
    }

    @Override
    public ResponseEntity<?> getTiposSingularidades()
    {
        try
        {
            List<String> grupos = obtenerGruposCoincidentesConSingularidades();
            if (grupos.isEmpty())
            {
                return ResponseEntity.status(404).body(Map.of("message", "No existen grupos asociados al usuario."));
            }

            List<String> columnas = singularidadesRepository.obtenerColumnasDeSingularidades();

            Map<String, Object> tipos = new HashMap<>();

            for (String columna : columnas)
            {
                try
                {
                    List<Object> valores = getDistinctValuesDynamicSingularidades(columna, grupos);
                    if (valores != null && !valores.isEmpty())
                    {
                        tipos.put(columna, valores);
                    }
                }
                catch (Exception e)
                {
                    System.out.println("Error obteniendo tipo para columna: " + columna);
                }
            }

            if (tipos.isEmpty())
            {
                return ResponseEntity.status(404).body(Map.of("message", "No existen tipos asociados."));
            }

            return ResponseEntity.ok(tipos);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error interno"));
        }
    }

    @Override
    public ResponseEntity<?> reportSingularidadesDynamic(SingularidadReportDTO dto)
    {
        try
        {
            if (dto.getSingularidades() == null)
            {
                return ResponseEntity.badRequest().body(Map.of("error", "La lista de singularidades esta vacia o no fue enviada."));
            }

            Map<String, Object> mapaReporte = new HashMap<>();

            int singActivas = dto.getSingularidades() != null ? dto.getSingularidades().size() : 0;
            mapaReporte.put("cantidadActivas", singActivas);

            Map<String, Integer> mapProceso = new HashMap<>();
            Map<String, Integer> mapTipoServicio = new HashMap<>();
            Map<String, Integer> mapTipoCurva = new HashMap<>();

            for (Object obj : dto.getSingularidades())
            {
                Map<String, Object> map = (Map<String, Object>) obj;
                String proceso = (String) map.get("proceso");
                if (proceso != null)
                {
                    mapProceso.put(proceso, mapProceso.getOrDefault(proceso, 0) + 1);
                }

                String tipoServicio = (String) map.get("tipoServicio");
                if (tipoServicio != null)
                {
                    mapTipoServicio.put(tipoServicio, mapTipoServicio.getOrDefault(tipoServicio, 0) + 1);
                }

                String tipocurva = (String) map.get("tipocurva");
                if (tipocurva != null)
                {
                    mapTipoCurva.put(tipocurva, mapTipoCurva.getOrDefault(tipocurva, 0) + 1);
                }
            }

            if (dto.getSingularidadesLeidas() != null)
            {
                for (Object obj : dto.getSingularidadesLeidas())
                {
                    Map<String, Object> map = (Map<String, Object>) obj;
                    String proceso = (String) map.get("proceso");
                    if (proceso != null)
                    {
                        mapProceso.put(proceso, mapProceso.getOrDefault(proceso, 0) + 1);
                    }

                    String tipoServicio = (String) map.get("tipoServicio");
                    if (tipoServicio != null)
                    {
                        mapTipoServicio.put(tipoServicio, mapTipoServicio.getOrDefault(tipoServicio, 0) + 1);
                    }

                    String tipocurva = (String) map.get("tipocurva");
                    if (tipocurva != null)
                    {
                        mapTipoCurva.put(tipocurva, mapTipoCurva.getOrDefault(tipocurva, 0) + 1);
                    }
                }
            }

            mapaReporte.put("singularidadesPorProceso", mapProceso);
            mapaReporte.put("singularidadesPorTipoServicio", mapTipoServicio);
            mapaReporte.put("singularidadesPorTipoCurva", mapTipoCurva);

            return ResponseEntity.ok(mapaReporte);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Ha ocurrido un error interno en el reporte dinamico."));
        }
    }

    private String normalizeGroup(String s)
    {
        if (s == null) return "";
        return s.trim()
                .replace("\\\\", "\\")
                .toUpperCase(Locale.ROOT);
    }

    private List<String> obtenerGruposCoincidentesConSingularidades() throws IOException
    {
        List<String> gruposUsuario = GroupUtils.getCurrentUserGroups();
        if (gruposUsuario.isEmpty()) return Collections.emptyList();

        List<String> gruposEnBD = singularidadesRepository.obtenerGruposLocalesUnicos();

        Set<String> gruposBDSet = gruposEnBD.stream()
                .map(this::normalizeGroup)
                .collect(Collectors.toSet());

        return gruposUsuario.stream()
                .map(this::normalizeGroup)
                .filter(gruposBDSet::contains)
                .collect(Collectors.toList());
    }

    private Map<String, Object> buildVisibleData(List<String> camposVisibles, SingularidadModel singularidad)
    {
        Map<String, Object> visibleData = new HashMap<>();

        for (String campo : camposVisibles)
        {
            try
            {
                String fieldName = COLUMN_TO_FIELD.getOrDefault(campo, campo);
                String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

                Method getter = SingularidadModel.class.getMethod(getterName);

                Object valor = getter.invoke(singularidad);

                if (("fecha_reconocimiento".equalsIgnoreCase(campo) || "fecha_alerta".equalsIgnoreCase(campo) || "fechasingularidad".equalsIgnoreCase(campo)) && valor != null)
                {
                    OffsetDateTime fecha = (OffsetDateTime) valor;
                    valor = fecha.format(FORMATTER);
                }

                visibleData.put(campo, valor);
            }
            catch (Exception e)
            {
                System.err.println("Error accediendo al campo: " + campo + " → " + e.getMessage());
            }
        }

        Optional<ProcessAssociateIconModel> findIconUrl = processAssociateIconRepository.findByProceso(singularidad.getProceso());
        if (findIconUrl.isPresent())
        {
            visibleData.put("IconAssocieteFromProceso", findIconUrl.get().getIconUrl());
        }
        else
        {
            visibleData.put("IconAssocieteFromProceso", "No existe un icono asociado al proceso.");
        }

        return visibleData;
    }

    private List<Map<String, Object>> getSingularidadesLeidas()
    {
        try
        {
            List<String> gruposCoincidentesParaBuscar = obtenerGruposCoincidentesConSingularidades();

            List<SingularidadModel> singularidades = singularidadesRepository.findAllLeidasByGrupoLocal(gruposCoincidentesParaBuscar);
            List<String> camposVisibles = visibleFieldRepository.findAll()
                    .stream()
                    .filter(SingularidadesVisibleFieldModel::getVisible)
                    .map(SingularidadesVisibleFieldModel::getFieldName)
                    .collect(Collectors.toList());

            List<Map<String, Object>> singularidadesVisibles = new ArrayList<>();
            for (SingularidadModel singularidad : singularidades)
            {
                Map<String, Object> visibleData = buildVisibleData(camposVisibles, singularidad);
                                Optional<ProcessAssociateIconModel> findIconUrl = processAssociateIconRepository.findByProceso(singularidad.getProceso());
                visibleData.put("id", singularidad.getId());

                if(!findIconUrl.isPresent())
                {
                    //alerta.setIconAssocieteFromProceso("No existe un icono asociado al proceso.");
                    visibleData.put("IconAssocieteFromProceso", "No existe un icono asociado al proceso.");
                }
                else
                {
                    //buscar la url asociada al proceso en 
                    //alerta.setIconAssocieteFromProceso(findIconUrl.get().getIconUrl());

                    visibleData.put("IconAssocieteFromProceso", findIconUrl.get().getIconUrl());
                }  
                singularidadesVisibles.add(visibleData);
            }

            return singularidadesVisibles;

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private List<Map<String, Object>> getSingularidadesLeidasByFilter(String proceso, String activo, List<String> grupos, OffsetDateTime initDate, OffsetDateTime endDate)
    {
        try
        {
            List<SingularidadModel> singularidades = singularidadesRepository.findByProcesoAndGruposAndDateRangeLeidas(
                    proceso,
                    activo,
                    grupos,
                    initDate,
                    endDate
            );

            List<String> camposVisibles = visibleFieldRepository.findAll()
                    .stream()
                    .filter(SingularidadesVisibleFieldModel::getVisible)
                    .map(SingularidadesVisibleFieldModel::getFieldName)
                    .collect(Collectors.toList());

            List<Map<String, Object>> singularidadesVisibles = new ArrayList<>();
            for (SingularidadModel singularidad : singularidades)
            {
                Map<String, Object> visibleData = buildVisibleData(camposVisibles, singularidad);
                singularidadesVisibles.add(visibleData);
            }

            return singularidadesVisibles;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }



    @Override
public ResponseEntity<?> filtrarDinamico(Map<String, Object> filtros)
{
    try
    {
        List<String> gruposUsuario = obtenerGruposCoincidentesConSingularidades();
        if (gruposUsuario == null || gruposUsuario.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No existen grupos asociados al usuario."));
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SingularidadModel> query = cb.createQuery(SingularidadModel.class);
        Root<SingularidadModel> root = query.from(SingularidadModel.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(root.get("grupoLocal").in(gruposUsuario));

        Object fechaInicioObj = filtros.get("fechaInicio");
        Object fechaFinObj = filtros.get("fechaFin");

        boolean tieneInicio = fechaInicioObj != null && !fechaInicioObj.toString().isBlank();
        boolean tieneFin = fechaFinObj != null && !fechaFinObj.toString().isBlank();

        if (tieneInicio || tieneFin)
        {
            try
            {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                LocalDate fechaInicio = tieneInicio ? LocalDate.parse(fechaInicioObj.toString(), formatter) : null;
                LocalDate fechaFin = tieneFin ? LocalDate.parse(fechaFinObj.toString(), formatter) : null;

                ZoneOffset offset = ZoneOffset.of("-03:00");

                OffsetDateTime inicioDelDia = fechaInicio != null ? fechaInicio.atStartOfDay().atOffset(offset) : null;
                OffsetDateTime finDelDia = fechaFin != null ? fechaFin.atTime(23, 59, 59).atOffset(offset) : null;

                Path<OffsetDateTime> campoFecha = root.get("fechaSingularidad");

                if (inicioDelDia != null && finDelDia != null)
                {
                    predicates.add(cb.between(campoFecha, inicioDelDia, finDelDia));
                }
                else if (inicioDelDia != null)
                {
                    predicates.add(cb.greaterThanOrEqualTo(campoFecha, inicioDelDia));
                }
                else
                {
                    predicates.add(cb.lessThanOrEqualTo(campoFecha, finDelDia));
                }
            }
            catch (Exception e)
            {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Formato de fecha invalido",
                        "detalle", "Usa formato: 2025-10-04 (solo dia/mes/anio)"
                ));
            }
        }

        filtros.forEach((campo, valor) ->
        {
            if (valor == null || campo == null)
            {
                return;
            }

            if (campo.equalsIgnoreCase("fechaInicio") || campo.equalsIgnoreCase("fechaFin"))
            {
                return;
            }

            try
            {
                Path<Object> path = root.get(campo);

                if (campo.equalsIgnoreCase("valida"))
                {
                    predicates.add(cb.equal(root.get("valida"), Boolean.valueOf(valor.toString())));
                    return;
                }

                if (valor instanceof String)
                {
                    predicates.add(cb.like(cb.lower(path.as(String.class)),
                            "%" + valor.toString().toLowerCase() + "%"));
                }
                else if (valor instanceof Number)
                {
                    predicates.add(cb.equal(path, valor));
                }
                else if (valor instanceof Boolean)
                {
                    predicates.add(cb.equal(path.as(Boolean.class), (Boolean) valor));
                }
                else if (valor instanceof Map)
                {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> rango = (Map<String, Object>) valor;

                    if (rango.containsKey("min") && rango.containsKey("max"))
                    {
                        predicates.add(cb.between(
                                path.as(Double.class),
                                cb.literal(((Number) rango.get("min")).doubleValue()),
                                cb.literal(((Number) rango.get("max")).doubleValue())
                        ));
                    }
                    else if (rango.containsKey("min"))
                    {
                        predicates.add(cb.greaterThanOrEqualTo(
                                path.as(Double.class),
                                cb.literal(((Number) rango.get("min")).doubleValue())
                        ));
                    }
                    else if (rango.containsKey("max"))
                    {
                        predicates.add(cb.lessThanOrEqualTo(
                                path.as(Double.class),
                                cb.literal(((Number) rango.get("max")).doubleValue())
                        ));
                    }
                }

            }
            catch (IllegalArgumentException e)
            {
                System.out.println("Campo ignorado: " + campo);
            }
        });

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        List<SingularidadModel> result = entityManager.createQuery(query).getResultList();

        List<SingularidadModel> singularidadesNormales = new ArrayList<>();
        List<SingularidadModel> singularidadesLeidas = new ArrayList<>();

        for (SingularidadModel singularidad : result)
        {
            if (singularidad.getValida() == null)
            {
                singularidadesNormales.add(singularidad);
            }
            else
            {
                singularidadesLeidas.add(singularidad);
            }
        }

        Map<String, String> iconMap = processAssociateIconRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        ProcessAssociateIconModel::getProceso,
                        ProcessAssociateIconModel::getIconUrl
                ));

        List<SingularidadModel> normalesConIcono = singularidadesNormales.stream()
                .peek(s -> s.setIconAssocieteFromProceso(
                        iconMap.getOrDefault(s.getProceso(), "No existe un icono asociado al proceso.")
                ))
                .collect(Collectors.toList());

        List<SingularidadModel> leidasConIcono = singularidadesLeidas.stream()
                .peek(s -> s.setIconAssocieteFromProceso(
                        iconMap.getOrDefault(s.getProceso(), "No existe un icono asociado al proceso.")
                ))
                .collect(Collectors.toList());

        Object singularidadesActivasRaw = filtros.get("alarmasActivas");

        if (singularidadesActivasRaw != null)
        {
            boolean singularidadesActivas = Boolean.parseBoolean(String.valueOf(filtros.get("alarmasActivas")));

            if (singularidadesActivas)
            {
                Map<String, Object> response = new HashMap<>();
                response.put("singularidades", normalesConIcono);
                response.put("singularidadesLeidas", new ArrayList<>());
                return ResponseEntity.ok(response);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("singularidades", normalesConIcono);
        response.put("singularidadesLeidas", leidasConIcono);

        return ResponseEntity.ok(response);

    }
    catch (Exception e)
    {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of("error", "Error al filtrar dinamicamente", "detalle", e.getMessage())
        );
    }
}

}
