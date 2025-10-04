package com.nageoffer.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.admin.common.biz.user.UserContext;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.dao.entity.GroupDO;
import com.nageoffer.shortlink.admin.dao.mapper.GroupMapper;
import com.nageoffer.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.nageoffer.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.nageoffer.shortlink.admin.remote.ShortLinkRemoteService;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.nageoffer.shortlink.admin.service.GroupService;
import com.nageoffer.shortlink.admin.tollkit.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短连接分组接口实现层
 */
@Service
@Slf4j
public class GrouptServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    /**
     * 创建短连接分组
     *
     * @param GroupName
     */
    @Override
    public void saveGroup(String GroupName) {
        String gid;
        do {
            gid = RandomGenerator.generateRandom();
        } while (!hasGid(gid));
        log.info("UserContext.username={}", UserContext.getUsername());
        GroupDO groupDO = GroupDO.builder()
                .name(GroupName)
                .username(UserContext.getUsername())
                .gid(RandomGenerator.generateRandom())
                .sortOrder(0)
                .build();
        baseMapper.insert(groupDO);
    }

    /**
     * 查询短连接分组
     *
     * @return
     */
    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {

        // 1) 先查分组列表
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);

        // 2) 远程查询每个 gid 的短链数量
        //    注意：即使没有分组也要安全处理，防止 NPE
        List<String> gidList = groupDOList.stream().map(GroupDO::getGid).toList();
        Map<String, Integer> countMap = new HashMap<>(gidList.size());
        try {
            if (!gidList.isEmpty()) {
                Result<List<ShortLinkGroupCountQueryRespDTO>> listResult =
                        shortLinkRemoteService.listGroupShortLinkCount(gidList);
                if (listResult != null && listResult.getData() != null) {
                    listResult.getData().forEach(item -> {
                        if (item != null) {
                            countMap.put(item.getGid(), item.getShortLinkCount());
                        }
                    });
                }
            }
        } catch (Exception ex) {
            // 远程失败不影响分组列表展示，数量置 0
            // 也可以按需记录日志 log.warn("query short link count error", ex);
        }

        // 3) DO -> DTO，并合并数量（没有返回的默认 0）
        List<ShortLinkGroupRespDTO> respList = BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);
        respList.forEach(each ->
                each.setShortLinkCount(countMap.getOrDefault(each.getGid(), 0))
        );

        // 4) 返回
        return respList;
    }

    @Override
    public void updateGroup(String gid, String name) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setName(name);
        baseMapper.update(groupDO, updateWrapper);
    }
    /**
     * 删除短连接分组
     *
     * @param gid
     */
    @Override
    public void deleteGroup(String gid) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        baseMapper.update(groupDO, updateWrapper);
    }

    /**
     * 短链接分组排序
     *
     * @param reqDTOs
     */
    @Override
    public void sortGroup(List<ShortLinkGroupSortReqDTO> reqDTOs) {
        reqDTOs.forEach(reqDTO -> {
            GroupDO groupDO = GroupDO.builder()
                    .gid(reqDTO.getGid()).
                    sortOrder(reqDTO.getSortOrder())
                    .build();
            LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                    .eq(GroupDO::getGid, reqDTO.getGid())
                    .eq(GroupDO::getUsername, UserContext.getUsername())
                    .eq(GroupDO::getDelFlag, 0);
            baseMapper.update(groupDO, updateWrapper);
        });
    }

    /**
     * 判断GID是否已存在
     *
     * @param gid
     * @return
     */
    private boolean hasGid(String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                //TODO 设置用户名
                .eq(GroupDO::getUsername, UserContext.getUsername());
        GroupDO hasGroupFlag = baseMapper.selectOne(queryWrapper);
        return hasGroupFlag == null;
    }
}
