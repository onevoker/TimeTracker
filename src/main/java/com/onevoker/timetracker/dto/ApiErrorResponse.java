package com.onevoker.timetracker.dto;

import lombok.Builder;

@Builder
public record ApiErrorResponse(
        String exceptionName,
        String exceptionMessage,
        String code
) {
}
