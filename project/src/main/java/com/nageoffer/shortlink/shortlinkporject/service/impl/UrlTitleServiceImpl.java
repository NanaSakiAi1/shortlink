package com.nageoffer.shortlink.shortlinkporject.service.impl;

import com.nageoffer.shortlink.shortlinkporject.service.UrlTitleService;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

/**
 * url标题服务实现类
 */
@Service
public class UrlTitleServiceImpl implements UrlTitleService {
    /**
     * 根据url获取标题
     * @param url
     * @return
     */
    @SneakyThrows
    @Override
    public String getTitleByUrl(String url) {
        Document document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (ShortLinkBot/1.0)")
                .timeout(5000)
                .get();
        return document.title();
    }
}
