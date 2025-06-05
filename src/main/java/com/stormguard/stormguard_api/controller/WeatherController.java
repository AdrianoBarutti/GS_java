package com.stormguard.stormguard_api.controller;

import com.stormguard.stormguard_api.dto.AlertResumoDTO;
import com.stormguard.stormguard_api.model.Alert;
import com.stormguard.stormguard_api.service.WeatherService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;


@RestController
@RequestMapping("/alerts")
@CrossOrigin(origins = "*")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @Tag(name = "Alertas")
    @Operation(
        summary = "Cria um novo alerta",
        description = "Cria um novo alerta no banco de dados. Apenas usuários autenticados com role ADMIN podem criar alertas."
    )
    // CREATE
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createAlert(@Valid @RequestBody Alert alert) {
    // Define a data de envio como agora, se não estiver definida
    if (alert.getSent() == null) {
        alert.setSent(LocalDateTime.now());
    }

    // Validação adicional para lógica de datas
    if (alert.getExpires().isBefore(alert.getEffective())) {
        return ResponseEntity.badRequest().body("A data de expiração deve ser maior que a data de efetivação.");
    }

        Alert savedAlert = weatherService.saveAlert(alert);
        return ResponseEntity.ok(savedAlert);
    }

    // READ BY ID
    @Tag(name = "Alertas")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Alert> getAlertById(@PathVariable Long id) {
        Optional<Alert> alert = weatherService.getAlertById(id);
        return alert.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // UPDATE
    @Tag(name = "Alertas")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Alert> updateAlert(@PathVariable Long id, @RequestBody Alert alert) {
        Optional<Alert> updated = weatherService.updateAlert(id, alert);
        return updated.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE
    @Tag(name = "Alertas")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        boolean deleted = weatherService.deleteAlert(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


    // Apenas usuários autenticados com role USER ou ADMIN podem acessar
    @Tag(name = "Alertas Resumidos")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(
        summary = "Busca alertas resumidos do banco de dados",
        description = "Busca todos os alertas do banco de dados com paginação e ordenação. Retorna uma lista de alertas filtrados por evento, status e área."
    )
    @GetMapping("/internal")
    @ResponseBody
    public ResponseEntity<Page<AlertResumoDTO>> getAlertsResumoFromDb(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sent,desc") String sort,
            @RequestParam(required = false) String event,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String area
    ) {
        Page<AlertResumoDTO> result = weatherService.getAlertsResumoFromDb(page, size, sort, event, status, area);
        return ResponseEntity.ok(result);
    }

    // Apenas ADMIN pode sincronizar alertas
    @Tag(name = "Sincronização de Alertas")
    @Operation(
        summary = "Sincroniza alertas com a API externa e salva no banco de dados",
        description = "Sincroniza os alertas com a API externa e salva no banco de dados, normalizando os dados e tratando possíveis erros de formatação de data. Retorna uma mensagem de sucesso com a quantidade de alertas salvos."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/sync")
    public ResponseEntity<String> syncAlerts(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String messageType,
            @RequestParam(required = false) String event,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String point,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String regionType,
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) String urgency,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String certainty,
            @RequestParam(required = false) String limit,
            @RequestParam(required = false) String cursor
    ) {
        return ResponseEntity.ok(weatherService.saveAlertsFromApi(
                start, end, status, messageType, event, code, area, point, region, regionType, zone, urgency, severity, certainty, limit, cursor
        ));
    }

    // Apenas autenticados podem ver alertas externos
    @Tag(name = "Alertas Externos")
    @Operation(
        summary = "Busca alertas da API externa",
        description = "Busca todos os alertas da API externa com filtros opcionais. Retorna uma lista de alertas filtrados por data, status, tipo de mensagem, evento, código, área, ponto, região, tipo de região, zona, urgência, severidade e certeza."
    )
    @PreAuthorize("hasRole('ADMIN')") 
    @GetMapping("/external")
    public ResponseEntity<?> getExternalAlerts(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String messageType,
            @RequestParam(required = false) String event,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String point,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String regionType,
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) String urgency,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String certainty,
            @RequestParam(required = false) String limit,
            @RequestParam(required = false) String cursor
    ) {
        return ResponseEntity.ok(weatherService.getAlertsFromExternalApi(
                start, end, status, messageType, event, code, area, point, region, regionType, zone, urgency, severity, certainty, limit, cursor
        ));
    }

    // Apenas ADMIN pode ver tipos de alerta
    @Tag(name = "Alertas Externos")
    @Operation(
        summary = "Busca tipos de alerta",
        description = "Busca os tipos de alerta disponíveis na API externa. Retorna uma lista fixa de tipos de alerta, como 'Tornado', 'Severe Thunderstorm', etc."
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/types")
    public ResponseEntity<?> getAlertTypes() {
        return ResponseEntity.ok(weatherService.getAlertTypes());
    }
}