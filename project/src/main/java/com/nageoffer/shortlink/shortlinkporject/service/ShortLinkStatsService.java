package com.nageoffer.shortlink.shortlinkporject.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nageoffer.shortlink.shortlinkporject.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.req.ShortLinkGroupStatsReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.req.ShortLinkStatsReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.resp.ShortLinkStatsRespDTO;

/**
 * 短链接监控接口层
 *
 */
public interface ShortLinkStatsService {

    /**
     * 获取单个短链接监控数据
     *
     * @param requestParam 获取短链接监控数据入参
     * @return 短链接监控数据
     */
    ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam);

    /**
     * 获取单个短链接访问记录
     *
     * @param requestParam 获取短链接访问记录入参
     * @return 短链接访问记录
     */
    IPage<ShortLinkStatsAccessRecordRespDTO> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam);
    /**
     * 获取分组短链接监控数据
     *
     * @param requestParam 获取分组短链接监控数据入参
     * @return 分组短链接监控数据
     */
    ShortLinkStatsRespDTO groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 访问分组短链接指定时间内访问记录监控数据
     *
     * @param requestParam 获取分组短链接监控访问记录数据入参
     * @return 分组访问记录监控数据
     */
    IPage<ShortLinkStatsAccessRecordRespDTO> groupShortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordReqDTO requestParam);


}

