package com.kim21.alertas.service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.util.InternCache;
import com.kim21.alertas.dto.AlertMarcarLeidaDTO;
import com.kim21.alertas.dto.AlertReportDTO;
import com.kim21.alertas.model.AlertasCodigo1Model;
import com.kim21.alertas.model.AlertasModel;
import com.kim21.alertas.model.ProcessAssociateIconModel;
import com.kim21.alertas.model.VisibleFieldConfigModel;
import com.kim21.alertas.repository.AlertasCodigo1Repository;
import com.kim21.alertas.repository.AlertasCodigo2Repository;
import com.kim21.alertas.repository.AlertasRepository;
import com.kim21.alertas.repository.ProcessAssociateIconRepository;
import com.kim21.alertas.repository.VisibleFieldConfigRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class AlertasServiceImpl implements AlertasService 
{

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AlertasRepository alertasRepository;
    @Autowired
    private VisibleFieldConfigRepository visibleFieldConfigRepository;

    private static final Map<String, String> COLUMN_TO_FIELD = Map.of(
    "fecha_reconocimiento", "fechaReconocimiento",
    "tiempo_reconocimiento", "tiempoReconocimiento",
    "grupo_local", "grupoLocal"
    );

    @Autowired
    private ProcessAssociateIconRepository processAssociateIconRepository;

    // 👉 Define el formato
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Autowired
    private AlertasCodigo1Repository alertasCodigo1Repository;

    @Autowired
    private AlertasCodigo2Repository alertasCodigo2Repository;
    
    // ALERTAS
    @Override
    public ResponseEntity<?> findAllAlertas() 
    {

        try 
        {
            List<String> gruposCoincidentesParaBuscar =  obtenerGruposCoincidentesConAlertas();
            //System.out.println("ESTOS SON LOS GRUPOS :" + gruposCoincidentesParaBuscar.toString() );
            List<AlertasModel> alertas = alertasRepository.findAllAlertsByGroupUser(gruposCoincidentesParaBuscar);
            // Obtener campos visibles desde la configuración
            List<String> camposVisibles = visibleFieldConfigRepository.findAll()
                .stream()
                .filter(VisibleFieldConfigModel::getVisible) // Solo los que están en true
                .map(VisibleFieldConfigModel::getFieldName)
                .collect(Collectors.toList());

            // Convertimos cada alerta a un Map con solo los campos visibles
            List<Map<String, Object>> resultado = new ArrayList<>();

            List<Map<String, Object>> alertasVisiblesNormales = new ArrayList<>();

            for (AlertasModel alerta : alertas) 
            {

                Map<String, Object> visibleData = new TreeMap<>();

                for (String campo : camposVisibles) 
                {
                    try 
                    {

                        String fieldName = COLUMN_TO_FIELD.getOrDefault(campo, campo);
                        String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);


                        // Obtiene el método de la clase
                        Method getter = AlertasModel.class.getMethod(getterName);

                        // Invoca el getter sobre la instancia actual
                        Object valor = getter.invoke(alerta);

                        //System.out.println("CAMPO: " + campo + "  VALOR: " + valor );
                        // Añade al map el nombre del campo y el valor
                        visibleData.put(campo, valor);

                        //System.out.println(campo + " y el valor: " + valor);

                    } catch (Exception e) 
                    {
                        // Log opcional si un campo no se puede acceder
                        System.err.println("Error accediendo al campo: " + campo + " → " + e.getMessage());
                    }
                }


                //agregamos campo de iconAssocieteFromProceso en ProcessAssociateIconModel
                Optional<ProcessAssociateIconModel> findIconUrl = processAssociateIconRepository.findByProceso(alerta.getProceso());

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
            
                

                alertasVisiblesNormales.add(visibleData);
            }


            Map<String, Object> response = new HashMap<>();
            response.put("alertas", alertasVisiblesNormales); // las visibles
            response.put("alertasLeidas", getAlertasLeidas());

            resultado.add(response);

            return ResponseEntity.ok(resultado);

        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al obtener alertas filtradas", "details", e.getMessage()));
        }

    }

    @Override
    public ResponseEntity<?> findAlertaById(Integer id) 
    {
        try 
        {
            List<AlertasModel> alertas = alertasRepository.findAllByAlertaid(id);

            List<String> gruposCoincidentesParaBuscar =  obtenerGruposCoincidentesConAlertas();

            // Obtener campos visibles desde la configuración
            List<String> camposVisibles = visibleFieldConfigRepository.findAll()
                .stream()
                .filter(VisibleFieldConfigModel::getVisible) // Solo los que están en true
                .map(VisibleFieldConfigModel::getFieldName)
                .collect(Collectors.toList());

            // Convertimos cada alerta a un Map con solo los campos visibles
            List<Map<String, Object>> resultado = new ArrayList<>();

            List<Map<String, Object>> alertasVisiblesNormales = new ArrayList<>();

            for (AlertasModel alerta : alertas) 
            {

                Map<String, Object> visibleData = new HashMap<>();

                for (String campo : camposVisibles) 
                {
                    try 
                    {

                        String fieldName = COLUMN_TO_FIELD.getOrDefault(campo, campo);
                        String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);


                        // Obtiene el método de la clase
                        Method getter = AlertasModel.class.getMethod(getterName);

                        // Invoca el getter sobre la instancia actual
                        Object valor = getter.invoke(alerta);

                        // 👀 Sanitizar fecha_reconocimiento
                        if ("fecha_reconocimiento".equalsIgnoreCase(campo) && valor != null) 
                        {
                            OffsetDateTime fecha = (OffsetDateTime) valor;
                            valor = fecha.format(FORMATTER);
                        }

                        // 👀 Sanitizar fecha_reconocimiento
                        if ("tiempo_reconocimiento".equalsIgnoreCase(campo) && valor != null) 
                        {
                            
                            valor = valor + " Minutos";
                        }

                        // Añade al map el nombre del campo y el valor
                        visibleData.put(campo, valor);

                        //System.out.println(campo + " y el valor: " + valor);

                    } catch (Exception e) 
                    {
                        // Log opcional si un campo no se puede acceder
                        System.err.println("Error accediendo al campo: " + campo + " → " + e.getMessage());
                    }
                }


                //agregamos campo de iconAssocieteFromProceso en ProcessAssociateIconModel
                Optional<ProcessAssociateIconModel> findIconUrl = processAssociateIconRepository.findByProceso(alerta.getProceso());
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
            
                

                alertasVisiblesNormales.add(visibleData);
            }


            Map<String, Object> response = new HashMap<>();
            response.put("alertas", alertasVisiblesNormales); // las visibles
            resultado.add(response);

            return ResponseEntity.ok(resultado);

        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al obtener alertas filtradas", "details", e.getMessage()));
        }
    }

    @Override
    public List<String> obtenerGruposDesdeCmd() throws IOException 
    {
        List<String> grupos = new ArrayList<>();

        ProcessBuilder pb = new ProcessBuilder(
    "C:\\Windows\\System32\\whoami.exe", "/groups"
        );

        pb.redirectErrorStream(true);
        //System.out.println("OS: " + System.getProperty("os.name"));
//System.out.println("Comando: " + pb.command());

        Process process = pb.start();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) 
        {
            String linea;
            while ((linea = reader.readLine()) != null) 
            {
                //System.out.println("DEBUGCMDDDDDDDDDDDDDDD -> " + linea); // 👈 aquí ves todo lo que devuelve el CMD

                if (linea.contains("S-1-")) 
                {
                    String[] partes = linea.trim().split("\\s{2,}");
                    if (partes.length > 0) 
                    {

                        grupos.add(partes[0].trim().toUpperCase());
                    }
                }
            }
        }

        
        return grupos;
    }



    private String normalizeGroup(String s) 
    {
        if (s == null) return "";
        return s.trim()
                .replace("\\\\", "\\")   // dobles barras → una sola
                .toUpperCase(Locale.ROOT);
    }



    @Override
    public List<String> obtenerGruposCoincidentesConAlertas() throws IOException 
    {
        
        //System.out.println("LLEGAMOS AL METODO BIEN OJO");

        List<String> gruposUsuario = obtenerGruposDesdeCmd();
                //System.out.println("LLEGAMOS AL METODO BIEN OJO1111" +gruposUsuario );

        if (gruposUsuario.isEmpty()) return Collections.emptyList();

        //System.out.println("grupos de CMD " + gruposUsuario);

        List<String> gruposEnBD = alertasRepository.obtenerGruposLocalesUnicos();
        //System.out.println("GRUPOS EN BD DE METODO " + gruposEnBD);

        // Normalizar BD
        Set<String> gruposBDSet = gruposEnBD.stream()
                .map(this::normalizeGroup)
                .collect(Collectors.toSet());

        //System.out.println("GRUPOS EN BD SET " + gruposBDSet);

        // Normalizar usuario antes de comparar
        return gruposUsuario.stream()
                .map(this::normalizeGroup)
                .filter(gruposBDSet::contains)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<?> getAlertsByProcesoAndGrupoLocalAndInitAndEndDate(String proceso,String activo, OffsetDateTime initDate,OffsetDateTime endDate)
    {

    // 1) Ambas fechas son obligatorias
    if (initDate == null || endDate == null) 
    {
        return ResponseEntity
            .badRequest()
            .body(Map.of("message", "Debes enviar ambas fechas: fecha inicio y fecha fin."));
    }

    // 2) Coherencia de rango
    if (endDate.isBefore(initDate)) 
    {
        return ResponseEntity
            .badRequest()
            .body(Map.of("message", "La fecha fin no puede ser anterior a la inicial."));
    }


        try 
        {
            List<String> gruposCoincidentesParaBuscar =  obtenerGruposCoincidentesConAlertas();
            if(gruposCoincidentesParaBuscar.isEmpty())
            {
                return ResponseEntity.status(404).body(Map.of("message","No existen grupos asociados al usuario en las alertas."));
            }   

            List<Map<String, Object>> alertasLeidas = getAlertasLeidasByFilter(proceso,
                activo,
                gruposCoincidentesParaBuscar,
                initDate,
                endDate
            );


            //crear map para almacenar alertas y alertasLeidas
            Map<String,Object> mapGeneral = new HashMap<>();

            mapGeneral.put("alertasLeidas", alertasLeidas);

            //obtener grupos locales que puede tener el usuario
            List<Map<String, Object>> alertas = getAlertasByFilter(
                proceso,
                activo,
                gruposCoincidentesParaBuscar,
                initDate,
                endDate
            );


            
            //logica para filtrar alertas no leidas
            mapGeneral.put("alertas", alertas);

            return ResponseEntity.ok(mapGeneral);

        } 
        catch (Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message","Ha ocurrido un error interno."));
        }
    }

    @Override
    public ResponseEntity<?> marcarAlertaComoLeida(AlertMarcarLeidaDTO dto) 
    {

        // 1. Validar que el ID de la alerta no sea nulo
        if (dto.getIdAlerta() == null || dto.getIdAlerta() <= 0) 
        {
            return ResponseEntity.badRequest().body(Map.of("message", "El campo 'alertaid' es obligatorio y debe ser mayor a 0"));
        }
        
        try 
        {

            // 1: verificar que existe la alerta con el id del dto
            Optional<AlertasModel> alertaOpt = alertasRepository.findById(dto.getIdAlerta());
            if (alertaOpt.isEmpty()) 
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No existe una alerta con el ID especificado."));
            }

            AlertasModel alerta = alertaOpt.get();

            // 3. Verificar si ya fue marcada como leída
            if (alerta.getFechaReconocimiento() != null) 
            { 
                return ResponseEntity.status(HttpStatus.CONFLICT).body( Map.of("message", "La alerta ya fue marcada como leída anteriormente"));
            }

            // 2: obtener el usuario del sistema
            String usuario = System.getProperty("user.name");
            if (usuario == null || usuario.isBlank()) 
            {
                usuario = "desconocido";
            }

            // 3: verificar que el comentario no exceda 80 caracteres
            if (dto.getComentario() != null && dto.getComentario().length() > 80) 
            {
                return ResponseEntity.badRequest() .body(Map.of("message", "El comentario no puede exceder los 80 caracteres."));
            }


            // 5. Validar existencia de códigos
            boolean codigo1Existe = alertasCodigo1Repository.existsByCodcodigo1(dto.getCodigo1());
            boolean codigo2Existe = alertasCodigo2Repository.existsByCodcodigo2(dto.getCodigo2());

            if (!codigo1Existe) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El código1 no existe en la base de datos."));
            }

            if (!codigo2Existe) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El código2 no existe en la base de datos."));
            }

            // 4: calcular tiempo transcurrido entre inicioevento y ahora
            OffsetDateTime fechaReconocimiento = OffsetDateTime.now();
            
            Long tiempoReconocimiento = null;
            if(alerta.getInicioevento() != null && fechaReconocimiento != null)
            {
                tiempoReconocimiento = Duration.between(alerta.getInicioevento(), fechaReconocimiento).toMinutes();
            }

            // 5: hacer el update final de la alerta
            alerta.setUserid(usuario);
            alerta.setComentario(dto.getComentario());
            alerta.setValida(dto.isValida());
            alerta.setFechaReconocimiento(fechaReconocimiento);
            if(tiempoReconocimiento != null)
            {
                alerta.setTiempoReconocimiento(tiempoReconocimiento);
            }
            alerta.setCodigo1(dto.getCodigo1());
            alerta.setCodigo2(dto.getCodigo2());

            alertasRepository.save(alerta);

            return ResponseEntity.ok(Map.of(
                    "message", "La alerta fue marcada como leída correctamente.",
                    "alertaid", alerta.getAlertaid(),
                    "usuario", usuario
            ));

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al marcar la alerta como leída", "details", e.getMessage()));
        }

    }

    @Override
    public ResponseEntity<?> reportAlerts() 
    {

        try 
        {
            Map<String, Object> report = new HashMap<>();

            // 1. Cantidad de alertas activas (no leídas)
            Long cantidadActivas = alertasRepository.countAlertasActivas();
            report.put("cantidadActivas", cantidadActivas);

            // 2. Cantidad por proceso
            List<Object[]> porProceso = alertasRepository.countAlertasPorProceso();
            Map<String, Long> procesoMap = new HashMap<>();
            for (Object[] row : porProceso) 
            {
                String proceso = (String) row[0];
                if(proceso == null )
                {
                    proceso = "NULO";
                }
                
                Long total = (Long) row[1];
                procesoMap.put(proceso, total);
            }
            report.put("alertasPorProceso", procesoMap);

            // 3. Cantidad por servicio
            List<Object[]> porServicio = alertasRepository.countAlertasPorServicio();
            Map<String, Long> servicioMap = new HashMap<>();
            for (Object[] row : porServicio) 
            {
                String servicio = (String) row[0];
                if(servicio == null )
                {
                    servicio = "NULO";
                }

                Long total = (Long) row[1];
                servicioMap.put(servicio, total);
            }
            report.put("alertasPorTipoServicio", servicioMap);

            // 4. Cantidad por criticidad (severidad)
            List<Object[]> porCriticidad = alertasRepository.countAlertasPorCriticidad();
            Map<String, Long> criticidadMap = new HashMap<>();
            for (Object[] row : porCriticidad) 
            {
                String criticidad = (String) row[0];
                if(criticidad == null )
                {
                    criticidad = "NULO";
                }

                Long total = (Long) row[1];
                criticidadMap.put(criticidad, total);
            }
            
            report.put("alertasPorCriticidad", criticidadMap);

            return ResponseEntity.ok(report);

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error interno al generar el reporte de alertas", "error", e.getMessage()));
        }
    }




    public List<Map<String, Object>> getAlertasLeidas()
    {

        try 
        {
            List<String> gruposCoincidentesParaBuscar =  obtenerGruposCoincidentesConAlertas();

            List<AlertasModel> alertas = alertasRepository.findAllAlertsByGroupUserLeidas(gruposCoincidentesParaBuscar);
            // Obtener campos visibles desde la configuración
            List<String> camposVisibles = visibleFieldConfigRepository.findAll()
                .stream()
                .filter(VisibleFieldConfigModel::getVisible) // Solo los que están en true
                .map(VisibleFieldConfigModel::getFieldName)
                .collect(Collectors.toList());

            // Convertimos cada alerta a un Map con solo los campos visibles
            List<Map<String, Object>> resultado = new ArrayList<>();

            List<Map<String, Object>> alertasVisiblesNormales = new ArrayList<>();

            for (AlertasModel alerta : alertas) 
            {

                Map<String, Object> visibleData = new HashMap<>();

                for (String campo : camposVisibles) 
                {
                    try 
                    {

                        String fieldName = COLUMN_TO_FIELD.getOrDefault(campo, campo);
                        String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);


                        // Obtiene el método de la clase
                        Method getter = AlertasModel.class.getMethod(getterName);

                        // Invoca el getter sobre la instancia actual
                        Object valor = getter.invoke(alerta);

                        // 👀 Sanitizar fecha_reconocimiento
                        if ("fecha_reconocimiento".equalsIgnoreCase(campo) && valor != null) 
                        {
                            OffsetDateTime fecha = (OffsetDateTime) valor;
                            valor = fecha.format(FORMATTER);
                        }

                        // Añade al map el nombre del campo y el valor
                        visibleData.put(campo, valor);

                        //System.out.println(campo + " y el valor: " + valor);

                    } catch (Exception e) 
                    {
                        // Log opcional si un campo no se puede acceder
                        System.err.println("Error accediendo al campo: " + campo + " → " + e.getMessage());
                    }
                }



                //agregamos campo de iconAssocieteFromProceso en ProcessAssociateIconModel
                Optional<ProcessAssociateIconModel> findIconUrl = processAssociateIconRepository.findByProceso(alerta.getProceso());
                if(!findIconUrl.isPresent())
                {
                    visibleData.put("IconAssocieteFromProceso", "No existe un icono asociado al proceso.");
                }
                else
                {
                    visibleData.put("IconAssocieteFromProceso", findIconUrl.get().getIconUrl());
                } 



                alertasVisiblesNormales.add(visibleData);
            }

            return alertasVisiblesNormales ;

        } 
        catch (Exception e) 
        {
            // TODO: handle exception
            e.printStackTrace();
            return Collections.emptyList(); 
        }

    }


    public List<Map<String, Object>> getAlertasLeidasByFilter(String proceso, String activo, List<String> grupos, OffsetDateTime initDate,OffsetDateTime endDate)
    {
        try 
        {
            List<String> gruposCoincidentesParaBuscar =  obtenerGruposCoincidentesConAlertas();

            List<AlertasModel> alertas = alertasRepository.findByProcesoAndGruposAndDateRangeLeidas
            (   proceso,
                activo,
                gruposCoincidentesParaBuscar,
                initDate,
                endDate
            );
            
            // Obtener campos visibles desde la configuración
            List<String> camposVisibles = visibleFieldConfigRepository.findAll()
                .stream()
                .filter(VisibleFieldConfigModel::getVisible) // Solo los que están en true
                .map(VisibleFieldConfigModel::getFieldName)
                .collect(Collectors.toList());

            // Convertimos cada alerta a un Map con solo los campos visibles
            List<Map<String, Object>> resultado = new ArrayList<>();

            List<Map<String, Object>> alertasVisiblesNormales = new ArrayList<>();

            for (AlertasModel alerta : alertas) 
            {

                Map<String, Object> visibleData = new HashMap<>();

                for (String campo : camposVisibles) 
                {
                    try 
                    {

                        String fieldName = COLUMN_TO_FIELD.getOrDefault(campo, campo);
                        String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);


                        // Obtiene el método de la clase
                        Method getter = AlertasModel.class.getMethod(getterName);

                        // Invoca el getter sobre la instancia actual
                        Object valor = getter.invoke(alerta);

                        // 👀 Sanitizar fecha_reconocimiento
                        if ("fecha_reconocimiento".equalsIgnoreCase(campo) && valor != null) 
                        {
                            OffsetDateTime fecha = (OffsetDateTime) valor;
                            valor = fecha.format(FORMATTER);
                        }

                        // Añade al map el nombre del campo y el valor
                        visibleData.put(campo, valor);

                        //System.out.println(campo + " y el valor: " + valor);

                    } catch (Exception e) 
                    {
                        // Log opcional si un campo no se puede acceder
                        System.err.println("Error accediendo al campo: " + campo + " → " + e.getMessage());
                    }
                }



                //agregamos campo de iconAssocieteFromProceso en ProcessAssociateIconModel
                Optional<ProcessAssociateIconModel> findIconUrl = processAssociateIconRepository.findByProceso(alerta.getProceso());
                if(!findIconUrl.isPresent())
                {
                    visibleData.put("IconAssocieteFromProceso", "No existe un icono asociado al proceso.");
                }
                else
                {
                    visibleData.put("IconAssocieteFromProceso", findIconUrl.get().getIconUrl());
                } 



                alertasVisiblesNormales.add(visibleData);
            }

            return alertasVisiblesNormales ;

        } 
        catch (Exception e) 
        {
            // TODO: handle exception
            e.printStackTrace();
            return Collections.emptyList(); 
        }
    }



    public List<Map<String, Object>> getAlertasByFilter(String proceso, String activo, List<String> grupos, OffsetDateTime initDate,OffsetDateTime endDate)
    {
        try 
        {
            List<String> gruposCoincidentesParaBuscar =  obtenerGruposCoincidentesConAlertas();

            List<AlertasModel> alertas = alertasRepository.findByProcesoAndGruposAndDateRange
            (   proceso,
                activo,
                gruposCoincidentesParaBuscar,
                initDate,
                endDate
            );
            
            // Obtener campos visibles desde la configuración
            List<String> camposVisibles = visibleFieldConfigRepository.findAll()
                .stream()
                .filter(VisibleFieldConfigModel::getVisible) // Solo los que están en true
                .map(VisibleFieldConfigModel::getFieldName)
                .collect(Collectors.toList());

            // Convertimos cada alerta a un Map con solo los campos visibles
            List<Map<String, Object>> resultado = new ArrayList<>();

            List<Map<String, Object>> alertasVisiblesNormales = new ArrayList<>();

            for (AlertasModel alerta : alertas) 
            {

                Map<String, Object> visibleData = new HashMap<>();

                for (String campo : camposVisibles) 
                {
                    try 
                    {

                        String fieldName = COLUMN_TO_FIELD.getOrDefault(campo, campo);
                        String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);


                        // Obtiene el método de la clase
                        Method getter = AlertasModel.class.getMethod(getterName);

                        // Invoca el getter sobre la instancia actual
                        Object valor = getter.invoke(alerta);


                        System.out.println("CAMPO: " + campo + "  VALOR: " + valor );
                        // Añade al map el nombre del campo y el valor
                        visibleData.put(campo, valor);

                        //System.out.println(campo + " y el valor: " + valor);

                    } catch (Exception e) 
                    {
                        // Log opcional si un campo no se puede acceder
                        System.err.println("Error accediendo al campo: " + campo + " → " + e.getMessage());
                    }
                }



                //agregamos campo de iconAssocieteFromProceso en ProcessAssociateIconModel
                Optional<ProcessAssociateIconModel> findIconUrl = processAssociateIconRepository.findByProceso(alerta.getProceso());
                if(!findIconUrl.isPresent())
                {
                    visibleData.put("IconAssocieteFromProceso", "No existe un icono asociado al proceso.");
                }
                else
                {
                    visibleData.put("IconAssocieteFromProceso", findIconUrl.get().getIconUrl());
                } 



                alertasVisiblesNormales.add(visibleData);
            }

            return alertasVisiblesNormales ;

        } 
        catch (Exception e) 
        {
            // TODO: handle exception
            e.printStackTrace();
            return Collections.emptyList(); 
        }
    }

    @Override
    public ResponseEntity<?> getProcesos() 
    {       
        try 
        {
            List<String> gruposCoincidentesParaBuscar =  obtenerGruposCoincidentesConAlertas();
            if(gruposCoincidentesParaBuscar.isEmpty())
            {
                return ResponseEntity.status(404).body(Map.of("message","No existen grupos asociados al usuario."));
            }
            
            List<String> procesos = alertasRepository.findDistinctProcesosByGrupoLocal(gruposCoincidentesParaBuscar);

            if(procesos.isEmpty())
            {
                return ResponseEntity.status(404).body(Map.of("message","No hay procesos asociados al usuario."));
            }

            return ResponseEntity.ok(procesos);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return ResponseEntity.status(404).body(Map.of("error","Ha ocurrido un error interno"));
        }

    }

    @Override
    public ResponseEntity<?> getActivos() 
    {
        try 
        {
            List<String> gruposCoincidentesParaBuscar =  obtenerGruposCoincidentesConAlertas();
            if(gruposCoincidentesParaBuscar.isEmpty())
            {
                return ResponseEntity.status(404).body(Map.of("message","No existen grupos asociados al usuario."));
            }

            List<String> activos = alertasRepository.findAllDistintActivosAndGrupoLocal(gruposCoincidentesParaBuscar);

            if(activos.isEmpty())
            {
                return ResponseEntity.status(404).body(Map.of("message","No hay activos asociados al usuario."));
            }
            return ResponseEntity.ok(activos);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return ResponseEntity.status(404).body(Map.of("error","Ha ocurrido un error interno"));
        }

    }

    @Override
    public ResponseEntity<?> filtrarDinamico(Map<String, Object> filtros) 
    {
        try 
        {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AlertasModel> query = cb.createQuery(AlertasModel.class);
        Root<AlertasModel> root = query.from(AlertasModel.class);

        List<Predicate> predicates = new ArrayList<>();


        // --------------------------
        // 🕐 FILTRO POR FECHAS (solo día/mes/año)
        // --------------------------
        Object fechaInicioObj = filtros.get("fechaInicio");
        Object fechaFinObj = filtros.get("fechaFin");

        boolean tieneInicio = fechaInicioObj != null && !fechaInicioObj.toString().isBlank();
        boolean tieneFin = fechaFinObj != null && !fechaFinObj.toString().isBlank();

        if (tieneInicio || tieneFin) 
        {
            try 
            {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                // Si se envían, las parseamos a LocalDate
                LocalDate fechaInicio = tieneInicio ? LocalDate.parse(fechaInicioObj.toString(), formatter) : null;
                LocalDate fechaFin = tieneFin ? LocalDate.parse(fechaFinObj.toString(), formatter) : null;

                // Convertimos LocalDate → OffsetDateTime (a medianoche local)
                ZoneOffset offsetChile = ZoneOffset.of("-03:00");
                Path<OffsetDateTime> campoFecha = root.get("fechaestado");

                if (fechaInicio != null && fechaFin != null) 
                {
                    // Ambos límites → between (inicio del día hasta fin del día)
                    OffsetDateTime inicioDelDia = fechaInicio.atStartOfDay().atOffset(offsetChile);
                    OffsetDateTime finDelDia = fechaFin.plusDays(1).atStartOfDay().atOffset(offsetChile).minusNanos(1);

                    predicates.add(cb.between(campoFecha, inicioDelDia, finDelDia));
                } 
                else if (fechaInicio != null) 
                {
                    // Solo fechaInicio → >=
                    OffsetDateTime inicioDelDia = fechaInicio.atStartOfDay().atOffset(offsetChile);
                    predicates.add(cb.greaterThanOrEqualTo(campoFecha, inicioDelDia));
                } 
                else 
                {
                    // Solo fechaFin → <=
                    OffsetDateTime finDelDia = fechaFin.plusDays(1).atStartOfDay().atOffset(offsetChile).minusNanos(1);
                    predicates.add(cb.lessThanOrEqualTo(campoFecha, finDelDia));
                }


            } 
            catch (Exception e) 
            {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Formato de fecha inválido",
                    "detalle", "Usa formato: 2025-10-04 (solo día/mes/año)"
                ));
            }
        }


filtros.forEach((campo, valor) -> {
    if (valor == null || campo == null) {
        return;
    }

    // ignoramos los que ya manejamos
    if (campo.equalsIgnoreCase("fechaInicio") || campo.equalsIgnoreCase("fechaFin")) {
        return;
    }

    try {
        Path<Object> path = root.get(campo);

        if (valor instanceof String) {
            predicates.add(cb.like(cb.lower(path.as(String.class)),
                    "%" + valor.toString().toLowerCase() + "%"));
        } else if (valor instanceof Number) {
            predicates.add(cb.equal(path, valor));
        } else if (valor instanceof Boolean) {
            predicates.add(cb.equal(path.as(Boolean.class), (Boolean) valor));
        } else if (valor instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> rango = (Map<String, Object>) valor;

            if (rango.containsKey("min") && rango.containsKey("max")) {
                predicates.add(cb.between(
                    path.as(Double.class),
                    cb.literal(((Number) rango.get("min")).doubleValue()),
                    cb.literal(((Number) rango.get("max")).doubleValue())
                ));
            } else if (rango.containsKey("min")) {
                predicates.add(cb.greaterThanOrEqualTo(
                    path.as(Double.class),
                    cb.literal(((Number) rango.get("min")).doubleValue())
                ));
            } else if (rango.containsKey("max")) {
                predicates.add(cb.lessThanOrEqualTo(
                    path.as(Double.class),
                    cb.literal(((Number) rango.get("max")).doubleValue())
                ));
            }
        }

    } catch (IllegalArgumentException e) {
        // campo inexistente → lo ignoramos silenciosamente
        System.out.println("⚠️ Campo ignorado: " + campo);
    }
});


        query.where(cb.and(predicates.toArray(new Predicate[0])));

        List<AlertasModel> result = entityManager.createQuery(query).getResultList();

        //diferencias entre alertas Leidas y alertas normales
        List<AlertasModel> alertasNormales = new ArrayList<>();
        List<AlertasModel> alertasLeidas = new ArrayList<>();


        for (AlertasModel alerta : result) 
        {
            //verificar si tiempo renocimiento y fechaReconocimiento es null, las agregamos a normales
            if(alerta.getFechaReconocimiento() == null)
            {

                //son alertas normales
                alertasNormales.add(alerta);
            }
            else
            {
                alertasLeidas.add(alerta);
            }
        }





        // Pre-cargar todos los íconos en memoria
Map<String, String> iconMap = processAssociateIconRepository.findAll()
    .stream()
    .collect(Collectors.toMap(
        ProcessAssociateIconModel::getProceso,
        ProcessAssociateIconModel::getIconUrl
    ));

List<AlertasModel> normalesConIcono = alertasNormales.stream()
    .peek(a -> a.setIconAssocieteFromProceso(
        iconMap.getOrDefault(a.getProceso(), "No existe un icono asociado al proceso.")
    ))
    .collect(Collectors.toList());

List<AlertasModel> leidasConIcono = alertasLeidas.stream()
    .peek(a -> a.setIconAssocieteFromProceso(
        iconMap.getOrDefault(a.getProceso(), "No existe un icono asociado al proceso.")
    ))
    .collect(Collectors.toList());

Map<String, Object> response = new HashMap<>();
response.put("alertas", normalesConIcono);
response.put("alertasLeidas", leidasConIcono);

return ResponseEntity.ok(response);
//
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("alertas", alertasNormales);
//        response.put("alertasLeidas", alertasLeidas);
//        return ResponseEntity.ok(response);

        } 
        catch (Exception e) 
        {
            // TODO: handle exception
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                Map.of("error", "Error al filtrar dinámicamente", "detalle", e.getMessage())
            );
        }

    }

    @Override
    public ResponseEntity<?> getAlertasActivas() 
    {
        try 
        {
            List<String> gruposCoincidentesParaBuscar =  obtenerGruposCoincidentesConAlertas();
            // Buscar alertas donde fecha_reconocimiento y tiempo_reconocimiento son NULL
            List<AlertasModel> alertasActivas = alertasRepository.findByValidaIsNullAndGrupoLocalIn(gruposCoincidentesParaBuscar);

            //ahora debemos ocupar el filtr para saber que columnas podemos retornar basado en la configuracion 
            // Obtener campos visibles desde la configuración
            List<String> camposVisibles = visibleFieldConfigRepository.findAll()
                .stream()
                .filter(VisibleFieldConfigModel::getVisible) // Solo los que están en true
                .map(VisibleFieldConfigModel::getFieldName)
                .collect(Collectors.toList());

                            // Convertimos cada alerta a un Map con solo los campos visibles
            List<Map<String, Object>> resultado = new ArrayList<>();

            List<Map<String, Object>> alertasVisiblesNormales = new ArrayList<>();

            for (AlertasModel alerta : alertasActivas) 
            {

                Map<String, Object> visibleData = new HashMap<>();

                for (String campo : camposVisibles) 
                {
                    try 
                    {

                        String fieldName = COLUMN_TO_FIELD.getOrDefault(campo, campo);
                        String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);


                        // Obtiene el método de la clase
                        Method getter = AlertasModel.class.getMethod(getterName);

                        // Invoca el getter sobre la instancia actual
                        Object valor = getter.invoke(alerta);

                        // 👀 Sanitizar fecha_reconocimiento
                        if ("fecha_reconocimiento".equalsIgnoreCase(campo) && valor != null) 
                        {
                            OffsetDateTime fecha = (OffsetDateTime) valor;
                            valor = fecha.format(FORMATTER);
                        }

                        // 👀 Sanitizar fecha_reconocimiento
                        if ("tiempo_reconocimiento".equalsIgnoreCase(campo) && valor != null) 
                        {
                            
                            valor = valor + " Minutos";
                        }

                        // Añade al map el nombre del campo y el valor
                        visibleData.put(campo, valor);

                        //System.out.println(campo + " y el valor: " + valor);

                    } catch (Exception e) 
                    {
                        // Log opcional si un campo no se puede acceder
                        System.err.println("Error accediendo al campo: " + campo + " → " + e.getMessage());
                    }
                }


                //agregamos campo de iconAssocieteFromProceso en ProcessAssociateIconModel
                Optional<ProcessAssociateIconModel> findIconUrl = processAssociateIconRepository.findByProceso(alerta.getProceso());
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
            
                

                alertasVisiblesNormales.add(visibleData);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("alertas", alertasVisiblesNormales); // las visibles
            response.put("alertasLeidas", null);
            resultado.add(response);

            return ResponseEntity.ok(resultado);
            
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error al obtener las alertas activas",
                "detalle", e.getMessage()
            ));
        }

    }



    @Override
    public ResponseEntity<?> getTipos()
    {
        try
        {
            List<String> gruposCoincidentesParaBuscar =  obtenerGruposCoincidentesConAlertas();
            if(gruposCoincidentesParaBuscar.isEmpty())
            {
                return ResponseEntity.status(404).body(Map.of("message","No existen grupos asociados al usuario."));
            }

            Map<String, Object> tipos = new HashMap<>();
            tipos.put("procesos", alertasRepository.findDistinctProcesosByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("nombreActivo", alertasRepository.findAllDistintActivosAndGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("codalerta", alertasRepository.findDistinctCodalertaByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("nombre", alertasRepository.findDistinctNombreByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("sentenciaId", alertasRepository.findDistinctSentenciaIdByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("identificacionalerta", alertasRepository.findDistinctIdentificacionAlertaByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("latencia", alertasRepository.findDistinctLatenciaByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("tipoServicio", alertasRepository.findDistinctTipoServicioByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("CI", alertasRepository.findDistinctCiByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("Subtiposervicio", alertasRepository.findDistinctSubtipoServicioByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("jitter", alertasRepository.findDistinctJitterByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("disponibilidad", alertasRepository.findDistinctDisponibilidadByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("packetlost", alertasRepository.findDistinctPacketLostByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("rssi", alertasRepository.findDistinctRssiByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("nsr", alertasRepository.findDistinctNsrByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("PLM", alertasRepository.findDistinctPlmByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("tipoExWa", alertasRepository.findDistinctTipoExWaByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("codigoEvento", alertasRepository.findDistinctCodigoEventoByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("descripcionevento", alertasRepository.findDistinctDescripcionEventoByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("Origen", alertasRepository.findDistinctOrigenByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("tipodocumento", alertasRepository.findDistinctTipoDocumentoByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("estado", alertasRepository.findDistinctEstadoByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("resumen", alertasRepository.findDistinctResumenByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("titulo", alertasRepository.findDistinctTituloByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("numero", alertasRepository.findDistinctNumeroByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("razonestado", alertasRepository.findDistinctRazonEstadoByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("gpsx", alertasRepository.findDistinctGpsxByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("gpsy", alertasRepository.findDistinctGpsyByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("gpsz", alertasRepository.findDistinctGpszByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("gpsh", alertasRepository.findDistinctGpshByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("radio", alertasRepository.findDistinctRadioByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("severidad", alertasRepository.findDistinctSeveridadByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("comentario", alertasRepository.findDistinctComentarioByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("OT", alertasRepository.findDistinctOtByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("ticket", alertasRepository.findDistinctTicketByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("prediccion", alertasRepository.findDistinctPrediccionByGrupoLocal(gruposCoincidentesParaBuscar));
            tipos.put("tiempoReconocimiento", alertasRepository.findDistinctTiempoReconocimientoByGrupoLocal(gruposCoincidentesParaBuscar));

            boolean existenValores = tipos.values().stream()
                    .filter(Objects::nonNull)
                    .anyMatch(valor -> valor instanceof List<?> && !((List<?>) valor).isEmpty());

            if(!existenValores)
            {
                return ResponseEntity.status(404).body(Map.of("message","No existen tipos asociados al usuario."));
            }

            return ResponseEntity.ok(tipos);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.status(404).body(Map.of("error","Ha ocurrido un error interno"));
        }
    }

    @Override
    public ResponseEntity<?> reportAlertsDynamic(AlertReportDTO dto) {
        try {
            if (dto.getAlertas() == null) 
            {
                return ResponseEntity.badRequest().body(Map.of("error", "La lista de alertas está vacía o no fue enviada."));
            }

            Map<String, Object> mapaReporte = new HashMap<>();

            // 1. Cantidad total de alertas activas
            Integer alertasActivasSize = 0;
            if(!dto.getAlertas().isEmpty())
            {
                alertasActivasSize = dto.getAlertas().size();
            }
            else
            {
                alertasActivasSize = 0;
            }

            mapaReporte.put("cantidadActivas", alertasActivasSize);

            // 2.´POR PROCESO
            Map<String,Integer> mapProceso = new HashMap<>();

            for (Object alerta : dto.getAlertas()) 
            {
                Map<String,Object> mapAlerta = (Map<String,Object>) alerta;

                String proceso = (String) mapAlerta.get("proceso");
                if(proceso != null)
                {   
                    mapProceso.put(proceso, mapProceso.getOrDefault(proceso, 0) + 1);
                }
            }

            for (Object alerta : dto.getAlertasLeidas()) 
            {
                Map<String,Object> mapAlerta = (Map<String,Object>) alerta;

                String proceso = (String) mapAlerta.get("proceso");
                if(proceso != null)
                {   
                    mapProceso.put(proceso, mapProceso.getOrDefault(proceso, 0) + 1);
                }
            }
            mapaReporte.put("alertasPorProceso", mapProceso);





            // 3. Por tipo de servicio
            Map<String,Integer> mapTipoServicio = new HashMap<>();

            for (Object alerta : dto.getAlertas()) 
            {
                Map<String,Object> mapAlerta = (Map<String,Object>) alerta;

                String tipoServicio = (String) mapAlerta.get("tipoServicio");
                if(tipoServicio != null)
                {   
                    mapTipoServicio.put(tipoServicio, mapTipoServicio.getOrDefault(tipoServicio, 0) + 1);
                }
            }

            for (Object alerta : dto.getAlertasLeidas()) 
            {
                Map<String,Object> mapAlerta = (Map<String,Object>) alerta;

                String tipoServicio = (String) mapAlerta.get("proceso");
                if(tipoServicio != null)
                {   
                    mapTipoServicio.put(tipoServicio, mapTipoServicio.getOrDefault(tipoServicio, 0) + 1);
                }
            }

            mapaReporte.put("alertasPorTipoServicio", mapTipoServicio);



            // 3. Por CRITICIDAD
            Map<String,Integer> mapCriticidad = new HashMap<>();

            for (Object alerta : dto.getAlertas()) 
            {
                Map<String,Object> mapAlerta = (Map<String,Object>) alerta;

                String severidad = (String) mapAlerta.get("severidad");
                if(severidad != null)
                {   
                    mapCriticidad.put(severidad, mapCriticidad.getOrDefault(severidad, 0) + 1);
                }
            }

            for (Object alerta : dto.getAlertasLeidas()) 
            {
                Map<String,Object> mapAlerta = (Map<String,Object>) alerta;

                String severidad = (String) mapAlerta.get("severidad");
                if(severidad != null)
                {   
                    mapCriticidad.put(severidad, mapCriticidad.getOrDefault(severidad, 0) + 1);
                }
            }

            mapaReporte.put("alertasPorCriticidad", mapCriticidad);


            return ResponseEntity.ok(mapaReporte);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(Map.of("error", "Ha ocurrido un error interno en el reporte dinámico."));
        }
    }

    private int contarPorCampo(List<Object> lista, String campo) 
    {
        int contador = 0;
        for (Object obj : lista) 
        {
            if (obj instanceof Map) 
            {
                Map<String, Object> map = (Map<String, Object>) obj;
                Object valor = map.get(campo);
                if (valor != null) 
                {
                    contador++;
                }
            }
        }
        return contador;
    }




}