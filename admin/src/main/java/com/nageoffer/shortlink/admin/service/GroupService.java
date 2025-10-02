package com.nageoffer.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.admin.dao.entity.GroupDO;
/**
 * 短连接分组服务接口层
 */
public interface GroupService extends IService<GroupDO> {
    /**
     * 新增短连接分组
     * @param GroupName 短连接分组名
     */
    void saveGroup(String GroupName);
}
