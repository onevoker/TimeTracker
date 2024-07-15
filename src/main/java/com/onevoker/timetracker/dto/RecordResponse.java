package com.onevoker.timetracker.dto;

import java.time.OffsetDateTime;

public record RecordResponse(String projectName,
                             String username,
                             int hours,
                             String description,
                             OffsetDateTime createdAt
) {
}
