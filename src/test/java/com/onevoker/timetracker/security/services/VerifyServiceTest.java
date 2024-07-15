package com.onevoker.timetracker.security.services;

import com.onevoker.timetracker.domain.entities.RecordEntity;
import com.onevoker.timetracker.domain.entities.UserEntity;
import com.onevoker.timetracker.domain.services.entityFinder.EntityFinder;
import com.onevoker.timetracker.exceptions.UnauthorizedUserException;
import com.onevoker.timetracker.security.userPrincipal.UserPrincipal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VerifyServiceTest {
    @Mock
    private EntityFinder entityFinder;

    @InjectMocks
    private VerifyService verifyService;

    private static final String UNAUTHORIZED_USER_EXCEPTION_MESSAGE = "You can't manage other users' data, even if you are an admin";
    private static final String USERNAME = "Man";
    private static final String ROLE_USER = "ROLE_User";
    private static final String ROLE_ADMIN = "ROLE_Admin";
    private static final int SAME_USER_ID = 1;
    private static final int NOT_THE_SAME_USER_ID = 2;
    private static final int RECORD_ID = 100;


    private static UserPrincipal admin;
    private static UserPrincipal user;

    @BeforeAll
    static void setUp() {
        admin = createUserPrincipal(SAME_USER_ID, USERNAME, ROLE_ADMIN);
        user = createUserPrincipal(SAME_USER_ID, USERNAME, ROLE_USER);
    }

    @Test
    void testVerifyUserId() {
        verifyService.verifyUserId(SAME_USER_ID, user);
    }

    @Test
    void testVerifyUserIdThrowsUnauthorizedUserException() {
        assertThatThrownBy(() -> verifyService.verifyUserId(NOT_THE_SAME_USER_ID, admin))
                .isInstanceOf(UnauthorizedUserException.class)
                .hasMessageContaining(UNAUTHORIZED_USER_EXCEPTION_MESSAGE);
    }

    @Test
    void testVerifyUserIdOrAdmin_UserIsAdmin() {
        verifyService.verifyUserIdOrAdmin(NOT_THE_SAME_USER_ID, admin);
    }

    @Test
    void testVerifyUserIdOrAdmin_UserIsSameAsRequested() {
        verifyService.verifyUserIdOrAdmin(SAME_USER_ID, user);
    }

    @Test
    void testVerifyUserIdOrAdminThrowsUnauthorizedUserException() {
        assertThatThrownBy(() -> verifyService.verifyUserIdOrAdmin(NOT_THE_SAME_USER_ID, user))
                .isInstanceOf(UnauthorizedUserException.class)
                .hasMessageContaining(UNAUTHORIZED_USER_EXCEPTION_MESSAGE);
    }

    @Test
    void testVerifyUserForDeleteRecord() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(SAME_USER_ID);

        RecordEntity recordEntity = new RecordEntity();
        recordEntity.setUserEntity(userEntity);

        when(entityFinder.getRecordEntity(RECORD_ID)).thenReturn(recordEntity);

        verifyService.verifyUserForDeleteRecord(RECORD_ID, user);
    }

    @Test
    void testVerifyUserForDeleteRecordThrowsUnauthorizedUserException() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(NOT_THE_SAME_USER_ID);

        RecordEntity recordEntity = new RecordEntity();
        recordEntity.setUserEntity(userEntity);

        when(entityFinder.getRecordEntity(RECORD_ID)).thenReturn(recordEntity);

        assertThatThrownBy(() -> verifyService.verifyUserForDeleteRecord(RECORD_ID, user))
                .isInstanceOf(UnauthorizedUserException.class)
                .hasMessageContaining(UNAUTHORIZED_USER_EXCEPTION_MESSAGE);
    }

    @Test
    void testVerifyUserByRecordId() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(SAME_USER_ID);

        RecordEntity recordEntity = new RecordEntity();
        recordEntity.setUserEntity(userEntity);

        when(entityFinder.getRecordEntity(RECORD_ID)).thenReturn(recordEntity);

        verifyService.verifyUserByRecordId(RECORD_ID, user);
    }

    @Test
    void testVerifyUserByRecordIdThrowsUnauthorizedUserException() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(NOT_THE_SAME_USER_ID);

        RecordEntity recordEntity = new RecordEntity();
        recordEntity.setUserEntity(userEntity);

        when(entityFinder.getRecordEntity(RECORD_ID)).thenReturn(recordEntity);

        assertThatThrownBy(() -> verifyService.verifyUserForDeleteRecord(RECORD_ID, user))
                .isInstanceOf(UnauthorizedUserException.class)
                .hasMessageContaining(UNAUTHORIZED_USER_EXCEPTION_MESSAGE);
    }

    private static UserPrincipal createUserPrincipal(Integer userId, String username, String role) {
        return UserPrincipal.builder()
                .userId(userId)
                .username(username)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(role)))
                .build();
    }
}
