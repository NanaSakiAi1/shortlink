package com.nageoffer.shortlink.admin.controller;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.dto.req.ShortLinkGroupReqDTO;
import com.nageoffer.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.nageoffer.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.nageoffer.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.nageoffer.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分组控制层
 *
 */
@RestController

@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    /**
     * 新增短连接分组
     * @param reqDTO
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/group")
    public Result<Void> saveGroup(@RequestBody ShortLinkGroupReqDTO reqDTO) {
        groupService.saveGroup(reqDTO.getName());
        return Results.success();
    }
    /**
     * 查询短连接分组
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/group")
    public Result<List<ShortLinkGroupRespDTO> >listGroup() {
        return Results.success(groupService.listGroup());
    }
    /**
     * 修改短连接分组
     * @param reqDTO
     * @return
     */
    @PutMapping("/api/short-link/admin/v1/group")
    public Result<Void> updateGroup(@RequestBody ShortLinkGroupUpdateReqDTO reqDTO) {
        groupService.updateGroup(reqDTO.getGid(), reqDTO.getName());
        return Results.success();
    }
    /**
     * 删除短连接分组
     * @param gid
     * @return
     */
    @DeleteMapping("/api/short-link/admin/v1/group")
    public Result<Void> deleteGroup(@RequestParam("gid") String gid) {
        groupService.deleteGroup(gid);
        return Results.success();
    }
    /**
     * 排序短连接分组
     * @param reqDTOs
     * @return
     */
    @PostMapping("/api/short-link/v1/admin/group/sort")
    public Result<Void> sortGroup(@RequestBody List<ShortLinkGroupSortReqDTO> reqDTOs) {
        groupService.sortGroup(reqDTOs);
        return Results.success();
    }
}
