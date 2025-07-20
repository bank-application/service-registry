package com.bank.service.registry.service;

import com.bank.service.registry.entity.ServiceInfo;

import java.util.List;

public interface RegistryService {
    void register(ServiceInfo serviceInfo);

    void deRegister(String instanceId);

    List<ServiceInfo> getAllRegisteredServices();
}
