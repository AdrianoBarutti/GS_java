package com.stormguard.stormguard_api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.stormguard.stormguard_api.dto.AlertResumoDTO;
import com.stormguard.stormguard_api.model.Alert;
import com.stormguard.stormguard_api.repository.AlertRepository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.*;


@Service
public class WeatherService {

    @Autowired
    private AlertRepository alertRepository;

    public Alert saveAlert(Alert alert) {
        return alertRepository.save(alert);
    }

    public Optional<Alert> getAlertById(Long id) {
        return alertRepository.findById(id);
    }

    public Optional<Alert> updateAlert(Long id, Alert alert) {
        return alertRepository.findById(id).map(existing -> {
            alert.setId(existing.getId());
            return alertRepository.save(alert);
        });
    }

    public boolean deleteAlert(Long id) {
        if (alertRepository.existsById(id)) {
            alertRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private static final String BASE_URL = "https://api.weather.gov";

    // 0. Busca todos os alertas do banco de dados com paginação e ordenação
    //    Retorna uma lista de alertas filtrados por evento, status e área.
    public Page<AlertResumoDTO> getAlertsResumoFromDb(int page, int size, String sort, String event, String status, String area) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        return alertRepository.findResumoByFilters(event, status, area, pageable);
    }

    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        return OffsetDateTime.parse(dateStr).toLocalDateTime();
    }
    
    // 1. Sincroniza os alertas com a API externa e salva no banco de dados, normalizando os dados
    //    e tratando possíveis erros de formatação de data.
    //    Retorna uma mensagem de sucesso com a quantidade de alertas salvos.
    //    Os parâmetros são opcionais e podem ser usados para filtrar os alertas.
    //    Se não forem fornecidos, serão usados os valores padrão da API.
    //    O limite padrão é 500, mas pode ser alterado pelo parâmetro 'limit'.
    //    O cursor é usado para paginação e pode ser fornecido para continuar a busca de onde parou.
    //    Exemplo de uso: /alerts/sync?start=2023-01-01T00:00:00Z&end=2023-01-02T00:00:00Z&status=actual&limit=100
    //    Exemplo de uso com cursor: /alerts/sync?cursor=eyJwYWdlIjoxfQ==
    public String saveAlertsFromApi(
        String start,
        String end,
        String status,
        String messageType,
        String event,
        String code,
        String area,
        String point,
        String region,
        String regionType,
        String zone,
        String urgency,
        String severity,
        String certainty,
        String limit,
        String cursor
    ) {
        String url = BASE_URL + "/alerts";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        if (start != null) builder.queryParam("start", start);
        if (end != null) builder.queryParam("end", end);
        if (status != null) builder.queryParam("status", status);
        if (messageType != null) builder.queryParam("message_type", messageType);
        if (event != null) builder.queryParam("event", event);
        if (code != null) builder.queryParam("code", code);
        if (area != null) builder.queryParam("area", area);
        if (point != null) builder.queryParam("point", point);
        if (region != null) builder.queryParam("region", region);
        if (regionType != null) builder.queryParam("region_type", regionType);
        if (zone != null) builder.queryParam("zone", zone);
        if (urgency != null) builder.queryParam("urgency", urgency);
        if (severity != null) builder.queryParam("severity", severity);
        if (certainty != null) builder.queryParam("certainty", certainty);
        if (limit != null && !limit.isBlank()) {
            builder.queryParam("limit", limit);
        } else {
            builder.queryParam("limit", "500");
        }
        if (cursor != null) builder.queryParam("cursor", cursor);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);

        List<Alert> savedAlerts = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode features = root.path("features");

            for (JsonNode feature : features) {
                JsonNode properties = feature.path("properties");
                Alert alert = new Alert();
                alert.setId(null);
                alert.setEvent(properties.path("event").asText());
                alert.setStatus(properties.path("status").asText());
                alert.setAreaDesc(properties.path("areaDesc").asText());
                alert.setUrgency(properties.path("urgency").asText());
                alert.setSeverity(properties.path("severity").asText());
                alert.setCertainty(properties.path("certainty").asText());
                alert.setSent(parseDate(properties.path("sent").asText()));
                alert.setEffective(parseDate(properties.path("effective").asText()));
                alert.setExpires(parseDate(properties.path("expires").asText()));
                alert.setHeadline(properties.path("headline").asText());
                alert.setDescription(properties.path("description").asText());
                alert.setInstruction(properties.path("instruction").asText());

                savedAlerts.add(alertRepository.save(alert));
            }
        } catch (Exception e) {
            System.err.println("Erro ao importar e salvar alertas: " + e.getMessage());
            e.printStackTrace();
        }
        return "Alertas importados e salvos com sucesso: " + savedAlerts.size();
    }

    // 2. Busca os alertas da API externa com os parâmetros fornecidos
    //    Retorna o corpo da resposta da API como uma String.
    //    Os parâmetros são opcionais e podem ser usados para filtrar os alertas.
    //    Se não forem fornecidos, serão usados os valores padrão da API.
    //    O limite padrão é 500, mas pode ser alterado pelo parâmetro 'limit'.
    public String getAlertsFromExternalApi(
        String start,
        String end,
        String status,
        String messageType,
        String event,
        String code,
        String area,
        String point,
        String region,
        String regionType,
        String zone,
        String urgency,
        String severity,
        String certainty,
        String limit,
        String cursor
    ) {
        String url = BASE_URL + "/alerts";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        if (start != null) builder.queryParam("start", start);
        if (end != null) builder.queryParam("end", end);
        if (status != null) builder.queryParam("status", status);
        if (messageType != null) builder.queryParam("message_type", messageType);
        if (event != null) builder.queryParam("event", event);
        if (code != null) builder.queryParam("code", code);
        if (area != null) builder.queryParam("area", area);
        if (point != null) builder.queryParam("point", point);
        if (region != null) builder.queryParam("region", region);
        if (regionType != null) builder.queryParam("region_type", regionType);
        if (zone != null) builder.queryParam("zone", zone);
        if (urgency != null) builder.queryParam("urgency", urgency);
        if (severity != null) builder.queryParam("severity", severity);
        if (certainty != null) builder.queryParam("certainty", certainty);
        if (limit != null && !limit.isBlank()) {
            builder.queryParam("limit", limit);
        } else {
            builder.queryParam("limit", "500");
        }
        if (cursor != null) builder.queryParam("cursor", cursor);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        return response.getBody();
    }

    // 3. Busca os tipos de alerta disponíveis na API
    //    Retorna o corpo da resposta da API como uma String.
    //    Não possui parâmetros, pois retorna uma lista fixa de tipos de alerta.
    //    Exemplo de uso: /alerts/types
    //    Retorna uma lista de tipos de alerta, como "Tornado", "Severe Thunderstorm", etc.
    //    A lista pode ser usada para filtrar os alertas por tipo.
    //    Exemplo de uso: /alerts/types?type=Tornado
    public Object getAlertTypes() {
        String url = BASE_URL + "/alerts/types";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getBody();
    }
}