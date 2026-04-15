package cn.tieling.yibao.service.admin.impl;

import cn.tieling.yibao.entity.SysUser;
import cn.tieling.yibao.entity.admin.*;
import cn.tieling.yibao.repository.*;
import cn.tieling.yibao.repository.admin.*;
import cn.tieling.yibao.service.admin.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private SiteConfigRepository siteConfigRepository;

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private HomeStatRepository homeStatRepository;

    @Autowired
    private PortalEntryRepository portalEntryRepository;

    @Autowired
    private DevApiRepository devApiRepository;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private RecognitionRecordRepository recognitionRecordRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ===== 网站配置 =====

    @Override
    public Map<String, String> getSiteConfig(String groupKey) {
        List<SiteConfig> configs;
        if (StringUtils.hasText(groupKey)) {
            configs = siteConfigRepository.findByGroupKeyOrderByConfigKey(groupKey);
        } else {
            configs = siteConfigRepository.findAll();
        }
        Map<String, String> result = new LinkedHashMap<>();
        configs.forEach(c -> result.put(c.getConfigKey(), c.getConfigValue()));
        return result;
    }

    @Override
    @Transactional
    public void saveSiteConfig(String groupKey, Map<String, String> configs, String operator) {
        configs.forEach((key, value) -> {
            SiteConfig config = siteConfigRepository.findByConfigKey(key)
                    .orElse(new SiteConfig());
            config.setGroupKey(groupKey);
            config.setConfigKey(key);
            config.setConfigValue(value);
            config.setUpdateBy(operator);
            siteConfigRepository.save(config);
        });
    }

    @Override
    public String getConfigValue(String configKey, String defaultValue) {
        return siteConfigRepository.findByConfigKey(configKey)
                .map(SiteConfig::getConfigValue)
                .orElse(defaultValue);
    }

    // ===== Banner管理 =====

    @Override
    public List<Banner> listBanners(boolean onlyEnabled) {
        if (onlyEnabled) {
            return bannerRepository.findByStatusOrderBySortOrderAsc(1);
        }
        return bannerRepository.findAllByOrderBySortOrderAsc();
    }

    @Override
    @Transactional
    public Banner saveBanner(Banner banner, String operator) {
        if (banner.getId() == null) {
            banner.setCreateBy(operator);
        }
        return bannerRepository.save(banner);
    }

    @Override
    @Transactional
    public void deleteBanner(Long id) {
        bannerRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateBannerStatus(Long id, Integer status) {
        bannerRepository.findById(id).ifPresent(b -> {
            b.setStatus(status);
            bannerRepository.save(b);
        });
    }

    // ===== 首页统计 =====

    @Override
    public List<HomeStat> listHomeStats(boolean onlyEnabled) {
        if (onlyEnabled) {
            return homeStatRepository.findByStatusOrderBySortOrderAsc(1);
        }
        return homeStatRepository.findAllByOrderBySortOrderAsc();
    }

    @Override
    @Transactional
    public HomeStat saveHomeStat(HomeStat stat) {
        return homeStatRepository.save(stat);
    }

    @Override
    @Transactional
    public void deleteHomeStat(Long id) {
        homeStatRepository.deleteById(id);
    }

    // ===== 服务门户入口 =====

    @Override
    public List<PortalEntry> listPortalEntries(boolean onlyEnabled) {
        if (onlyEnabled) {
            return portalEntryRepository.findByStatusOrderByRoleGroupAscSortOrderAsc(1);
        }
        return portalEntryRepository.findAllByOrderByRoleGroupAscSortOrderAsc();
    }

    @Override
    @Transactional
    public PortalEntry savePortalEntry(PortalEntry entry) {
        return portalEntryRepository.save(entry);
    }

    @Override
    @Transactional
    public void deletePortalEntry(Long id) {
        portalEntryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updatePortalEntryStatus(Long id, Integer status) {
        portalEntryRepository.findById(id).ifPresent(e -> {
            e.setStatus(status);
            portalEntryRepository.save(e);
        });
    }

    // ===== 开发者API =====

    @Override
    public List<DevApi> listDevApis(boolean onlyEnabled) {
        if (onlyEnabled) {
            return devApiRepository.findByStatusOrderByCategoryAscSortOrderAsc(1);
        }
        return devApiRepository.findAllByOrderByCategoryAscSortOrderAsc();
    }

    @Override
    @Transactional
    public DevApi saveDevApi(DevApi api) {
        return devApiRepository.save(api);
    }

    @Override
    @Transactional
    public void deleteDevApi(Long id) {
        devApiRepository.deleteById(id);
    }

    // ===== 用户管理 =====

    @Override
    public List<SysUser> listUsers(String role, Integer status, String keyword) {
        List<SysUser> all = sysUserRepository.findAll();
        return all.stream()
                .filter(u -> !StringUtils.hasText(role) || role.equals(u.getRole()))
                .filter(u -> status == null || status.equals(u.getStatus()))
                .filter(u -> !StringUtils.hasText(keyword) ||
                        (u.getUsername() != null && u.getUsername().contains(keyword)) ||
                        (u.getRealName() != null && u.getRealName().contains(keyword)) ||
                        (u.getPhone() != null && u.getPhone().contains(keyword)))
                .sorted(Comparator.comparing(SysUser::getId).reversed())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SysUser saveUser(SysUser user, String operator) {
        if (user.getId() == null) {
            // 新建用户，加密密码
            if (StringUtils.hasText(user.getPassword())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        } else {
            // 更新用户，如果密码字段有值则重新加密
            SysUser existing = sysUserRepository.findById(user.getId()).orElse(null);
            if (existing != null && StringUtils.hasText(user.getPassword())
                    && !user.getPassword().startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else if (existing != null) {
                user.setPassword(existing.getPassword());
            }
        }
        return sysUserRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        sysUserRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long id, Integer status) {
        sysUserRepository.findById(id).ifPresent(u -> {
            u.setStatus(status);
            sysUserRepository.save(u);
        });
    }

    @Override
    @Transactional
    public void resetPassword(Long id, String newPassword, String operator) {
        sysUserRepository.findById(id).ifPresent(u -> {
            u.setPassword(passwordEncoder.encode(newPassword));
            sysUserRepository.save(u);
            log.info("管理员 {} 重置了用户 {} 的密码", operator, u.getUsername());
        });
    }

    // ===== 仪表盘统计 =====

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalUsers", sysUserRepository.count());
        stats.put("totalPolicies", policyRepository.count());
        stats.put("totalNews", newsRepository.count());
        stats.put("totalInstitutions", institutionRepository.count());
        stats.put("totalRecognitions", recognitionRecordRepository.count());
        stats.put("totalBanners", bannerRepository.count());
        // 按角色统计用户数
        Map<String, Long> userByRole = new LinkedHashMap<>();
        List<SysUser> allUsers = sysUserRepository.findAll();
        allUsers.stream()
                .collect(Collectors.groupingBy(SysUser::getRole, Collectors.counting()))
                .forEach(userByRole::put);
        stats.put("userByRole", userByRole);
        return stats;
    }
}
