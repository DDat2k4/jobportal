package com.example.jobportal.service;

import com.example.jobportal.data.entity.UserProfile;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository repo;

    public Optional<UserProfile> getById(Long id) {
        return repo.findById(id);
    }

    public Optional<UserProfile> getByUserId(Long userId) {
        return repo.findByUserId(userId).map(record -> record.into(UserProfile.class));
    }

    public UserProfile create(UserProfile profile) {
        return repo.create(profile);
    }

    public Optional<UserProfile> update(UserProfile profile) {
        return repo.update(profile);
    }

    public int deleteByUserId(Long userId) {
        return repo.deleteByUserId(userId);
    }

    public Page<UserProfile> getAll(UserProfile filter, Pageable pageable) {
        return repo.findAll(filter, pageable);
    }

    public long count(UserProfile filter) {
        return repo.count(filter);
    }

    public void insertBasic(Long userId, String name, String avatar) {
        repo.insert(userId, name, avatar);
    }

    public void updateBasic(Long userId, String name, String avatar) {
        repo.update(userId, name, avatar);
    }
}
