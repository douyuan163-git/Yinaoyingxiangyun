package cn.tieling.yibao.controller;

import cn.tieling.yibao.dto.response.Result;
import cn.tieling.yibao.entity.admin.*;
import cn.tieling.yibao.service.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 前台公共数据接口（无需Token，白名单）
 * 路径前缀：/api/public
 */
@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private AdminService adminService;

    /**
     * GET /api/public/site-config?groupKey=basic
     */
    @GetMapping("/site-config")
    public Result<Map<String, String>> getSiteConfig(@RequestParam(required = false) String groupKey) {
        return Result.success(adminService.getSiteConfig(groupKey));
    }

    /**
     * GET /api/public/banners
     */
    @GetMapping("/banners")
    public Result<List<Banner>> getBanners() {
        return Result.success(adminService.listBanners(true));
    }

    /**
     * GET /api/public/home-stats
     */
    @GetMapping("/home-stats")
    public Result<List<HomeStat>> getHomeStats() {
        return Result.success(adminService.listHomeStats(true));
    }

    /**
     * GET /api/public/portal-entries
     */
    @GetMapping("/portal-entries")
    public Result<List<PortalEntry>> getPortalEntries() {
        return Result.success(adminService.listPortalEntries(true));
    }

    /**
     * GET /api/public/dev-apis?category=auth
     */
    @GetMapping("/dev-apis")
    public Result<List<DevApi>> getDevApis(@RequestParam(required = false) String category) {
        List<DevApi> apis = adminService.listDevApis(true);
        if (category != null && !category.isEmpty()) {
            apis = apis.stream()
                    .filter(a -> category.equals(a.getCategory()))
                    .collect(Collectors.toList());
        }
        return Result.success(apis);
    }
}
