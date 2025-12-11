package com.example.jobportal.controller.learningpath;

import com.example.jobportal.data.pojo.learningpath.MatchResult;
import com.example.jobportal.data.pojo.learningpath.RoadmapResult;
import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.service.learningpath.MatchService;
import com.example.jobportal.service.learningpath.RoadmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;
    private final RoadmapService roadmapService;

    @GetMapping("/{userId}/job/{jobId}")
    @PreAuthorize("hasAuthority('MATCH_READ') or #userId == principal.id")
    public ApiResponse<MatchResult> match(@PathVariable Long userId, @PathVariable Long jobId) {
        return matchService.matchUserToJob(userId, jobId)
                .map(r -> ApiResponse.ok("Match computed", r))
                .orElseGet(() -> ApiResponse.error("Match not found"));
    }

    @GetMapping("/{userId}/job/{jobId}/roadmap")
    @PreAuthorize("hasAuthority('ROADMAP_READ') or #userId == principal.id")
    public ApiResponse<RoadmapResult> roadmap(@PathVariable Long userId, @PathVariable Long jobId) {
        RoadmapResult res = roadmapService.generateRoadmap(userId, jobId);
        return ApiResponse.ok("Roadmap generated", res);
    }

    /** Lấy tất cả match cho job dựa trên tất cả user có CV mặc định */
    @GetMapping("/job/{jobId}/matches")
    @PreAuthorize("hasAuthority('EMPLOYER_MATCH_READ') or @securityService.isJobOwner(#jobId, principal.id)")
    public ApiResponse<List<MatchResult>> getJobSeekerMatches(@PathVariable Long jobId) {
        List<MatchResult> matches = matchService.matchAllUsersToJob(jobId);
        if (matches.isEmpty()) return ApiResponse.error("No matches found for this job");
        return ApiResponse.ok("Matches retrieved", matches);
    }

    /** Lấy roadmap cho tất cả user match với job */
    @GetMapping("/job/{jobId}/roadmaps")
    @PreAuthorize("hasAuthority('EMPLOYER_ROADMAP_READ') or @securityService.isJobOwner(#jobId, principal.id)")
    public ApiResponse<List<RoadmapResult>> getJobSeekerRoadmaps(@PathVariable Long jobId) {
        List<MatchResult> matches = matchService.matchAllUsersToJob(jobId);
        if (matches.isEmpty()) return ApiResponse.error("No seekers found for this job");
        List<RoadmapResult> roadmaps = roadmapService.generateRoadmapsForJob(jobId, matches);
        return ApiResponse.ok("Roadmaps retrieved", roadmaps);
    }
}

