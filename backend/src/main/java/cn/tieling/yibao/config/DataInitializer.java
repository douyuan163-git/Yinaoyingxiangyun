package cn.tieling.yibao.config;

import cn.tieling.yibao.entity.*;
import cn.tieling.yibao.entity.admin.*;
import cn.tieling.yibao.repository.*;
import cn.tieling.yibao.repository.admin.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;

/**
 * 应用启动时初始化演示数据
 */
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private SysUserRepository userRepository;
    @Autowired private PolicyRepository policyRepository;
    @Autowired private NewsRepository newsRepository;
    @Autowired private InstitutionRepository institutionRepository;
    @Autowired private RecognitionRecordRepository recognitionRepository;
    @Autowired private SiteConfigRepository siteConfigRepository;
    @Autowired private BannerRepository bannerRepository;
    @Autowired private HomeStatRepository homeStatRepository;
    @Autowired private PortalEntryRepository portalEntryRepository;
    @Autowired private DevApiRepository devApiRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) {
        initUsers();
        initSiteConfig();
        initBanners();
        initHomeStats();
        initPortalEntries();
        initDevApis();
        initPolicies();
        initNews();
        initInstitutions();
        initRecognitionRecords();
        log.info("演示数据初始化完成");
    }

    private void initUsers() {
        if (userRepository.count() > 0) return;

        // 系统管理员（后台CMS）
        SysUser adminUser = new SysUser();
        adminUser.setUsername("sysadmin");
        adminUser.setPassword(passwordEncoder.encode("Admin@2026"));
        adminUser.setRealName("系统管理员");
        adminUser.setPhone("13900000000");
        adminUser.setRole("admin");
        adminUser.setRemark("后台CMS管理员账号");
        userRepository.save(adminUser);

        // 监管部门账号
        SysUser supervisor = new SysUser();
        supervisor.setUsername("admin");
        supervisor.setPassword(passwordEncoder.encode("admin123"));
        supervisor.setRealName("张管理员");
        supervisor.setPhone("13900000001");
        supervisor.setRole("supervisor");
        userRepository.save(supervisor);

        // 医疗机构账号
        SysUser hospital = new SysUser();
        hospital.setUsername("doctor001");
        hospital.setPassword(passwordEncoder.encode("doctor123"));
        hospital.setRealName("李医生");
        hospital.setPhone("13900000002");
        hospital.setRole("hospital");
        hospital.setOrgId(1L);
        userRepository.save(hospital);

        // 参保人员账号
        SysUser patient = new SysUser();
        patient.setUsername("110211198001010001");
        patient.setPassword(passwordEncoder.encode("patient123"));
        patient.setRealName("王参保");
        patient.setPhone("13900000003");
        patient.setRole("patient");
        patient.setIdCard("110211198001010001");
        patient.setInsuranceNo("210700000000001");
        userRepository.save(patient);

        // 开发者账号
        SysUser developer = new SysUser();
        developer.setUsername("dev001");
        developer.setPassword(passwordEncoder.encode("dev123"));
        developer.setRealName("赵开发");
        developer.setPhone("13900000004");
        developer.setRole("developer");
        userRepository.save(developer);

        log.info("用户数据初始化完成，共5个账号（含管理员）");
    }

    private void initSiteConfig() {
        if (siteConfigRepository.count() > 0) return;

        Object[][] configs = {
            // 基本信息
            {"basic", "site.title", "铁岭市医保影像云统一服务门户", "网站标题"},
            {"basic", "site.subtitle", "Tieling Healthcare Security Imaging Portal", "网站副标题"},
            {"basic", "site.icp", "辽ICP备XXXXXXXX号", "ICP备案号"},
            {"basic", "site.copyright", "© 2025 铁岭市医疗保障局 版权所有", "版权信息"},
            {"basic", "site.contact.phone", "024-72000000", "联系电话"},
            {"basic", "site.contact.email", "yibao@tieling.gov.cn", "联系邮箱"},
            {"basic", "site.contact.address", "辽宁省铁岭市银州区广裕街1号", "联系地址"},
            {"basic", "site.host", "铁岭市医疗保障局", "主办单位"},
            {"basic", "site.tech_support", "融御科技（辽宁）有限公司", "技术支持单位"},
            // 首页配置
            {"home", "home.hero.badge", "辽宁省医保影像云试点示范单位", "首页标识徽章"},
            {"home", "home.hero.title", "铁岭市\n医保影像云\n统一服务门户", "首页主标题"},
            {"home", "home.hero.description", "为医疗机构与参保人员提供云端影像存储、院间互联共享、AI智能辅助诊断及医保快速结算的综合数字平台。", "首页描述"},
            {"home", "home.hero.btn_primary", "进入服务门户", "首页主按钮文字"},
            {"home", "home.hero.btn_secondary", "机构接入申请", "首页次按钮文字"},
            // SSO配置
            {"sso", "sso.supervisor.name", "医保监管系统", "监管部门系统名称"},
            {"sso", "sso.supervisor.url", "http://localhost:8081", "监管部门系统URL"},
            {"sso", "sso.hospital.name", "云胶片医生工作站", "医疗机构系统名称"},
            {"sso", "sso.hospital.url", "http://localhost:8082", "医疗机构系统URL"},
            {"sso", "sso.patient.name", "参保人员查询端", "参保人员系统名称"},
            {"sso", "sso.patient.url", "http://localhost:8083", "参保人员系统URL"},
            {"sso", "sso.developer.name", "开发者控制台", "开发者系统名称"},
            {"sso", "sso.developer.url", "http://localhost:8084", "开发者系统URL"},
        };

        for (Object[] cfg : configs) {
            SiteConfig sc = new SiteConfig();
            sc.setGroupKey((String) cfg[0]);
            sc.setConfigKey((String) cfg[1]);
            sc.setConfigValue((String) cfg[2]);
            sc.setDescription((String) cfg[3]);
            sc.setUpdateBy("system");
            siteConfigRepository.save(sc);
        }
        log.info("网站配置初始化完成");
    }

    private void initBanners() {
        if (bannerRepository.count() > 0) return;

        Banner b1 = new Banner();
        b1.setTitle("铁岭市医保影像云平台正式上线");
        b1.setSubtitle("全市7家医疗机构同步接入，云胶片服务全面开通");
        b1.setImageUrl("");
        b1.setLinkUrl("/news");
        b1.setSortOrder(1);
        b1.setStatus(1);
        b1.setCreateBy("system");
        bannerRepository.save(b1);

        Banner b2 = new Banner();
        b2.setTitle("2025年度检查互认工作全面推进");
        b2.setSubtitle("CT、MRI等主要影像检查结果实现跨院互认");
        b2.setImageUrl("");
        b2.setLinkUrl("/mutual-recognition");
        b2.setSortOrder(2);
        b2.setStatus(1);
        b2.setCreateBy("system");
        bannerRepository.save(b2);

        log.info("Banner初始化完成");
    }

    private void initHomeStats() {
        if (homeStatRepository.count() > 0) return;

        Object[][] stats = {
            {"已接入医疗机构", 7L, "家", "text-blue-600", 1},
            {"影像存储总量", 12L, "万张", "text-cyan-600", 2},
            {"检查互认次数", 3856L, "次", "text-emerald-600", 3},
            {"参保人员覆盖", 280L, "万人", "text-indigo-600", 4},
        };

        for (Object[] s : stats) {
            HomeStat hs = new HomeStat();
            hs.setLabel((String) s[0]);
            hs.setValue((Long) s[1]);
            hs.setSuffix((String) s[2]);
            hs.setColor((String) s[3]);
            hs.setSortOrder((Integer) s[4]);
            hs.setStatus(1);
            homeStatRepository.save(hs);
        }
        log.info("首页统计数据初始化完成");
    }

    private void initPortalEntries() {
        if (portalEntryRepository.count() > 0) return;

        Object[][] entries = {
            // roleGroup, name, description, icon, linkUrl, isPrimary, sortOrder
            {"supervisor", "医保监管系统", "影像质控、费用稽核、数据分析", "Shield", "/portal/supervisor", true, 1},
            {"supervisor", "影像质控管理", "影像质量评分与问题追踪", "CheckCircle", "#", false, 2},
            {"supervisor", "费用稽核分析", "重复检查识别与费用分析", "BarChart2", "#", false, 3},
            {"supervisor", "数据统计报表", "平台运营数据统计与导出", "FileText", "#", false, 4},
            {"hospital", "云胶片工作站", "影像上传、报告管理、互认申请", "Hospital", "/portal/hospital", true, 1},
            {"hospital", "影像上传管理", "DICOM影像批量上传与管理", "Upload", "#", false, 2},
            {"hospital", "报告在线管理", "诊断报告查看与电子签名", "FileCheck", "#", false, 3},
            {"hospital", "互认申请处理", "检查互认申请的提交与审核", "RefreshCw", "#", false, 4},
            {"patient", "云胶片查询", "查询个人历次影像及报告", "Search", "/portal/patient", true, 1},
            {"patient", "影像在线查看", "浏览个人DICOM影像", "Eye", "#", false, 2},
            {"patient", "报告下载打印", "下载诊断报告与影像", "Download", "#", false, 3},
            {"patient", "互认记录查询", "查询检查互认历史记录", "History", "#", false, 4},
            {"developer", "开发者控制台", "API接入、密钥管理、调试工具", "Code2", "/portal/developer", true, 1},
            {"developer", "API密钥管理", "创建和管理API访问密钥", "Key", "#", false, 2},
            {"developer", "接口调试工具", "在线测试API接口", "Terminal", "#", false, 3},
            {"developer", "SDK下载中心", "下载各语言SDK和示例代码", "Package", "#", false, 4},
        };

        for (Object[] e : entries) {
            PortalEntry pe = new PortalEntry();
            pe.setRoleGroup((String) e[0]);
            pe.setName((String) e[1]);
            pe.setDescription((String) e[2]);
            pe.setIcon((String) e[3]);
            pe.setLinkUrl((String) e[4]);
            pe.setIsPrimary((Boolean) e[5]);
            pe.setSortOrder((Integer) e[6]);
            pe.setStatus(1);
            portalEntryRepository.save(pe);
        }
        log.info("服务门户入口初始化完成");
    }

    private void initDevApis() {
        if (devApiRepository.count() > 0) return;

        Object[][] apis = {
            {"统一登录接口", "auth", "POST", "/api/auth/login", "SSO统一登录，返回JWT Token", false, 1},
            {"Token刷新接口", "auth", "POST", "/api/auth/refresh", "使用RefreshToken换取新AccessToken", false, 2},
            {"SSO跳转Token", "auth", "GET", "/api/auth/sso-token", "获取子系统单点登录跳转Token", true, 3},
            {"Token验证接口", "auth", "GET", "/api/auth/validate", "验证Token有效性（供子系统调用）", true, 4},
            {"影像列表查询", "imaging", "GET", "/api/imaging/list", "查询患者影像列表（需Token）", true, 1},
            {"影像详情获取", "imaging", "GET", "/api/imaging/{id}", "获取单条影像详情及DICOM URL", true, 2},
            {"影像上传接口", "imaging", "POST", "/api/imaging/upload", "上传DICOM影像文件", true, 3},
            {"互认记录查询", "recognition", "GET", "/api/recognition/list", "查询检查互认记录列表", true, 1},
            {"互认申请提交", "recognition", "POST", "/api/recognition/apply", "提交检查互认申请", true, 2},
            {"机构列表查询", "institution", "GET", "/api/institutions", "查询已接入医疗机构列表（公开）", false, 1},
            {"机构接入申请", "institution", "POST", "/api/institutions/apply", "提交机构接入申请", false, 2},
        };

        for (Object[] a : apis) {
            DevApi da = new DevApi();
            da.setName((String) a[0]);
            da.setCategory((String) a[1]);
            da.setMethod((String) a[2]);
            da.setPath((String) a[3]);
            da.setDescription((String) a[4]);
            da.setNeedAuth((Boolean) a[5]);
            da.setSortOrder((Integer) a[6]);
            da.setStatus(1);
            devApiRepository.save(da);
        }
        log.info("开发者API文档初始化完成");
    }

    private void initPolicies() {
        if (policyRepository.count() > 0) return;

        String[][] data = {
            {"国家医疗保障局关于推进医保影像云建设的指导意见", "国家医疗保障局", "医保发〔2024〕18号", "national",
             "推进全国医保影像云统一建设，实现影像数据互联互通，降低参保人员重复检查负担。"},
            {"国家卫生健康委关于推进医疗机构检查检验结果互认工作的通知", "国家卫生健康委员会", "国卫医函〔2022〕53号", "national",
             "要求各级医疗机构积极推进检查检验结果互认，减少重复检查，节约医疗资源。"},
            {"辽宁省医疗保障局关于开展医保影像云试点工作的通知", "辽宁省医疗保障局", "辽医保发〔2025〕12号", "provincial",
             "在铁岭等地市开展医保影像云试点，探索云胶片替代传统胶片的实施路径。"},
            {"辽宁省卫生健康委员会关于医学影像检查结果互认工作实施方案", "辽宁省卫生健康委员会", "辽卫发〔2024〕88号", "provincial",
             "明确辽宁省医学影像检查结果互认的范围、标准和实施步骤。"},
            {"铁岭市医疗保障局关于铁岭市医保影像云平台建设实施方案", "铁岭市医疗保障局", "铁医保〔2025〕5号", "municipal",
             "铁岭市医保影像云平台建设总体方案，明确建设目标、任务分工和时间节点。"},
            {"铁岭市卫生健康委员会关于推进医保影像云接入工作的通知", "铁岭市卫生健康委员会", "铁卫健发〔2025〕8号", "municipal",
             "要求全市二级及以上医疗机构于2025年底前完成医保影像云接入工作。"},
        };

        for (int i = 0; i < data.length; i++) {
            Policy p = new Policy();
            p.setTitle(data[i][0]);
            p.setIssuer(data[i][1]);
            p.setDocNo(data[i][2]);
            p.setLevel(data[i][3]);
            p.setSummary(data[i][4]);
            p.setContent("<p>" + data[i][4] + "</p><p>（详细内容请下载附件查阅）</p>");
            p.setPublishDate(LocalDate.now().minusDays(i * 30L));
            p.setSortOrder(data.length - i);
            policyRepository.save(p);
        }
        log.info("政策数据初始化完成");
    }

    private void initNews() {
        if (newsRepository.count() > 0) return;

        String[][] data = {
            {"铁岭市医保影像云平台正式上线运行", "notice", "铁岭市医保影像云统一服务门户平台已正式上线，全市7家医疗机构同步接入，参保人员可通过本平台查询个人影像资料。", "1"},
            {"关于云胶片服务开通使用的公告", "notice", "自2025年10月1日起，铁岭市接入医疗机构将全面推行云胶片服务，不再提供传统胶片打印。", "0"},
            {"铁岭市医保局召开影像云推进工作会议", "dynamic", "铁岭市医疗保障局组织召开全市医保影像云推进工作会议，部署下一阶段重点工作任务。", "0"},
            {"辽宁省医保影像云试点工作现场会在铁岭召开", "dynamic", "辽宁省医疗保障局在铁岭市召开医保影像云试点工作现场会，铁岭市的建设经验获省局充分肯定。", "1"},
            {"解读：国家医保影像云建设标准规范要点", "policy", "本文对国家医保影像云建设相关标准规范进行解读，重点介绍影像存储、传输和互认的技术要求。", "0"},
            {"2025年全国医保影像云建设进展综述", "industry", "截至2025年底，全国已有超过200个地市启动医保影像云建设，覆盖医疗机构超过5000家。", "0"},
        };

        for (int i = 0; i < data.length; i++) {
            News n = new News();
            n.setTitle(data[i][0]);
            n.setCategory(data[i][1]);
            n.setExcerpt(data[i][2]);
            n.setContent("<p>" + data[i][2] + "</p>");
            n.setAuthor("铁岭市医保局");
            n.setPublishDate(LocalDate.now().minusDays(i * 7L));
            n.setIsTop(Integer.parseInt(data[i][3]));
            n.setSortOrder(data.length - i);
            newsRepository.save(n);
        }
        log.info("新闻数据初始化完成");
    }

    private void initInstitutions() {
        if (institutionRepository.count() > 0) return;

        String[][] data = {
            {"铁岭市中心医院", "三级甲等", "综合医院", "银州区", "铁岭市银州区广裕街1号", "024-72222222", "CT,MRI,DR,US"},
            {"铁岭市第二人民医院", "三级乙等", "综合医院", "银州区", "铁岭市银州区凡河新区", "024-72333333", "CT,DR,US"},
            {"铁岭县中心医院", "二级甲等", "综合医院", "铁岭县", "铁岭县银州镇", "024-73111111", "CT,DR"},
            {"开原市中心医院", "二级甲等", "综合医院", "开原市", "开原市中央路1号", "024-76111111", "CT,MRI,DR"},
            {"昌图县中心医院", "二级甲等", "综合医院", "昌图县", "昌图县昌图镇", "024-77111111", "CT,DR"},
            {"西丰县人民医院", "二级乙等", "综合医院", "西丰县", "西丰县西丰镇", "024-78111111", "DR,US"},
            {"调兵山市人民医院", "二级甲等", "综合医院", "调兵山市", "调兵山市铁法路", "024-74111111", "CT,DR,US"},
        };

        for (int i = 0; i < data.length; i++) {
            Institution inst = new Institution();
            inst.setName(data[i][0]);
            inst.setLevel(data[i][1]);
            inst.setType(data[i][2]);
            inst.setDistrict(data[i][3]);
            inst.setAddress(data[i][4]);
            inst.setPhone(data[i][5]);
            inst.setJoinDate(LocalDate.now().minusDays(i * 15L));
            inst.setStatus("running");
            inst.setRecognitionItems(data[i][6]);
            inst.setSupportRecognition(1);
            institutionRepository.save(inst);
        }
        log.info("机构数据初始化完成，共{}家", data.length);
    }

    private void initRecognitionRecords() {
        if (recognitionRepository.count() > 0) return;

        RecognitionRecord r1 = new RecognitionRecord();
        r1.setPatientName("王参保");
        r1.setIdCard("110211198001010001");
        r1.setSourceInstitutionId(1L);
        r1.setSourceInstitutionName("铁岭市中心医院");
        r1.setTargetInstitutionId(2L);
        r1.setTargetInstitutionName("铁岭市第二人民医院");
        r1.setExamType("CT");
        r1.setExamDate(LocalDate.now().minusDays(30));
        r1.setRecognitionDate(LocalDate.now().minusDays(25));
        r1.setStatus("recognized");
        recognitionRepository.save(r1);

        log.info("互认记录数据初始化完成");
    }
}
