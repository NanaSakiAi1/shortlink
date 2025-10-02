package com.nageoffer.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.admin.dao.entity.GroupDO;
import com.nageoffer.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.nageoffer.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

/**
 * 短连接分组服务接口层
 */
public interface GroupService extends IService<GroupDO> {
    /**
     * 新增短连接分组
     * @param GroupName 短连接分组名
     */
    void saveGroup(String GroupName);
    /**
     * 查询短连接分组
     * @return
     */
    List<ShortLinkGroupRespDTO> listGroup();
    /**
     * 修改短连接分组
     * @param gid 分组ID
     * @param name 分组名
     */
    void updateGroup(String gid, String name);
    /**
     * 删除短连接分组
     * @param gid 分组ID
     */
    void deleteGroup(String gid);
    /**
     * 排序短连接分组排序
     * @param reqDTOs
     */
    void sortGroup(List<ShortLinkGroupSortReqDTO> reqDTOs);
}
