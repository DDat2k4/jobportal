package com.example.jobportal.service;

import com.example.jobportal.data.entity.Permission;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PermissionService {

    private final PermissionRepository repo;

    public PermissionService(PermissionRepository repo) {
        this.repo = repo;
    }

    public Optional<Permission> getById(Long id) {
        return repo.findById(id);
    }

    public Optional<Permission> getByCode(String code) {
        return repo.findByCode(code);
    }

    public Permission create(Permission permission) {
        return repo.create(permission);
    }

    public Optional<Permission> update(Long id, Permission permission) {
        return repo.update(id, permission);
    }

    public int delete(Long id) {
        return repo.delete(id);
    }

    public Page<Permission> getAll(Permission filter, Pageable pageable) {
        return repo.findAll(filter, pageable);
    }
}
