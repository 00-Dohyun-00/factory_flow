package com.factoryflow.backend.productionresult;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductionResultDataInitializer implements CommandLineRunner {

    private final ProductionResultRepository productionResultRepository;

    public ProductionResultDataInitializer(ProductionResultRepository productionResultRepository) {
        this.productionResultRepository = productionResultRepository;
    }

    @Override
    public void run(String... args) {
        if (productionResultRepository.count() > 0) return;

        productionResultRepository.saveAll(List.of(
            new ProductionResult("1", "PR2024001", "WO2024001", "플라스틱 부품 A", 250, 5,  98.0, "EQ001", "2024-05-29"),
            new ProductionResult("2", "PR2024002", "WO2024002", "포장박스 세트",   500, 10, 98.0, "EQ002", "2024-05-28"),
            new ProductionResult("3", "PR2024003", "WO2024001", "플라스틱 부품 A", 300, 8,  97.3, "EQ001", "2024-05-27"),
            new ProductionResult("4", "PR2024004", "WO2024001", "플라스틱 부품 A", 200, 2,  99.0, "EQ001", "2024-05-26")
        ));
    }
}