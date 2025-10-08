package com.nageoffer.shortlink.shortlinkporject.dto.resp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 短链接分页响应对象
 */
@Data
public class ShortLinkPageRespDTO{
    /**
     * ID
     */
    private Long id;

    /**
     * 域名
     */
    private String domain;

    /**
     * 短连接
     */
    private String shortUri;

    /**
     * 完整短连接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 点击量
     */
    private Integer clickNum;

    /**
     * 分组标识
     */
    private String gid;

    private String favicon;


    private Integer totalPv;
    private Integer todayPv;

    private Integer totalUv;
    private Integer todayUv;

    private Integer totalUip;
    private Integer todayUip;

    /**
     * 创建类型0:接口创建，1:控制台创建
     */
    private Integer createdType;

    /**
     * 有效期类型
     */
    private Integer validDateType;

    /**
     * 有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validDate;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    /**
     * 描述
     */
    @TableField("`describe`")
    private String describe;

}
