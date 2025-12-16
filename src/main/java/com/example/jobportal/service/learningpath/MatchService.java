package com.example.jobportal.service.learningpath;

import com.example.jobportal.data.entity.*;
import com.example.jobportal.data.pojo.learningpath.MatchResult;
import static com.example.jobportal.util.CommonUtils.normalize;
import com.example.jobportal.repository.*;
import com.example.jobportal.service.UserCvService;
import com.example.jobportal.util.FuzzyMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class MatchService {

    private static final Logger log = LoggerFactory.getLogger(MatchService.class);

    private static final double WEIGHT_SKILL = 0.6;
    private static final double WEIGHT_EXPERIENCE = 0.3;
    private static final double WEIGHT_EDUCATION = 0.1;

    private final JobRepository jobRepo;
    private final JobSkillRepository jobSkillRepo;
    private final SkillRepository skillRepo;
    private final UserCvRepository userCvRepo;
    private final CvSectionRepository sectionRepo;
    private final UserCvService userCvService;

    private final Map<Long, String> skillNameCache = new ConcurrentHashMap<>();

    public MatchService(JobRepository jobRepo,
                        JobSkillRepository jobSkillRepo,
                        SkillRepository skillRepo,
                        UserCvRepository userCvRepo,
                        CvSectionRepository sectionRepo,
                        UserCvService userCvService) {
        this.jobRepo = jobRepo;
        this.jobSkillRepo = jobSkillRepo;
        this.skillRepo = skillRepo;
        this.userCvRepo = userCvRepo;
        this.sectionRepo = sectionRepo;
        this.userCvService = userCvService;
    }

    /**
     * MAIN MATCH
     */
    public Optional<MatchResult> matchUserToJob(Long userId, Long jobId) {

        log.info("MATCH USER {} TO JOB {}", userId, jobId);

        var jobOpt = jobRepo.findById(jobId);
        if (jobOpt.isEmpty()) {
            log.warn("Job {} not found", jobId);
            return Optional.empty();
        }

        Job job = jobOpt.get();

        List<JobSkill> jobSkills = jobSkillRepo.findByJobId(jobId);
        if (jobSkills == null) jobSkills = List.of();
        log.info("Job has {} required skills", jobSkills.size());

        // --- Load CV
        var cvOpt = userCvRepo.findDefaultByUserId(userId);
        if (cvOpt.isEmpty()) {
            log.warn("User {} does not have a default CV", userId);
            return Optional.empty();
        }
        Long cvId = cvOpt.get().getId();

        // --- Load sections
        List<CvSection> sections = sectionRepo.findByCvId(cvId);
        log.info("Loaded {} sections from CV {}", sections.size(), cvId);

        // --- Extract skills
        List<String> userSkillNames = extractSkillsFromSections(sections).stream()
                .map(s -> normalize(s))
                .distinct()
                .toList();

        log.info("Extracted skills from CV: {}", userSkillNames);

        // --- Experience
        double userYears = extractYearsFromSections(sections);
        log.info("User experience years = {}", userYears);

        // --- Education
        int educationScore = extractEducationScoreFromSections(sections, job);
        log.info("Education score = {}", educationScore);

        // --- Do matching
        MatchResult result = matchSkill(
                userId, jobId, job,
                jobSkills, userSkillNames,
                userYears, educationScore
        );

        log.info("Total match score = {}", result.getTotalScore());
        return Optional.of(result);
    }

    // ------------------------------------------------------
    // SKILL PARSING
    // ------------------------------------------------------

    private List<String> extractSkillsFromSections(List<CvSection> sections) {
        List<String> skills = new ArrayList<>();

        for (CvSection sec : sections) {
            if (!"SKILL".equalsIgnoreCase(sec.getType())) continue;

            Map<String, Object> content = sec.getContent();
            if (content == null) continue;

            List<Map<String, Object>> items =
                    (List<Map<String, Object>>) content.get("items");

            if (items == null) continue;

            for (Map<String, Object> item : items) {
                String name = (String) item.get("name");
                if (name == null || name.isBlank()) continue;

                skills.add(name);
            }
        }

        log.info("Parsed {} raw skills from sections", skills.size());
        return skills;
    }

    // ------------------------------------------------------
    // EXPERIENCE
    // ------------------------------------------------------

    private double extractYearsFromSections(List<CvSection> sections) {
        List<CvSection> exps = sections.stream()
                .filter(s -> "EXPERIENCE".equalsIgnoreCase(s.getType()))
                .toList();

        double totalMonths = 0;

        for (CvSection sec : exps) {
            Map<String, Object> content = sec.getContent();
            if (content == null) continue;

            List<Map<String, Object>> items =
                    (List<Map<String, Object>>) content.get("items");

            if (items == null) continue;

            for (Map<String, Object> item : items) {
                String start = (String) item.get("startDate");
                String end = (String) item.getOrDefault("endDate", "present");

                try {
                    YearMonth s = YearMonth.parse(start);
                    YearMonth e = "present".equalsIgnoreCase(end)
                            ? YearMonth.now()
                            : YearMonth.parse(end);

                    totalMonths += (e.getYear() - s.getYear()) * 12
                            + (e.getMonthValue() - s.getMonthValue());
                } catch (Exception ignore) {
                }
            }
        }

        return totalMonths / 12.0;
    }

    // ------------------------------------------------------
    // EDUCATION
    // ------------------------------------------------------

    private int extractEducationScoreFromSections(List<CvSection> sections, Job job) {

        // 1) Map required education from job → int level
        int requiredLevel = mapEducation(job.getRequiredEducation());

        // 2) Extract highest education level from CV
        int highestLevel = 0;

        List<CvSection> eduSecs = sections.stream()
                .filter(s -> "EDUCATION".equalsIgnoreCase(s.getType()))
                .toList();

        for (CvSection sec : eduSecs) {
            List<Map<String, Object>> items = (List<Map<String, Object>>) sec.getContent().get("items");
            if (items == null) continue;

            for (Map<String, Object> item : items) {
                String degree = (String) item.get("degree");
                if (degree == null) continue;

                int level = mapEducation(degree);
                highestLevel = Math.max(highestLevel, level);
            }
        }

        // 3) Match score: nếu >= yêu cầu → 100 điểm, ngược lại tính % khoảng cách
        if (requiredLevel == 0) return 100; // không yêu cầu → full score

        double ratio = (double) highestLevel / requiredLevel;
        int score = (int) Math.round(Math.min(ratio, 1.0) * 100);

        return score;
    }

    private int mapEducation(String edu) {
        if (edu == null) return 0;

        String e = edu.trim().toUpperCase();

        return switch (e) {
            case "PHD", "DOCTOR", "DOCTORATE" -> 5;
            case "MASTER", "MSC", "MA" -> 4;
            case "BACHELOR", "BA", "BS" -> 3;
            case "COLLEGE", "ASSOCIATE" -> 2;
            case "HIGH_SCHOOL" -> 1;
            default -> 0; // NONE
        };
    }

    // ------------------------------------------------------
    // MATCH SKILL + CALC SCORE
    // ------------------------------------------------------

    private MatchResult matchSkill(Long userId,
                                   Long jobId,
                                   Job job,
                                   List<JobSkill> jobSkills,
                                   List<String> userSkills,
                                   double userYears,
                                   int educationScore) {

        log.info("Start skill matching...");

        double totalSkillWeight = jobSkills.stream()
                .mapToDouble(js -> js.getPriority() != null && js.getPriority() == 2 ? 1.5 : 1.0)
                .sum();

        List<String> matchedSkillNames = new ArrayList<>();
        List<JobSkill> matchedJobSkills = new ArrayList<>();
        List<String> missingSkillNames = new ArrayList<>();
        List<JobSkill> missingJobSkills = new ArrayList<>();

        for (JobSkill js : jobSkills) {
            String jobSkillName = lookupSkillName(js.getSkillId());
            String normalized = normalize(jobSkillName);

            boolean matched = userSkills.stream()
                    .anyMatch(us -> FuzzyMatcher.matches(us, normalized));

            if (matched) {
                log.info("MATCHED skill: {}", jobSkillName);
                matchedSkillNames.add(jobSkillName);
                matchedJobSkills.add(js);
            } else {
                log.info("MISSING skill: {}", jobSkillName);
                missingSkillNames.add(jobSkillName);
                missingJobSkills.add(js);
            }
        }

        double matchedWeight = matchedJobSkills.stream()
                .mapToDouble(js -> js.getPriority() != null && js.getPriority() == 2 ? 1.5 : 1.0)
                .sum();

        int skillScore = totalSkillWeight == 0
                ? 100
                : (int) Math.round((matchedWeight / totalSkillWeight) * 100);

        int experienceScore = (int) Math.round(Math.min(userYears / 5.0, 1.0) * 100);

        int totalScore = (int) Math.round(
                skillScore * WEIGHT_SKILL +
                        experienceScore * WEIGHT_EXPERIENCE +
                        educationScore * WEIGHT_EDUCATION
        );

        log.info("Skill score = {}", skillScore);
        log.info("Experience score = {}", experienceScore);
        log.info("Education score = {}", educationScore);

        return MatchResult.builder()
                .userId(userId)
                .jobId(jobId)
                .totalScore(totalScore)
                .skillScore(skillScore)
                .experienceScore(experienceScore)
                .educationScore(educationScore)
                .matchedSkills(matchedSkillNames)
                .missingSkills(missingSkillNames)
                .matchedJobSkills(matchedJobSkills)
                .missingJobSkills(missingJobSkills)
                .build();
    }

    private String lookupSkillName(Long skillId) {
        return skillNameCache.computeIfAbsent(
                skillId,
                id -> skillRepo.findById(id).map(Skill::getName).orElse("Unknown")
        );
    }

    /**
     * Match tất cả user có CV với một job, trả về danh sách MatchResult
     */
    public List<MatchResult> matchAllUsersToJob(Long jobId) {
        var jobOpt = jobRepo.findById(jobId);
        if (jobOpt.isEmpty()) return Collections.emptyList();
        Job job = jobOpt.get();

        List<JobSkill> jobSkills = jobSkillRepo.findByJobId(jobId);
        if (jobSkills == null) jobSkills = List.of();

        List<MatchResult> results = new ArrayList<>();

        for (UserCv cv : userCvService.getAllDefaultCvs()) {
            Long userId = cv.getUserId();
            Long cvId = cv.getId();
            List<CvSection> sections = sectionRepo.findByCvId(cvId);

            List<String> userSkillNames = extractSkillsFromSections(sections).stream()
                    .map(s -> normalize(s))
                    .distinct()
                    .toList();
            double userYears = extractYearsFromSections(sections);
            int educationScore = extractEducationScoreFromSections(sections, job);

            results.add(matchSkill(userId, jobId, job, jobSkills, userSkillNames, userYears, educationScore));
        }

        results.sort(Comparator.comparingInt(MatchResult::getTotalScore).reversed());
        return results;
    }

}