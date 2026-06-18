package com.factoryflow.backend.equipment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, String> {
    long countByStatus(String status);
    List<Equipment> findAllByOrderByCodeAsc();
}
