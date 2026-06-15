package com.factoryflow.backend.equipment;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@CrossOrigin(origins = "*")
public class EquipmentController {

    private final EquipmentRepository equipmentRepository;

    public EquipmentController(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @GetMapping
    public List<Equipment> getEquipmentList() {
        return equipmentRepository.findAll();
    }

    @PostMapping
    public Equipment createEquipmentList(@RequestBody Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    @PutMapping("/{id}")
    public Equipment updateEquipment(@PathVariable String id, @RequestBody Equipment equipment) {
        equipment.setId(id);
        return equipmentRepository.save(equipment);
    }

    @DeleteMapping("/{id}")
    public void deleteEquipment(@PathVariable String id) {
        equipmentRepository.deleteById(id);
    }

}