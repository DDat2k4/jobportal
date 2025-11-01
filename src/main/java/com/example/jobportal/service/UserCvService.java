package com.example.jobportal.service;

import com.example.jobportal.data.entity.CvSection;
import com.example.jobportal.data.entity.UserCv;
import com.example.jobportal.data.pojo.FullCvResponse;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.CvSectionRepository;
import com.example.jobportal.repository.UserCvRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserCvService {

    private final UserCvRepository userCvRepo;
    private final CvSectionRepository sectionRepo;

    public UserCvService(UserCvRepository userCvRepo, CvSectionRepository sectionRepo) {
        this.userCvRepo = userCvRepo;
        this.sectionRepo = sectionRepo;
    }

    public Optional<UserCv> getById(Long id) {
        return userCvRepo.findById(id);
    }

    public Optional<UserCv> getByFilter(UserCv filter) {
        return userCvRepo.findByFilter(filter);
    }

    public Optional<UserCv> getDefaultByUserId(Long userId) {
        return userCvRepo.findDefaultByUserId(userId);
    }

    public UserCv create(UserCv cv) {
        return userCvRepo.create(cv);
    }

    public Optional<UserCv> update(Long id, UserCv cv) {
        return userCvRepo.update(id, cv);
    }

    public int delete(Long id) {
        return userCvRepo.delete(id);
    }

    public Page<UserCv> getAll(UserCv filter, Pageable pageable) {
        return userCvRepo.findAll(filter, pageable);
    }


    /** Lấy full CV (bao gồm section) */
    public Optional<FullCvResponse> getFullCv(Long cvId) {
        var cvOpt = userCvRepo.findById(cvId);
        if (cvOpt.isEmpty()) return Optional.empty();

        List<CvSection> sections = sectionRepo.findByCvId(cvId);
        return Optional.of(new FullCvResponse(cvOpt.get(), sections));
    }

    /** Cập nhật full CV (bao gồm section) */
    public boolean updateFullCv(Long id, FullCvResponse payload) {
        Optional<UserCv> cvOpt = userCvRepo.update(id, payload.getCv());
        if (cvOpt.isEmpty()) return false;

        // Xóa section cũ và thêm lại
        sectionRepo.deleteByCvId(id);
        payload.getSections().forEach(s -> {
            s.setCvId(id);
            sectionRepo.create(s);
        });

        return true;
    }

    /** Sao chép CV (clone 1 bản) */
    public Optional<UserCv> cloneCv(Long cvId) {
        var original = userCvRepo.findById(cvId);
        if (original.isEmpty()) return Optional.empty();

        UserCv clone = new UserCv()
                .setUserId(original.get().getUserId())
                .setTitle(original.get().getTitle() + " (Copy)")
                .setTemplateCode(original.get().getTemplateCode())
                .setSummary(original.get().getSummary())
                .setData(original.get().getData())
                .setIsDefault(false);

        UserCv newCv = userCvRepo.create(clone);

        // Clone luôn các section
        List<CvSection> sections = sectionRepo.findByCvId(cvId);
        for (CvSection section : sections) {
            sectionRepo.create(
                    new CvSection()
                            .setCvId(newCv.getId())
                            .setType(section.getType())
                            .setTitle(section.getTitle())
                            .setContent(section.getContent())
                            .setPosition(section.getPosition())
            );
        }

        return Optional.of(newCv);
    }

    public Optional<UserCv> setDefault(Long id) {
        return userCvRepo.findById(id).map(cv -> {
            // Bỏ default cũ của user
            userCvRepo.unsetDefaultByUserId(cv.getUserId());
            // Đặt CV này là default
            return userCvRepo.updateIsDefault(id, true).orElse(cv);
        });
    }
}
