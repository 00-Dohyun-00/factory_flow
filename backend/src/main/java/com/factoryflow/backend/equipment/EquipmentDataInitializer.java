package com.factoryflow.backend.equipment;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class
EquipmentDataInitializer implements CommandLineRunner {

    private final EquipmentRepository equipmentRepository;

    public EquipmentDataInitializer(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    public void run(String... args) {
        if (equipmentRepository.count() > 0) return;

        equipmentRepository.saveAll(List.of(
            new Equipment("1", "EQ001", "사출성형기 A", "1공장 A라인", "active",      "김철수", "2024-05-25", "2023-01-15"),
            new Equipment("2", "EQ002", "포장기계 B",   "1공장 B라인", "maintenance", "이영희", "2024-05-20", "2023-02-20"),
            new Equipment("3", "EQ003", "절단기 C",     "2공장 A라인", "error",       "박민수", "2024-05-28", "2023-03-10"),
            new Equipment("4", "EQ004", "검사장비 D",   "2공장 B라인", "stopped",     "최수진", "2024-05-27", "2023-04-05")
        ));
    }
}
