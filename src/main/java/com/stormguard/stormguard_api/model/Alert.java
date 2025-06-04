package com.stormguard.stormguard_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;
import java.time.LocalDateTime;


@Entity
public class Alert {
    @Id
    @NotBlank
    private String id;

    @NotBlank
    @Column(name = "event")
    private String event;

    @NotBlank
    @Column(name = "status")
    private String status;

    @Column(name = "area_desc", columnDefinition = "TEXT")
    private String areaDesc;

    @NotBlank
    private String urgency;

    @NotBlank
    @Column(name = "severity")
    private String severity;

    @NotBlank
    private String certainty;

    @Column(name = "sent")
    private LocalDateTime sent;

    @Column(name = "effective")
    private LocalDateTime effective;

    @Column(name = "expires")
    private LocalDateTime expires;

    @Column(name = "headline")
    private String headline;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "instruction", columnDefinition = "TEXT")
    private String instruction;

    @ManyToMany
    @JoinTable(
        name = "alert_area",
        joinColumns = @JoinColumn(name = "alert_id"),
        inverseJoinColumns = @JoinColumn(name = "area_code")
    )
    private Set<Area> areas;

    // Getters e Setters para os novos campos
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }
    public void setEvent(String event) {
        this.event = event;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getAreaDesc() {
    return areaDesc;
    }

    public void setAreaDesc(String areaDesc) {
        this.areaDesc = areaDesc;
    }

    public String getUrgency() {
        return urgency;
    }
    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }
    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getCertainty() {
        return certainty;
    }
    public void setCertainty(String certainty) {
        this.certainty = certainty;
    }

    public LocalDateTime getSent() {
        return sent;
    }
    public void setSent(LocalDateTime sent) {
        this.sent = sent;
    }   

    public LocalDateTime getEffective() {
        return effective;
    }
    public void setEffective(LocalDateTime effective) {
        this.effective = effective;
    }

    public LocalDateTime getExpires() {
        return expires;
    }
    public void setExpires(LocalDateTime expires) {
        this.expires = expires;
    }

    public String getHeadline() {
        return headline;
    }
    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstruction() {
        return instruction;
    }
    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
}