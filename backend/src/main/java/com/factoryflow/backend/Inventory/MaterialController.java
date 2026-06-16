package com.factoryflow.backend.Inventory;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
@CrossOrigin(origins = "*")
public class MaterialController {

    private final MaterialRepository materialRepository;

    public MaterialController(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @GetMapping
    public List<Material> getMaterialList() {
        return materialRepository.findAll();
    }


    @PostMapping
    public Material createMaterialList(@RequestBody Material material) {
        return materialRepository.save(material);
    }

    @PutMapping("/{id}")
    public Material updateMaterial(@PathVariable String id, @RequestBody Material material) {
        if (!materialRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 자재를 찾을 수 없습니다: " + id);
        }
        material.setId(id);
        return materialRepository.save(material);
    }

    @DeleteMapping("/{id}")
    public void deleteMaterial(@PathVariable String id) {

        if (!materialRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 자재를 찾을 수 없습니다: " + id);
        }

        materialRepository.deleteById(id);
    }
}
