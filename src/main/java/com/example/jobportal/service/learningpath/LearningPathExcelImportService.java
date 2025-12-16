package com.example.jobportal.service.learningpath;

import com.example.jobportal.data.entity.Skill;
import com.example.jobportal.data.entity.learningpath.LearningResource;
import com.example.jobportal.data.entity.learningpath.RoadmapTemplate;
import com.example.jobportal.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LearningPathExcelImportService {

    private final SkillService skillService;
    private final RoadmapTemplateService roadmapTemplateService;
    private final LearningResourceService learningResourceService;

    private static final DataFormatter FORMATTER = new DataFormatter();

    @Transactional
    public void importAll(MultipartFile file) {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {

            System.out.println(">>> Import SKILLS");
            importSkill(workbook.getSheet("SKILLS"));

            System.out.println(">>> Import ROADMAP_TEMPLATES");
            importRoadmapTemplates(workbook.getSheet("ROADMAP_TEMPLATES"));

            System.out.println(">>> Import LEARNING_RESOURCES");
            importLearningResources(workbook.getSheet("LEARNING_RESOURCES"));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Import Excel failed: " + e.getMessage(), e);
        }
    }

    private void importSkill(Sheet sheet) {
        if (sheet == null) return;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row r = sheet.getRow(i);
            if (r == null) continue;

            try {
                String name = FORMATTER.formatCellValue(r.getCell(0)).trim();
                if (name.isEmpty()) continue;

                String type = FORMATTER.formatCellValue(r.getCell(1)).trim();
                int difficulty = Integer.parseInt(FORMATTER.formatCellValue(r.getCell(2)).trim());
                String aliasesRaw = FORMATTER.formatCellValue(r.getCell(3));
                String description = FORMATTER.formatCellValue(r.getCell(4));

                String normalized = normalize(name);

                if (skillService.findByNameIgnoreCase(name).isPresent()) {
                    System.out.println("SKIP duplicate skill: " + name);
                    continue;
                }

                Skill skill = new Skill()
                        .setName(name)
                        .setType(type)
                        .setDifficulty(difficulty)
                        .setNormalizedName(normalized)
                        .setAliases(parseAliases(aliasesRaw))
                        .setDescription(description);

                skillService.create(skill);

            } catch (Exception e) {
                throw new RuntimeException("Error at sheet SKILLS, row " + (i + 1), e);
            }
        }
    }


    private void importRoadmapTemplates(Sheet sheet) {
        if (sheet == null) return;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row r = sheet.getRow(i);
            if (r == null) continue;

            try {
                String skillName = FORMATTER.formatCellValue(r.getCell(0));

                Skill skill = skillService.findByNormalizedName(normalize(skillName))
                        .orElseThrow(() -> new RuntimeException("Skill not found: " + skillName));

                RoadmapTemplate t = new RoadmapTemplate()
                        .setSkillId(skill.getId())
                        .setStepOrder(Integer.parseInt(FORMATTER.formatCellValue(r.getCell(1))))
                        .setTitle(FORMATTER.formatCellValue(r.getCell(2)))
                        .setAction(FORMATTER.formatCellValue(r.getCell(3)))
                        .setDurationDays(Integer.parseInt(FORMATTER.formatCellValue(r.getCell(4))));

                roadmapTemplateService.create(t);

            } catch (Exception e) {
                throw new RuntimeException("Error at sheet ROADMAP_TEMPLATES, row " + (i + 1), e);
            }
        }
    }

    private void importLearningResources(Sheet sheet) {
        if (sheet == null) return;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row r = sheet.getRow(i);
            if (r == null) continue;

            try {
                String skillName = FORMATTER.formatCellValue(r.getCell(0));

                Skill skill = skillService.findByNormalizedName(normalize(skillName))
                        .orElseThrow(() -> new RuntimeException("Skill not found: " + skillName));

                LearningResource lr = new LearningResource()
                        .setSkillId(skill.getId())
                        .setTitle(FORMATTER.formatCellValue(r.getCell(1)))
                        .setUrl(FORMATTER.formatCellValue(r.getCell(2)))
                        .setType(FORMATTER.formatCellValue(r.getCell(3)))
                        .setDifficulty(Integer.parseInt(FORMATTER.formatCellValue(r.getCell(4))))
                        .setDurationMinutes(Integer.parseInt(FORMATTER.formatCellValue(r.getCell(5))))
                        .setProvider(FORMATTER.formatCellValue(r.getCell(6)));

                if (learningResourceService.existsBySkillAndUrl(skill.getId(), lr.getUrl())) {
                    continue;
                }

                learningResourceService.create(lr);

            } catch (Exception e) {
                throw new RuntimeException("Error at sheet LEARNING_RESOURCES, row " + (i + 1), e);
            }
        }
    }

    private String normalize(String input) {
        return input == null ? "" :
                input.toLowerCase()
                        .replaceAll("[^a-z0-9 ]", "")
                        .replaceAll("\\s+", " ")
                        .trim();
    }

    private List<String> parseAliases(String raw) {
        if (raw == null || raw.isBlank()) return List.of();
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}