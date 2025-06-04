package com.stormguard.stormguard_api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.*;
import java.util.Set;


@Entity
public class Area {
    @Id
    @Column(name = "code")
    @NotBlank
    private String code;

    @Column(name = "name")
    @NotBlank
    private String name;

    @ManyToMany(mappedBy = "areas")
    private Set<Alert> alerts;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(Set<Alert> alerts) {
        this.alerts = alerts;
    }
}