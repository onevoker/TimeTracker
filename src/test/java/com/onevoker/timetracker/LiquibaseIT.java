package com.onevoker.timetracker;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LiquibaseIT extends IntegrationTest{
    @Test
    void testContainerStarts() {
        assertThat(POSTGRES.isRunning()).isTrue();
    }
}
