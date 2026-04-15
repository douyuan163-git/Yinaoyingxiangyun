package cn.tieling.yibao.service.admin;

import cn.tieling.yibao.entity.SysUser;
import cn.tieling.yibao.entity.admin.*;

import java.util.List;
import java.util.Map;

/**
 * 后台管理服务接口
 */
public interface AdminService {

    // ===== 网站配置 =====
    Map<String, String> getSiteConfig(String groupKey);
    void saveSiteConfig(String groupKey, Map<String, String> configs, String operator);
    String getConfigValue(String configKey, String defaultValue);

    // ===== Banner管理 =====
    List<Banner> listBanners(boolean onlyEnabled);
    Banner saveBanner(Banner banner, String operator);
    void deleteBanner(Long id);
    void updateBannerStatus(Long id, Integer status);

    // ===== 首页统计 =====
    List<HomeStat> listHomeStats(boolean onlyEnabled);
    HomeStat saveHomeStat(HomeStat stat);
    void deleteHomeStat(Long id);

    // ===== 服务门户入口 =====
    List<PortalEntry> listPortalEntries(boolean onlyEnabled);
    PortalEntry savePortalEntry(PortalEntry entry);
    void deletePortalEntry(Long id);
    void updatePortalEntryStatus(Long id, Integer status);

    // ===== 开发者API =====
    List<DevApi> listDevApis(boolean onlyEnabled);
    DevApi saveDevApi(DevApi api);
    void deleteDevApi(Long id);

    // ===== 用户管理 =====
    List<SysUser> listUsers(String role, Integer status, String keyword);
    SysUser saveUser(SysUser user, String operator);
    void deleteUser(Long id);
    void updateUserStatus(Long id, Integer status);
    void resetPassword(Long id, String newPassword, String operator);

    // ===== 仪表盘统计 =====
    Map<String, Object> getDashboardStats();
}
