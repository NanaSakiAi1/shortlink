package com.nageoffer.shortlink.shortlinkporject.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.protobuf.ServiceException;
import com.nageoffer.shortlink.shortlinkporject.common.convention.result.Result;
import com.nageoffer.shortlink.shortlinkporject.common.convention.result.Results;
import com.nageoffer.shortlink.shortlinkporject.dto.req.ShortLinkCreateReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.req.ShortLinkPageReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.req.ShortLinkUpdateReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.resp.ShortLinkPageRespDTO;
import com.nageoffer.shortlink.shortlinkporject.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShortLinkController {
    private final ShortLinkService shortLinkService;
    /**
     * 创建短链接
     * @return
     */
    @PostMapping("/api/short-link/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO reqDTO) throws ServiceException {
        return Results.success(shortLinkService.createShortLink(reqDTO));
    }
    /**
     * 分页查询短链接
     * @return
     */
    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO reqDTO) {
        return Results.success(shortLinkService.pageShortLink(reqDTO));
    }
    /**
     * 短链接分组组内数量
     * @return
     */
    @GetMapping("/api/short-link/v1/count")
    public Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(@RequestParam List<String> requestParam){
        return Results.success(shortLinkService.listGroupShortLinkCount(requestParam));
    }

    @PostMapping("/api/short-link/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO reqDTO)  {
        shortLinkService.updateShortLink(reqDTO);
        return Results.success();
    }
}
