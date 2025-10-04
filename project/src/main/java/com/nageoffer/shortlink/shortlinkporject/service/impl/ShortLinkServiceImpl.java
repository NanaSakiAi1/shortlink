package com.nageoffer.shortlink.shortlinkporject.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.shortlinkporject.common.convention.exception.ServiceException;
import com.nageoffer.shortlink.shortlinkporject.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.shortlinkporject.dao.mapper.ShortLinkMapper;
import com.nageoffer.shortlink.shortlinkporject.dto.req.ShortLinkCreateReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.req.ShortLinkPageReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.req.ShortLinkUpdateReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.resp.ShortLinkPageRespDTO;
import com.nageoffer.shortlink.shortlinkporject.service.ShortLinkService;
import com.nageoffer.shortlink.shortlinkporject.toolkit.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.nageoffer.shortlink.shortlinkporject.common.enums.ValidDateTypeEnum.PERMANENT;

/**
 * 短链接服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {
    private final StringRedisTemplate stringRedisTemplate;
    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;

    /**
     * 创建短链接
     *
     * @return
     */
    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO reqDTO) {

        String shortLinkSUffix = generateSuffix(reqDTO);
        String fullShortUrl = reqDTO.getDomain() + "/" + shortLinkSUffix;

        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(reqDTO.getDomain())
                .originUrl(reqDTO.getOriginUrl())
                .gid(reqDTO.getGid())
                .createdType(reqDTO.getCreatedType())
                .validDateType(reqDTO.getValidDateType())
                .validDate(reqDTO.getValidDate())
                .describe(reqDTO.getDescribe())
                .shortUri(shortLinkSUffix)
                .enableStatus(0)
                .fullShortUrl(fullShortUrl)
                .build();


        try {
            baseMapper.insert(shortLinkDO);
        } catch (DuplicateKeyException e) {
            //TODO 已经误判的短连接如何处理
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class).eq(ShortLinkDO::getFullShortUrl, fullShortUrl);
            ShortLinkDO hasShortLinkDO = baseMapper.selectOne(queryWrapper);
            if (hasShortLinkDO != null) {
                log.warn("短链接{}已入库", fullShortUrl);
                throw new ServiceException("短链接已存在");
            }

        }

        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(shortLinkDO.getFullShortUrl())
                .originUrl(reqDTO.getOriginUrl())
                .gid(reqDTO.getGid())
                .build();
    }

    /**
     * 分页查询短链接
     *
     * @param reqDTO
     * @return
     */
    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO reqDTO) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, reqDTO.getGid())
                .eq(ShortLinkDO::getEnableStatus, 0)
                .eq(ShortLinkDO::getDelFlag, 0)
                .orderByDesc(ShortLinkDO::getCreateTime);
        IPage<ShortLinkDO> resultPage = baseMapper.selectPage(reqDTO, queryWrapper);
        return resultPage.convert(each -> BeanUtil.toBean(each, ShortLinkPageRespDTO.class));
    }

    @Override
    public List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> requestParam) {
        QueryWrapper<ShortLinkDO> queryWrapper = Wrappers.query(new ShortLinkDO())
                .select("gid as gid, count(*) as shortLinkCount")
                .in("gid", requestParam)
                .eq("enable_status", 0)

                .groupBy("gid");
        List<Map<String, Object>> shortLinkDOList = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(shortLinkDOList, ShortLinkGroupCountQueryRespDTO.class);
    }

    /**
     * 更新短链接
     *
     * @param reqDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO reqDTO) {

        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, reqDTO.getGid())
                .eq(ShortLinkDO::getFullShortUrl, reqDTO.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);

        ShortLinkDO hasShortLinkDO = baseMapper.selectOne(queryWrapper);
        if (hasShortLinkDO == null) {
            throw new ServiceException("短链接不存在");
        }
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(hasShortLinkDO.getDomain())
                .shortUri(hasShortLinkDO.getShortUri())
                .favicon(hasShortLinkDO.getFavicon())
                .createdType(hasShortLinkDO.getCreatedType())
                .gid(reqDTO.getGid())
                .originUrl(reqDTO.getOriginUrl())
                .describe(reqDTO.getDescribe())
                .validDateType(reqDTO.getValidDateType())
                .validDate(reqDTO.getValidDate())
                .build();
        if (Objects.equals(hasShortLinkDO.getGid(), reqDTO.getGid())) {
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, reqDTO.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, reqDTO.getGid())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .set(Objects.equals(reqDTO.getValidDateType(), PERMANENT.getType()), ShortLinkDO::getValidDate, null);

            baseMapper.update(shortLinkDO, updateWrapper);
        } else {
            LambdaUpdateWrapper<ShortLinkDO> linkUpdateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, reqDTO.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, hasShortLinkDO.getGid())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            baseMapper.delete(linkUpdateWrapper);
            baseMapper.insert(shortLinkDO);

        }

    return ;
    }

    /**
     * 生成短链接后缀
     *
     * @param reqDTO
     * @return
     */

    private String generateSuffix(ShortLinkCreateReqDTO reqDTO) {
        String originalUrl = reqDTO.getOriginUrl();
        int customeGenerateCount = 0;
        String shortLinkSuffix;
        while (true) {
            if (customeGenerateCount > 10) {
                throw new ServiceException("短链接生成失败");
            }
            originalUrl += System.currentTimeMillis();
            shortLinkSuffix = HashUtil.hashToBase62(originalUrl);
            if (!shortUriCreateCachePenetrationBloomFilter.contains(shortLinkSuffix)) {
                // 加入布隆过滤器
                shortUriCreateCachePenetrationBloomFilter.add(shortLinkSuffix);
                break;
            }

            customeGenerateCount++;
        }
        return shortLinkSuffix;
    }
}
