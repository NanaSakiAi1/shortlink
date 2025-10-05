package com.nageoffer.shortlink.shortlinkporject.controller;

import com.nageoffer.shortlink.shortlinkporject.common.convention.result.Result;
import com.nageoffer.shortlink.shortlinkporject.common.convention.result.Results;
import com.nageoffer.shortlink.shortlinkporject.dto.req.RecycleBinSaveReqDTO;
import com.nageoffer.shortlink.shortlinkporject.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 回收站控制层
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    private final RecycleBinService recycleBinService;

    @PostMapping("/api/short-link/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO ReqDTO){
        recycleBinService.saveRecycleBin(ReqDTO);
        return Results.success();
    }
}
