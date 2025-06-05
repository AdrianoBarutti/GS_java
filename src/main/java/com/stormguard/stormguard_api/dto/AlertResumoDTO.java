package com.stormguard.stormguard_api.dto;


public class AlertResumoDTO {
    private Long id;
    private String event;
    private String status;
    private String areaDesc;
    private String severity;
    private String urgency;
    private String certainty;

    public AlertResumoDTO(Long id, String event, String status, String areaDesc, String severity, String urgency, String certainty) {
        this.id = id;
        this.event = event;
        this.status = status;
        this.areaDesc = areaDesc;
        this.severity = severity;
        this.urgency = urgency;
        this.certainty = certainty;
    }

    public Long getId() { return id; }
    public String getEvent() { return event; }
    public String getStatus() { return status; }
    public String getAreaDesc() { return areaDesc; }
    public String getSeverity() { return severity; }
    public String getUrgency() { return urgency; }
    public String getCertainty() { return certainty; }
}