package com.bank.service.registry.scheduler;

import com.bank.service.registry.entity.ServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Set;

@Slf4j
@Service
public class ServiceHealthChecker {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String KEY_PREFIX = "service_info:";

    public ServiceHealthChecker(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(fixedRate = 30000)
    public void checkAllServices(){
        Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");
        for (String key : keys) {
            ServiceInfo serviceInfo = (ServiceInfo) redisTemplate.opsForValue().get(key);
            if (serviceInfo == null) continue;
            boolean isRunning = ping(serviceInfo.getHost(), serviceInfo.getPort());
            serviceInfo.setStatus(isRunning ? "UP" : "DOWN");
            serviceInfo.setLastHeartbeatTime(System.currentTimeMillis());
            redisTemplate.opsForValue().set(key, serviceInfo);
        }
    }

    private boolean ping(String host, int port){
        try(Socket socket = new Socket()){
            socket.connect(new InetSocketAddress(host, port), 2000);
            log.info("Successfully ping host : {} and port : {}", host, port);
            return true;
        }catch (Exception e){
            log.warn("Unable to ping host: {} port: {} — {}", host, port, e.getMessage());
            return false;
        }
    }
}
