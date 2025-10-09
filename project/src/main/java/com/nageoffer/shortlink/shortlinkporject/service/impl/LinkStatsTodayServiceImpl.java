package com.nageoffer.shortlink.shortlinkporject.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.shortlinkporject.dao.entity.LinkStatsTodayDO;
import com.nageoffer.shortlink.shortlinkporject.dao.mapper.LinkStatsTodayMapper;
import com.nageoffer.shortlink.shortlinkporject.service.LinkStatsTodayService;
import org.springframework.stereotype.Service;

/**
 * 短链接今日统计接口实现层
 *
 */
@Service
public class LinkStatsTodayServiceImpl extends ServiceImpl<LinkStatsTodayMapper, LinkStatsTodayDO> implements LinkStatsTodayService {

}
