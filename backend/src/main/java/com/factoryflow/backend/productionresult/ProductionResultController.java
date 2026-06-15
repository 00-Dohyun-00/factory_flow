package com.factoryflow.backend.productionresult;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/production-results")
@CrossOrigin(origins = "*")
public class ProductionResultController {

    private final ProductionResultRepository productionResultRepository;

    public ProductionResultController(ProductionResultRepository productionResultRepository) {
        this.productionResultRepository = productionResultRepository;
    }

    @GetMapping
    public List<ProductionResult> getProductionResultList() {
        return productionResultRepository.findAll();
    }
}