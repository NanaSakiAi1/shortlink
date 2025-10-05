package com.nageoffer.shortlink.shortlinkporject.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.shortlinkporject.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.shortlinkporject.dto.req.RecycleBinRecoverReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.req.RecycleBinRemoveReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.req.RecycleBinSaveReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.resp.ShortLinkPageRespDTO;

/**
 * 回收站服务
 */
public interface RecycleBinService extends IService<ShortLinkDO> {
    /**
     * 保存回收站
     * @param requestParam
     */
    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);
    /**
     * 分页查询回收站
     * @param ReqDTO
     * @return
     */
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkRecycleBinPageReqDTO ReqDTO);

    /**
     * 回收站恢复功能
     * @param requestParam
     */
    void recoverShortLink(RecycleBinRecoverReqDTO requestParam);
    /**
     * 移除短连接
     * @param requestParam
     */
    void removeShortLink(RecycleBinRemoveReqDTO requestParam);
}
