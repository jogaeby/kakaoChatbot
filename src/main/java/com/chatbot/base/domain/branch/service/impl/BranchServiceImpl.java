package com.chatbot.base.domain.branch.service.impl;

import com.chatbot.base.common.GoogleSheetUtil;
import com.chatbot.base.domain.branch.dto.BranchDto;
import com.chatbot.base.domain.branch.service.BranchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BranchServiceImpl implements BranchService {
    private final String SHEET_ID = "12LK-mODVa9b5b8KA_m68GUF50AojwdOK7_0cok3inFM";
    private final String BRANCH_SHEET_NAME = "지점정보";

    private final GoogleSheetUtil googleSheetUtil;

    @Override
    public BranchDto getBranch(String channelId) {
        try {
            List<List<Object>> rows = googleSheetUtil.readAllSheet(SHEET_ID, BRANCH_SHEET_NAME);
            // 데이터가 존재하고, 헤더 다음 행부터 탐색
            if (rows.size() <= 1) {
                throw new NoSuchElementException("지점 정보가 없습니다.");
            }

            List<Object> row = rows.get(1);
            return BranchDto.builder()
                    .id(row.get(0).toString())
                    .name(row.get(1).toString())
                    .ownerName(row.get(2).toString())
                    .ownerPhone(row.get(3).toString())
                    .accountName(row.get(4).toString())
                    .accountBankName(row.get(5).toString())
                    .accountNum(row.get(6).toString())
                    .build();
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }
}
