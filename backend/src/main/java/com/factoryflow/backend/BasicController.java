package com.factoryflow.backend;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class BasicController {
    @GetMapping("/")
    String hello(){
        return "안녕하세요";
    }
}
