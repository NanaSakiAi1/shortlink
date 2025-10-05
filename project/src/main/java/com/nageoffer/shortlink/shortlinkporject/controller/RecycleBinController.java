package com.nageoffer.shortlink.shortlinkporject.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nageoffer.shortlink.shortlinkporject.common.convention.result.Result;
import com.nageoffer.shortlink.shortlinkporject.common.convention.result.Results;
import com.nageoffer.shortlink.shortlinkporject.dto.req.RecycleBinRecoverReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.req.RecycleBinRemoveReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.req.RecycleBinSaveReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.resp.ShortLinkPageRespDTO;
import com.nageoffer.shortlink.shortlinkporject.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
    @GetMapping("/api/short-link/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleShortLink(ShortLinkRecycleBinPageReqDTO ReqDTO) {
        return Results.success(recycleBinService.pageShortLink(ReqDTO));
    }
    /**
     * 恢复短链接
     *
     * @param requestParam 恢复短链接请求参数
     * @return 恢复短链接结果
     */
    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    public Result<Void> recoverShortLink(@RequestBody RecycleBinRecoverReqDTO requestParam) {
        recycleBinService.recoverShortLink(requestParam);
        return Results.success();
    }
    /**
     * 删除短链接
     *
     * @param requestParam 删除短链接请求参数
     * @return 删除短链接结果
     */
    @PostMapping("/api/short-link/v1/recycle-bin/remove")
    public Result<Void> removeShortLink(@RequestBody RecycleBinRemoveReqDTO requestParam) {
        recycleBinService.removeShortLink(requestParam);
        return Results.success();
    }
}
