package com.kim21.alertas.service;

import com.kim21.alertas.model.RangoColorConfigModel;
import com.kim21.alertas.repository.RangoColorConfigRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RangoColorConfigServiceImpl implements RangoColorConfigService {

    private final RangoColorConfigRepository repository;

    public RangoColorConfigServiceImpl(RangoColorConfigRepository repository) {
        this.repository = repository;
    }

    @Override
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(repository.findAllByOrderByPrioridadAsc());
    }

    @Override
    public ResponseEntity<?> obtenerPorId(Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No existe rango con id " + id)));
    }

    @Override
    public ResponseEntity<?> crear(RangoColorConfigModel request) {
        if (request.getNombre() == null || request.getNombre().isBlank()
                || request.getColor() == null || request.getColor().isBlank()
                || request.getPrioridad() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "nombre, color y prioridad son obligatorios"));
        }

        boolean nombreEnUso = repository.findByNombreIgnoreCase(request.getNombre()).isPresent();
        if (nombreEnUso) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Ya existe un rango con el nombre indicado"));
        }

        RangoColorConfigModel nuevo = RangoColorConfigModel.builder()
                .nombre(request.getNombre())
                .color(request.getColor())
                .minValor(request.getMinValor())
                .maxValor(request.getMaxValor())
                .prioridad(request.getPrioridad())
                .build();

        return ResponseEntity.ok(repository.save(nuevo));
    }

    @Override
    public ResponseEntity<?> actualizar(List<RangoColorConfigModel> request) 
    {
        try 
        {
            // 1. Borrar toda la tabla
            repository.deleteAll();
            repository.flush(); // Asegura el truncate lógico antes del insert

            // 2. Crear nuevos registros sin IDs (forzamos IDs nuevos)
            List<RangoColorConfigModel> nuevos = request.stream()
                    .map(r -> RangoColorConfigModel.builder()
                            .nombre(r.getNombre())
                            .color(r.getColor())
                            .minValor(r.getMinValor())
                            .maxValor(r.getMaxValor())
                            .prioridad(r.getPrioridad())
                            .build())
                    .collect(Collectors.toList());

            // 3. Insertar todo nuevamente
            List<RangoColorConfigModel> guardados = repository.saveAll(nuevos);

            return ResponseEntity.ok(guardados);

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                            "error", "No se pudo actualizar la configuración de colores",
                            "detalle", e.getMessage()
                    ));
        }

    }
    @Override
    public ResponseEntity<?> eliminar(Integer id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No existe rango con id " + id));
        }
        repository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Rango eliminado", "idEliminado", id));
    }
}
