package com.bank.service.registry.service.impl;

import com.bank.service.registry.entity.ServiceInfo;
import com.bank.service.registry.service.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class RegistryServiceImpl implements RegistryService {

    private static final String COLLECTION_KEY = "service_info:collection";
    private static final String KEY_PREFIX = "service_info:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void register(ServiceInfo serviceInfo) {

        ServiceInfo instance = ServiceInfo.builder()
                .serviceId(UUID.randomUUID().toString())
                .serviceName(serviceInfo.getServiceName())
                .host(serviceInfo.getHost())
                .port(serviceInfo.getPort())
                .status("UP")
                .lastHeartbeatTime(System.currentTimeMillis())
                .registeredAt(String.valueOf(Instant.now()))
                .build();
        redisTemplate.opsForValue().set(KEY_PREFIX + instance.getServiceName(), instance);
        log.info("{} register successfully", instance.getServiceName());
        // Add service ID to collection
//        redisTemplate.opsForSet().add(COLLECTION_KEY, instance.getInstanceId());
    }

    @Override
    public void deRegister(String instanceId) {
        String key = KEY_PREFIX + instanceId;
        redisTemplate.delete(key);
        log.info("Deregister service successfully");
    }

    @Override
    public List<ServiceInfo> getAllRegisteredServices() {
        List<ServiceInfo> services = new ArrayList<>();

        RedisConnection connection = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection();

        // Scan for keys matching the service registry prefix
        Cursor<byte[]> cursor = connection.scan(
                ScanOptions.scanOptions().match(KEY_PREFIX + "*").count(1000).build()
        );

        try {
            while (cursor.hasNext()) {
                String key = new String(cursor.next());
                Object value = redisTemplate.opsForValue().get(key);
                if (value instanceof ServiceInfo serviceInfo) {
                    services.add(serviceInfo);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while scanning Redis keys", e);
        } finally {
            cursor.close();
        }
        log.info("Getting all services successfully");
        return services;
    }
}
