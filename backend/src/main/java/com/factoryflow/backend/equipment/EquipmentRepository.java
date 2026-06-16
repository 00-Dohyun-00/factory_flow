package com.factoryflow.backend.equipment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<Equipment, String> {
    long countByStatus(String status);
}
