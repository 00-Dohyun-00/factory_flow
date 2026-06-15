package com.factoryflow.backend.productionresult;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @PostMapping
    public ProductionResult createProductionResult(@RequestBody ProductionResult productionResult) {
        return productionResultRepository.save(productionResult);
    }

    @PutMapping("/{id}")
    public ProductionResult updateProductionResult(@PathVariable String id, @RequestBody ProductionResult productionResult) {
        productionResult.setId(id);
        return productionResultRepository.save(productionResult);
    }

    @DeleteMapping("/{id}")
    public void deleteProductionResult(@PathVariable String id) {

        if (!productionResultRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 생산실적을 찾을 수 없습니다: " + id);
        }


        productionResultRepository.deleteById(id);
    }
}