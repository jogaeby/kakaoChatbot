package com.chatbot.base.domain.branch.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BranchServiceTest {
    @Autowired
    private BranchService branchService;
    @Test
    void getBranch() {
        branchService.getBranch("");
    }
}