package com.factoryflow.backend.Inventory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MaterialDataInitializer implements CommandLineRunner {

    private final MaterialRepository materialRepository;

    public MaterialDataInitializer(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @Override
    public void run(String... args) {
        if (materialRepository.count() > 0) return;

        materialRepository.saveAll(List.of(
            new Material("1", "MT001", "ABS 수지",      "원료",  150, 100, "kg",   "normal",   "2024-05-29"),
            new Material("2", "MT002", "포장박스",       "포장재",  50, 200, "개",   "critical", "2024-05-26"),
            new Material("3", "MT003", "라벨지",         "부자재",  80, 100, "roll", "low",      "2024-05-28"),
            new Material("4", "MT004", "스테인리스 강판", "원료",  300, 150, "kg",   "normal",   "2024-05-30")
        ));
    }
}
