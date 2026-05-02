package com.orderinventorymanagementsystem.authservice.mapper;


import com.orderinventorymanagementsystem.authservice.dto.TenantResponseDTO;
import com.orderinventorymanagementsystem.authservice.entity.Tenant;

public class TenantMapper {

    public static TenantResponseDTO toDTO(Tenant tenant) {
        return new TenantResponseDTO(
                tenant.getId(),
                tenant.getName(),
                tenant.getStatus().name()
        );
    }
}