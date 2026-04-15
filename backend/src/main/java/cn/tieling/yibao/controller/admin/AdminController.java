package cn.tieling.yibao.controller.admin;

import cn.tieling.yibao.dto.response.Result;
import cn.tieling.yibao.entity.SysUser;
import cn.tieling.yibao.entity.admin.*;
import cn.tieling.yibao.service.admin.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 后台管理统一控制器
 * 所有接口需要 admin 角色 Token
 * 路径前缀：/api/admin
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // ==================== 仪表盘 ====================

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard() {
        return Result.success(adminService.getDashboardStats());
    }

    // ==================== 网站配置 ====================

    @GetMapping("/config")
    public Result<Map<String, String>> getConfig(@RequestParam(required = false) String groupKey) {
        return Result.success(adminService.getSiteConfig(groupKey));
    }

    @PostMapping("/config/{groupKey}")
    public Result<String> saveConfig(@PathVariable String groupKey,
                                     @RequestBody Map<String, String> configs,
                                     HttpServletRequest request) {
        String operator = (String) request.getAttribute("username");
        adminService.saveSiteConfig(groupKey, configs, operator);
        return Result.success("配置保存成功");
    }

    // ==================== Banner管理 ====================

    @GetMapping("/banners")
    public Result<List<Banner>> listBanners(@RequestParam(defaultValue = "false") boolean onlyEnabled) {
        return Result.success(adminService.listBanners(onlyEnabled));
    }

    @PostMapping("/banners")
    public Result<Banner> saveBanner(@RequestBody Banner banner, HttpServletRequest request) {
        String operator = (String) request.getAttribute("username");
        return Result.success("保存成功", adminService.saveBanner(banner, operator));
    }

    @DeleteMapping("/banners/{id}")
    public Result<String> deleteBanner(@PathVariable Long id) {
        adminService.deleteBanner(id);
        return Result.success("删除成功");
    }

    @PutMapping("/banners/{id}/status")
    public Result<String> updateBannerStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        adminService.updateBannerStatus(id, body.get("status"));
        return Result.success("状态更新成功");
    }

    // ==================== 首页统计 ====================

    @GetMapping("/home-stats")
    public Result<List<HomeStat>> listHomeStats(@RequestParam(defaultValue = "false") boolean onlyEnabled) {
        return Result.success(adminService.listHomeStats(onlyEnabled));
    }

    @PostMapping("/home-stats")
    public Result<HomeStat> saveHomeStat(@RequestBody HomeStat stat) {
        return Result.success("保存成功", adminService.saveHomeStat(stat));
    }

    @DeleteMapping("/home-stats/{id}")
    public Result<String> deleteHomeStat(@PathVariable Long id) {
        adminService.deleteHomeStat(id);
        return Result.success("删除成功");
    }

    // ==================== 服务门户入口 ====================

    @GetMapping("/portal-entries")
    public Result<List<PortalEntry>> listPortalEntries(@RequestParam(defaultValue = "false") boolean onlyEnabled) {
        return Result.success(adminService.listPortalEntries(onlyEnabled));
    }

    @PostMapping("/portal-entries")
    public Result<PortalEntry> savePortalEntry(@RequestBody PortalEntry entry) {
        return Result.success("保存成功", adminService.savePortalEntry(entry));
    }

    @DeleteMapping("/portal-entries/{id}")
    public Result<String> deletePortalEntry(@PathVariable Long id) {
        adminService.deletePortalEntry(id);
        return Result.success("删除成功");
    }

    @PutMapping("/portal-entries/{id}/status")
    public Result<String> updatePortalEntryStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        adminService.updatePortalEntryStatus(id, body.get("status"));
        return Result.success("状态更新成功");
    }

    // ==================== 开发者API ====================

    @GetMapping("/dev-apis")
    public Result<List<DevApi>> listDevApis(@RequestParam(defaultValue = "false") boolean onlyEnabled) {
        return Result.success(adminService.listDevApis(onlyEnabled));
    }

    @PostMapping("/dev-apis")
    public Result<DevApi> saveDevApi(@RequestBody DevApi api) {
        return Result.success("保存成功", adminService.saveDevApi(api));
    }

    @DeleteMapping("/dev-apis/{id}")
    public Result<String> deleteDevApi(@PathVariable Long id) {
        adminService.deleteDevApi(id);
        return Result.success("删除成功");
    }

    // ==================== 政策法规管理（复用PolicyController逻辑） ====================

    // 政策、新闻、机构、互认的后台CRUD通过各自的Controller处理
    // 本Controller专注于admin专属配置管理

    // ==================== 用户管理 ====================

    @GetMapping("/users")
    public Result<List<SysUser>> listUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        List<SysUser> users = adminService.listUsers(role, status, keyword);
        users.forEach(u -> u.setPassword(null));
        return Result.success(users);
    }

    @PostMapping("/users")
    public Result<SysUser> saveUser(@RequestBody SysUser user, HttpServletRequest request) {
        String operator = (String) request.getAttribute("username");
        SysUser saved = adminService.saveUser(user, operator);
        saved.setPassword(null);
        return Result.success("保存成功", saved);
    }

    @DeleteMapping("/users/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return Result.success("删除成功");
    }

    @PutMapping("/users/{id}/status")
    public Result<String> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        adminService.updateUserStatus(id, body.get("status"));
        return Result.success("状态更新成功");
    }

    @PutMapping("/users/{id}/reset-password")
    public Result<String> resetPassword(@PathVariable Long id,
                                        @RequestBody Map<String, String> body,
                                        HttpServletRequest request) {
        String operator = (String) request.getAttribute("username");
        adminService.resetPassword(id, body.get("password"), operator);
        return Result.success("密码重置成功");
    }
}
