package com.factoryflow.backend.equipment;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    private String id;
    private String code;
    private String name;
    private String location;
    private String status;
    private String manager;
    private String lastCheckDate;
    private String registeredDate;

    public Equipment() {}

    public Equipment(String id, String code, String name, String location, String status, String manager, String lastCheckDate, String registeredDate) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.location = location;
        this.status = status;
        this.manager = manager;
        this.lastCheckDate = lastCheckDate;
        this.registeredDate = registeredDate;
    }

    public String getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getStatus() { return status; }
    public String getManager() { return manager; }
    public String getLastCheckDate() { return lastCheckDate; }
    public String getRegisteredDate() { return registeredDate; }

    public void setId(String id) { this.id = id; }
    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setLocation(String location) { this.location = location; }
    public void setStatus(String status) { this.status = status; }
    public void setManager(String manager) { this.manager = manager; }
    public void setLastCheckDate(String lastCheckDate) { this.lastCheckDate = lastCheckDate; }
    public void setRegisteredDate(String registeredDate) { this.registeredDate = registeredDate; }
}