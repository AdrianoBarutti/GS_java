package com.stormguard.stormguard_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.util.Set;
import java.time.LocalDateTime;


@Entity
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O campo 'event' é obrigatório.")
    private String event;

    @NotNull(message = "O campo 'status' é obrigatório.")
    private String status;

    @NotNull(message = "O campo 'areaDesc' é obrigatório.")
    private String areaDesc;

    @NotNull(message = "O campo 'urgency' é obrigatório.")
    private String urgency;

    @NotNull(message = "O campo 'severity' é obrigatório.")
    private String severity;

    @NotNull(message = "O campo 'certainty' é obrigatório.")
    private String certainty;

    @PastOrPresent(message = "O campo 'sent' deve ser uma data no passado ou presente.")
    private LocalDateTime sent;

    @NotNull(message = "O campo 'effective' é obrigatório.")
    private LocalDateTime effective;

    @Future(message = "O campo 'expires' deve ser uma data no futuro.")
    private LocalDateTime expires;

    private String headline;
    private String description;
    private String instruction;
    @ManyToMany
    @JoinTable(
        name = "alert_area",
        joinColumns = @JoinColumn(name = "alert_id"),
        inverseJoinColumns = @JoinColumn(name = "area_code")
    )
    private Set<Area> areas;

    // Getters e Setters para os novos campos
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
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