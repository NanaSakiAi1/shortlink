package com.nageoffer.shortlink.shortlinkporject.dto.req;

import lombok.Data;

/**
 * 回收站保存功能
 */
@Data
public class RecycleBinRecoverReqDTO {
    /**
     * 分组id
     */
    private String gid;
    /**
     * 短链接
     */
    private String fullShortUrl;
}
