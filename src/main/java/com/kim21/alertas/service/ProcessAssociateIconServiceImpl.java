package com.kim21.alertas.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.kim21.alertas.dto.ProcessAssociateIconDTO;
import com.kim21.alertas.model.AlertasModel;
import com.kim21.alertas.model.ProcessAssociateIconModel;
import com.kim21.alertas.repository.AlertasRepository;
import com.kim21.alertas.repository.ProcessAssociateIconRepository;

@Service
public class ProcessAssociateIconServiceImpl implements ProcessAssociateIconService
{

        
    @Autowired
    private ProcessAssociateIconRepository repository;

    @Autowired
    private AlertasRepository alertasRepository;

    @Autowired
    private AlertasService alertasService;

    @Override
    public ResponseEntity<?> getIconByProceso(String proceso) 
    {
        try 
        {
            Optional<ProcessAssociateIconModel> result = repository.findByProceso(proceso);

            if (result.isEmpty()) 
            {
                return ResponseEntity.status(404).body(Map.of("error", "No se encontró ícono para el proceso: " + proceso));
            }

            return ResponseEntity.ok(result.get());

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            // TODO: handle exception
            return ResponseEntity.status(500).body(Map.of("error","Ha ocurrido un error interno."));
        }



    }

    @Override
    public ResponseEntity<?> createProcessIcon(ProcessAssociateIconDTO dto) 
    {


        try 
        {
            // Validación básica
            if (dto.getProceso() == null || dto.getProceso().isBlank() ||
                dto.getIconUrl() == null || dto.getIconUrl().isBlank() || dto.getGrupoLocal() == null || dto.getGrupoLocal().isBlank()
                )

            {
                return ResponseEntity.badRequest().body(Map.of("error", "Los campos 'proceso' e 'iconUrl' son obligatorios."));
            }

            // Evitar duplicados por proceso
            if (repository.findByProceso(dto.getProceso()).isPresent()) 
            {
                return ResponseEntity.status(409).body(Map.of("error", "El proceso ya tiene un ícono asociado."));
            }

            // Mapear DTO a entidad
            ProcessAssociateIconModel entity = ProcessAssociateIconModel.builder()
                    .proceso(dto.getProceso())
                    .iconUrl(dto.getIconUrl())
                    .grupoLocal(dto.getGrupoLocal())
                    .build();

            // Guardar en BD
            ProcessAssociateIconModel saved = repository.save(entity);

            // Convertir de nuevo a DTO para la respuesta
            ProcessAssociateIconDTO responseDto = ProcessAssociateIconDTO.builder()
                    .proceso(saved.getProceso())
                    .iconUrl(saved.getIconUrl())
                    .build();

            return ResponseEntity.ok(responseDto);

        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(500).body(Map.of("error", "Error interno al guardar el ícono", "details", e.getMessage()));
        }

    }

    @Override
    public ResponseEntity<?> updateIconByProceso(ProcessAssociateIconDTO dto) 
    {
        try    
        {
            // Validar campos
            if (dto.getProceso() == null || dto.getProceso().isBlank() ||
                dto.getIconUrl() == null || dto.getIconUrl().isBlank()) 
            {
                return ResponseEntity.badRequest().body(Map.of("error", "Los campos 'proceso' e 'iconUrl' son obligatorios."));
            }

            // Buscar si el proceso existe
            Optional<ProcessAssociateIconModel> existingOpt = repository.findByProceso(dto.getProceso());
            if (existingOpt.isEmpty()) 
            {
                return ResponseEntity.status(404).body(Map.of("error", "No existe un ícono asociado al proceso: " + dto.getProceso()));
            }

            // Actualizar entidad
            ProcessAssociateIconModel existing = existingOpt.get();
            existing.setIconUrl(dto.getIconUrl());

            ProcessAssociateIconModel updated = repository.save(existing);

            // Convertir a DTO para la respuesta
            ProcessAssociateIconDTO responseDto = ProcessAssociateIconDTO.builder()
                    .proceso(updated.getProceso())
                    .iconUrl(updated.getIconUrl())
                    .build();

            return ResponseEntity.ok(responseDto);

        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(500) .body(Map.of("error", "Error interno al actualizar el ícono", "details", e.getMessage()));
        }

    }

    @Override
    public ResponseEntity<?> getAllProcesos() 
    {
        //List<ProcessAssociateIconModel> listaProcesos= new ArrayList<>(repository.findAllByGrupoLocal(alertasService.obtenerGruposCoincidentesConAlertas()));
        try 
        {
            List<ProcessAssociateIconModel> listaProcesos = repository.findAllByGrupoLocalIn(alertasService.obtenerGruposCoincidentesConAlertas());

            List<AlertasModel> allAlerts = alertasRepository.findAll();

            List<ProcessAssociateIconModel> returnList = new ArrayList<>();


            //debo agregar el grupo local para poder mostralo en el frontend
            for (ProcessAssociateIconModel processAsocciete : listaProcesos) 
            {

                if(processAsocciete.getProceso() != null && processAsocciete.getGrupoLocal() != null)
                {
                    returnList.add(processAsocciete);
                }
            }

            // ahora debemos verificar los procesos que tengamos en alertas que no existan en returnMap para asociarlos            
            return ResponseEntity.ok(returnList);
        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(500).body(Map.of("error","Ha ocurrido un error interno"));
        }

    }

    @Override
    public ResponseEntity<?> deleteProcessIconById(Integer id) 
    {
        if (id == null || id <= 0) 
                {
            return ResponseEntity.badRequest().body(Map.of("error", "El ID proporcionado no es válido."));
        }

        Optional<ProcessAssociateIconModel> iconOpt = repository.findById(id);

        if (iconOpt.isEmpty()) 
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "No existe un icono asociado con el ID indicado."));
        }

        repository.deleteById(id);

        return ResponseEntity.ok(Map.of("message", "Icono eliminado correctamente.", "idEliminado", id));
    }
    
}
