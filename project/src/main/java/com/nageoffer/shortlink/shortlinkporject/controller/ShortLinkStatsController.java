package com.nageoffer.shortlink.shortlinkporject.controller;

import com.nageoffer.shortlink.shortlinkporject.common.convention.result.Result;
import com.nageoffer.shortlink.shortlinkporject.common.convention.result.Results;
import com.nageoffer.shortlink.shortlinkporject.dto.req.ShortLinkStatsReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.resp.ShortLinkStatsRespDTO;
import com.nageoffer.shortlink.shortlinkporject.service.ShortLinkStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ShortLinkStatsController {
    private final ShortLinkStatsService shortLinkStatsService;
    /**
     * 访问单个短链接指定时间内监控数据
     */
    @GetMapping("/api/short-link/v1/stats")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO requestParam) {
        return Results.success(shortLinkStatsService.oneShortLinkStats(requestParam));
    }

}
