package com.factoryflow.backend.Inventory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, String> {
    long countByStatusIn(List<String> statuses);
    List<Material> findAllByOrderByCodeAsc();
}
