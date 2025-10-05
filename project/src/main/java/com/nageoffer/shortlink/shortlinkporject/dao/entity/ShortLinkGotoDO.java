package com.nageoffer.shortlink.shortlinkporject.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链接跳转实体
 */
@Data
@TableName("t_link_goto")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkGotoDO {
    private Integer id;
    private String gid;
    private String fullShortUrl;
}
