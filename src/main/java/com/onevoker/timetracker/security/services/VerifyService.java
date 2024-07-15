package com.onevoker.timetracker.security.services;

import com.onevoker.timetracker.domain.entities.RecordEntity;
import com.onevoker.timetracker.domain.services.entityFinder.EntityFinder;
import com.onevoker.timetracker.exceptions.UnauthorizedUserException;
import com.onevoker.timetracker.security.userPrincipal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VerifyService {
    private final EntityFinder entityFinder;

    private static final String NOT_THE_SAME_USERS_MESSAGE = "You can't manage other users' data, even if you are an admin";
    private static final String ROLE_ADMIN = "ROLE_Admin";

    public void verifyUserId(int userId, UserPrincipal principal) {
        if (!principal.getUserId().equals(userId)) {
            throw new UnauthorizedUserException(NOT_THE_SAME_USERS_MESSAGE);
        }
    }

    public void verifyUserIdOrAdmin(int userId, UserPrincipal principal) {
        try {
            // check, if user want to delete himself
            verifyUserId(userId, principal);
        } catch (UnauthorizedUserException exception) {
            // check, if it's an admin
            if (!getAuthoritiesAsStrings(principal).contains(ROLE_ADMIN)) {
                throw new UnauthorizedUserException(NOT_THE_SAME_USERS_MESSAGE);
            }
        }

    }

    public void verifyUserForDeleteRecord(int recordId, UserPrincipal principal) {
        RecordEntity record = entityFinder.getRecordEntity(recordId);
        int userId = record.getUserEntity().getId();

        verifyUserId(userId, principal);
    }

    public void verifyUserByRecordId(int recordId, UserPrincipal principal) {
        RecordEntity record = entityFinder.getRecordEntity(recordId);
        int userId = record.getUserEntity().getId();

        verifyUserId(userId, principal);
    }

    private List<String> getAuthoritiesAsStrings(UserPrincipal principal) {
        return principal.getAuthorities().stream()
                .map(GrantedAuthority::toString)
                .toList();
    }
}
