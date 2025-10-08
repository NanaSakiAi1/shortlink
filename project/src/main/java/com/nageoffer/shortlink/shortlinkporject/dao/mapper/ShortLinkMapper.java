package com.nageoffer.shortlink.shortlinkporject.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nageoffer.shortlink.shortlinkporject.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.shortlinkporject.dto.req.ShortLinkPageReqDTO;
import org.apache.ibatis.annotations.Param;

/**
 * 短链接持久层Mapper
 *
 */
public interface ShortLinkMapper extends BaseMapper<ShortLinkDO> {
    /**
     * 短链接访问统计自增
     */
    void incrementStats(@Param("gid") String gid,
                        @Param("fullShortUrl") String fullShortUrl,
                        @Param("totalPv") Integer totalPv,
                        @Param("totalUv") Integer totalUv,
                        @Param("totalUip") Integer totalUip);


    /**
     * 分页统计短链接
     */
    IPage<ShortLinkDO> pageLink(ShortLinkPageReqDTO requestParam);
}
