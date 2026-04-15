package cn.tieling.yibao.config;

import cn.tieling.yibao.entity.*;
import cn.tieling.yibao.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 应用启动时初始化演示数据
 */
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private SysUserRepository userRepository;
    @Autowired
    private PolicyRepository policyRepository;
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private InstitutionRepository institutionRepository;
    @Autowired
    private RecognitionRecordRepository recognitionRepository;

    @Override
    public void run(String... args) {
        initUsers();
        initPolicies();
        initNews();
        initInstitutions();
        initRecognitionRecords();
        log.info("演示数据初始化完成");
    }

    private void initUsers() {
        if (userRepository.count() > 0) return;

        // 监管部门账号
        SysUser supervisor = new SysUser();
        supervisor.setUsername("admin");
        supervisor.setPassword("admin123");
        supervisor.setRealName("张管理员");
        supervisor.setPhone("13900000001");
        supervisor.setRole("supervisor");
        userRepository.save(supervisor);

        // 医疗机构账号
        SysUser hospital = new SysUser();
        hospital.setUsername("doctor001");
        hospital.setPassword("doctor123");
        hospital.setRealName("李医生");
        hospital.setPhone("13900000002");
        hospital.setRole("hospital");
        hospital.setOrgId(1L);
        userRepository.save(hospital);

        // 参保人员账号
        SysUser patient = new SysUser();
        patient.setUsername("110211198001010001");
        patient.setPassword("patient123");
        patient.setRealName("王参保");
        patient.setPhone("13900000003");
        patient.setRole("patient");
        patient.setIdCard("110211198001010001");
        patient.setInsuranceNo("210700000000001");
        userRepository.save(patient);

        // 开发者账号
        SysUser developer = new SysUser();
        developer.setUsername("dev001");
        developer.setPassword("dev123");
        developer.setRealName("赵开发");
        developer.setPhone("13900000004");
        developer.setRole("developer");
        userRepository.save(developer);

        log.info("用户数据初始化完成，共4个账号");
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
