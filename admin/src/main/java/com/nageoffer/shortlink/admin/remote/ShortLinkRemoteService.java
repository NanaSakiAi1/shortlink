package com.nageoffer.shortlink.admin.remote;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.remote.dto.req.*;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短链接服务接口
 */

public interface ShortLinkRemoteService {
    /**
     * 创建短链接
     *
     * @param requestParam 创建短链接请求参数
     * @return 短链接创建响应
     */
    default Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam) {

        String url = "http://127.0.0.1:8001/api/short-link/v1/create";
        HttpResponse resp = HttpRequest.post(url)
                .header("Content-Type", "application/json")
                .body(JSON.toJSONString(requestParam))
                .execute();

        int status = resp.getStatus();
        String body = resp.body();
        System.out.println("[REMOTE][create] url=" + url + ", status=" + status + ", body=" + body);

        if (status < 200 || status >= 300) {
            // 直传远程原文，避免二次解析出错
            Result<ShortLinkCreateRespDTO> r = new Result<>();

            r.setCode("REMOTE_HTTP_" + status);
            r.setMessage(body);
            return r;
        }

        try {
            return JSON.parseObject(body, new TypeReference<Result<ShortLinkCreateRespDTO>>() {});
        } catch (Exception ex) {
            Result<ShortLinkCreateRespDTO> r = new Result<>();
            r.setCode("REMOTE_JSON_PARSE");
            r.setMessage("原文: " + body);
            return r;
        }
    }

    /**
     * 分页查询短链接
     *
     * @param requestParam 分页短链接请求参数
     * @return 查询短链接响应
     */
    default Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gid", requestParam.getGid());
        requestMap.put("current", requestParam.getCurrent());
        requestMap.put("size", requestParam.getSize());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/page", requestMap);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }

    /**
     * 短链接分组组内数量
     *
     * @param requestParam 分组数量请求参数
     * @return 短链接分组组内数量响应
     */
    default Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(List<String> requestParam) {
        String body = HttpRequest.get("http://127.0.0.1:8001/api/short-link/v1/count")
                .form("requestParam", requestParam.toArray(new String[0])) // -> requestParam=a&requestParam=b
                .execute()
                .body();
        return JSON.parseObject(body, new TypeReference<>() {});
    }

    /**
     * 修改短链接
     *
     * @param ReqDTO 修改短链接请求参数
     */
    default void updateShortLink(ShortLinkUpdateReqDTO ReqDTO){
        HttpRequest.post("http://127.0.0.1:8001/api/short-link/v1/update")
                .header("Content-Type", "application/json")
                .body(JSON.toJSONString(ReqDTO))
                .execute();
    }
    /**
     * 根据url获取标题
     * @param url
     * @return
     */
    default Result<String> getTitleByUrl(@RequestParam("url") String url){
        String resultStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/title?url="+url);
        return JSON.parseObject(resultStr, new TypeReference<>() {});
    }
    /**
     * 回收站保存功能
     */
    default void saveRecycleBin(RecycleBinSaveReqDTO ReqDTO){
        HttpRequest.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/save")
                .header("Content-Type", "application/json")
                .body(JSON.toJSONString(ReqDTO))
                .execute();
    }
    /**
     * 分页查询回收站短链接
     *
     * @param requestParam 分页短链接请求参数
     * @return 短链接分页响应
     */
    default Result<IPage<ShortLinkPageRespDTO>> pageRecycleShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gidList", requestParam.getGidList());
        requestMap.put("current", requestParam.getCurrent());
        requestMap.put("size", requestParam.getSize());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/page", requestMap);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }
    /**
     * 回收站恢复功能
     */
    default void recoverShortLink(RecycleBinRecoverReqDTO requestParam) {
        HttpRequest.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/recover")
                .header("Content-Type", "application/json")
                .body(JSON.toJSONString(requestParam))
                .execute();

    }
}
