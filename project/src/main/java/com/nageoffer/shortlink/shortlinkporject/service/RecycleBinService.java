package com.nageoffer.shortlink.shortlinkporject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.shortlinkporject.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.shortlinkporject.dto.req.RecycleBinSaveReqDTO;

/**
 * 回收站服务
 */
public interface RecycleBinService extends IService<ShortLinkDO> {
    /**
     * 保存回收站
     * @param requestParam
     */
    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);
}
