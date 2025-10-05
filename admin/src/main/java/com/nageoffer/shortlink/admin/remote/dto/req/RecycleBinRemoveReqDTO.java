package com.nageoffer.shortlink.admin.remote.dto.req;

import lombok.Data;

/**
 * 回收站移除
 */
@Data
public class RecycleBinRemoveReqDTO {
    /**
     * 分组id
     */
    private String gid;
    /**
     * 短链接
     */
    private String fullShortUrl;
}
