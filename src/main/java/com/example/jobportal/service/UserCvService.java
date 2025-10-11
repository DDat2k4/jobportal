package com.example.jobportal.service;

import com.example.jobportal.data.entity.UserCv;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.UserCvRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserCvService {

    private final UserCvRepository repo;

    public UserCvService(UserCvRepository repo) {
        this.repo = repo;
    }

    public Optional<UserCv> getById(Long id) {
        return repo.findById(id);
    }

    public Optional<UserCv> getByFilter(UserCv filter) {
        return repo.findByFilter(filter);
    }

    public Optional<UserCv> getDefaultByUserId(Long userId) {
        return repo.findDefaultByUserId(userId);
    }

    public UserCv create(UserCv cv) {
        return repo.create(cv);
    }

    public Optional<UserCv> update(Long id, UserCv cv) {
        return repo.update(id, cv);
    }

    public int delete(Long id) {
        return repo.delete(id);
    }

    public Page<UserCv> getAll(UserCv filter, Pageable pageable) {
        return repo.findAll(filter, pageable);
    }
}
