package com.nageoffer.shortlink.shortlinkporject.service;

/**
 * url标题服务接口
 */
public interface UrlTitleService {
    /**
     * 根据url获取标题
     * @param url
     * @return
     */
    String getTitleByUrl(String url);
}
