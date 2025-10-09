package com.nageoffer.shortlink.shortlinkporject.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.protobuf.ServiceException;
import com.nageoffer.shortlink.shortlinkporject.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.shortlinkporject.dto.req.ShortLinkBatchCreateReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.req.ShortLinkCreateReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.req.ShortLinkPageReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.req.ShortLinkUpdateReqDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.resp.ShortLinkBatchCreateRespDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.nageoffer.shortlink.shortlinkporject.dto.resp.ShortLinkPageRespDTO;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.util.List;

/**
 * 短链接服务接口层
 */
public interface ShortLinkService extends IService<ShortLinkDO> {


    /**
     * 创建短链接
     *
     * @param ReqDTO 创建短链接请求参数
     * @return 创建短链接响应参数
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO ReqDTO) throws ServiceException;

    /**
     * 分页查询短链接
     *
     * @param ReqDTO 分页查询短链接请求参数
     * @return 分页查询短链接响应参数
     */
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO ReqDTO);
    /**
     * 短链接分组查询数量
     *
     * @param requestParam 短链接分组查询数量请求参数
     * @return 短链接分组查询数量响应参数
     */

    List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> requestParam);

    /**
     * 修改短链接
     *
     * @param ReqDTO 修改短链接请求参数
     * @return 修改短链接响应参数
     */
    void updateShortLink(ShortLinkUpdateReqDTO ReqDTO);
    /**
     * 短链接跳转
     *
     * @param shortUri 短链接
     * @param request  请求参数
     * @param response 响应参数
     */
    void restoreUrl(String shortUri, ServletRequest request, ServletResponse response);

    /**
     * 批量创建短链接
     *
     * @param requestParam 批量创建短链接请求参数
     * @return 批量创建短链接返回参数
     */
    ShortLinkBatchCreateRespDTO batchCreateShortLink(ShortLinkBatchCreateReqDTO requestParam);
}
