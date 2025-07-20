package com.bank.service.registry.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceInfo implements Serializable {
    private String serviceId;
    private String serviceName;
    private String host;
    private int port;
    private String status;   // e.g., UP, DOWN, UNKNOWN, DEREGISTERED
    private String registeredAt;
    private long lastHeartbeatTime;
}
