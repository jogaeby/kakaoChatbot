package com.chatbot.base.domain.branch.service;

import com.chatbot.base.domain.branch.dto.BranchDto;

public interface BranchService {
    BranchDto getBranch(String channelId);
}
