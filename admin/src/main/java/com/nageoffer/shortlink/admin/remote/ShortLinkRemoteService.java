package com.nageoffer.shortlink.admin.remote;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nageoffer.shortlink.admin.common.biz.user.UserContext;
import com.nageoffer.shortlink.admin.common.biz.user.UserInfoDTO;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.remote.dto.req.*;
import com.nageoffer.shortlink.admin.remote.dto.resp.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短链接服务接口
 */

public interface ShortLinkRemoteService {

    // 从 ThreadLocal 取当前登录用户（你已有 UserContext/UserInfoDTO）
    private Map<String, String> authHeaders() {
        Map<String, String> h = new HashMap<>();
        UserInfoDTO u = UserContext.getUser();
        if (u != null) {
            h.put("username", u.getUsername());
            h.put("token", u.getToken());
        }
        System.out.println("[AUTH HEADERS] " + h); // ✅ 调试
        return h;
    }
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
            return JSON.parseObject(body, new TypeReference<Result<ShortLinkCreateRespDTO>>() {
            });
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
        requestMap.put("orderTag", requestParam.getOrderTag());
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
        return JSON.parseObject(body, new TypeReference<>() {
        });
    }

    /**
     * 修改短链接
     *
     * @param ReqDTO 修改短链接请求参数
     */
    default void updateShortLink(ShortLinkUpdateReqDTO ReqDTO) {
        HttpRequest.post("http://127.0.0.1:8001/api/short-link/v1/update")
                .header("Content-Type", "application/json")
                .body(JSON.toJSONString(ReqDTO))
                .execute();
    }

    /**
     * 根据url获取标题
     *
     * @param url
     * @return
     */
    default Result<String> getTitleByUrl(@RequestParam("url") String url) {
        String resultStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/title?url=" + url);
        return JSON.parseObject(resultStr, new TypeReference<>() {
        });
    }

    /**
     * 回收站保存功能
     */
    default void saveRecycleBin(RecycleBinSaveReqDTO ReqDTO) {
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

    /**
     * 删除短链接
     */
    default void removeShortLink(RecycleBinRemoveReqDTO requestParam) {
        HttpRequest.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/remove")
                .header("Content-Type", "application/json")
                .body(JSON.toJSONString(requestParam))
                .execute();
    }

    default Result<ShortLinkStatsRespDTO> oneShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        System.out.println("[REMOTE][stats] DTO=" + JSON.toJSONString(requestParam)); // 打印看看gid有没有值
        if (requestParam == null
                || requestParam.getGid() == null
                || requestParam.getGid().isEmpty()) {
            throw new IllegalArgumentException("gid 不能为空：请先选中分组或传入正确的 gid");
        }

        String url = "http://127.0.0.1:8001/api/short-link/v1/stats";
        Map<String, Object> params = new HashMap<>();
        params.put("fullShortUrl", requestParam.getFullShortUrl());
        params.put("gid", requestParam.getGid());
        params.put("enableStatus", requestParam.getEnableStatus());
        params.put("startDate", requestParam.getStartDate());
        params.put("endDate", requestParam.getEndDate());

        HttpResponse resp = HttpRequest.get(url)
                .addHeaders(authHeaders())     // 继续带 username、token
                .form(params)                  // 统一用 map 传参，避免某些场景为空
                .execute();

        String body = resp.body();
        System.out.println("[REMOTE][stats] url=" + url
                + ", status=" + resp.getStatus()
                + ", body=" + body);

        return JSON.parseObject(body, new TypeReference<>() {});
    }
    /**
     * 单个短链接监控访问记录
     *
     * @param requestParam 短链接监控访问记录请求参数
     * @return 短链接监控访问记录响应
     */
    default Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam){
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(requestParam, false, true);
        stringObjectMap.remove("orders");
        stringObjectMap.remove("records");
        String resultStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats/access-record", stringObjectMap);
        return JSON.parseObject(resultStr, new TypeReference<>() {
        });
    }

    /**
     * 访问分组短链接指定时间内监控数据
     *
     * @param requestParam 访分组问短链接监控请求参数
     * @return 分组短链接监控信息
     */
    default Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam) {
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats/group", BeanUtil.beanToMap(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }

}

