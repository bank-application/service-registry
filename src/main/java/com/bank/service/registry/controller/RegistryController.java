package com.bank.service.registry.controller;

import com.bank.service.registry.entity.ServiceInfo;
import com.bank.service.registry.service.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/registry")
public class RegistryController {

    @Autowired
    private RegistryService registryService;

    @PostMapping("/register")
    public ResponseEntity<String> registerService(@RequestBody ServiceInfo serviceInfo){
        registryService.register(serviceInfo);
        return ResponseEntity.status(HttpStatus.OK).body("Service successfully register.");
    }

    @PostMapping("/services")
    public ResponseEntity<List<ServiceInfo>> getAllServicesInfo(){
        List<ServiceInfo> serviceInfos = registryService.getAllRegisteredServices();
        return ResponseEntity.status(HttpStatus.OK).body(serviceInfos);
    }

    @PostMapping("/deregister")
    public ResponseEntity<String> deRegister(@RequestParam(value = "serviceId", required = true) String serviceId){
        registryService.deRegister(serviceId);
        return ResponseEntity.status(HttpStatus.OK).body("Service deregistered successfully.");
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus(){
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("status", "Service Registry Running UP");
        return ResponseEntity.status(HttpStatus.OK).body(status);
    }
}
