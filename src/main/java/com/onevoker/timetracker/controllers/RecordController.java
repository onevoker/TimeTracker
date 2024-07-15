package com.onevoker.timetracker.controllers;

import com.onevoker.timetracker.domain.services.interfaces.RecordService;
import com.onevoker.timetracker.dto.RecordRequest;
import com.onevoker.timetracker.dto.RecordResponse;
import com.onevoker.timetracker.security.services.VerifyService;
import com.onevoker.timetracker.security.userPrincipal.UserPrincipal;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/records")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class RecordController {
    private final RecordService recordService;
    private final VerifyService verifyService;

    @PostMapping("/projects/{projectId}/users/{userId}")
    @PreAuthorize("hasRole('ROLE_User')")
    public void createRecord(@PathVariable Integer userId,
                             @PathVariable Integer projectId,
                             @RequestBody RecordRequest recordRequest,
                             @AuthenticationPrincipal UserPrincipal principal
    ) {
        verifyService.verifyUserId(userId, principal);
        recordService.createRecord(recordRequest, userId, projectId);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_Admin')")
    public List<RecordResponse> getAllRecords() {
        return recordService.getAllRecords();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_User')")
    public RecordResponse getRecordById(@PathVariable Integer id) {
        return recordService.getRecordById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_User')")
    public void updateRecord(@PathVariable Integer id,
                             @RequestBody RecordRequest recordRequest,
                             @AuthenticationPrincipal UserPrincipal principal
    ) {
        verifyService.verifyUserByRecordId(id, principal);
        recordService.updateRecord(id, recordRequest);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_User')")
    public void deleteRecord(@PathVariable Integer id, @AuthenticationPrincipal UserPrincipal principal) {
        verifyService.verifyUserForDeleteRecord(id, principal);
        recordService.deleteRecord(id);
    }

    @GetMapping("/projects/{projectId}/between-dates")
    @PreAuthorize("hasRole('ROLE_User')")
    public List<RecordResponse> getRecordsOnProjectBetweenDates(@PathVariable Integer projectId,
                                                                @RequestParam OffsetDateTime startDate,
                                                                @RequestParam OffsetDateTime endDate) {
        return recordService.getRecordsOnProjectBetweenDates(projectId, startDate, endDate);
    }

    @GetMapping("/projects/{projectId}/users/{userId}/between-dates")
    @PreAuthorize("hasRole('ROLE_User')")
    public List<RecordResponse> getRecordsOnProjectForUserBetweenDates(@PathVariable Integer projectId,
                                                                       @PathVariable Integer userId,
                                                                       @RequestParam OffsetDateTime startDate,
                                                                       @RequestParam OffsetDateTime endDate) {
        return recordService.getRecordsOnProjectForUserBetweenDates(projectId, userId, startDate, endDate);
    }

    @GetMapping("/users/{userId}/between-dates")
    @PreAuthorize("hasRole('ROLE_User')")
    public List<RecordResponse> getUserRecordsAcrossProjectsBetweenDates(@PathVariable Integer userId,
                                                                         @RequestParam OffsetDateTime startDate,
                                                                         @RequestParam OffsetDateTime endDate) {
        return recordService.getUserRecordsAcrossProjectsBetweenDates(userId, startDate, endDate);
    }

    @GetMapping("/between-dates")
    @PreAuthorize("hasRole('ROLE_Admin')")
    public List<RecordResponse> getAllRecordsBetweenDates(@RequestParam OffsetDateTime startDate,
                                                          @RequestParam OffsetDateTime endDate) {
        return recordService.getAllRecordsBetweenDates(startDate, endDate);
    }
}
