package cn.tieling.yibao.controller;

import cn.tieling.yibao.dto.response.Result;
import cn.tieling.yibao.entity.Institution;
import cn.tieling.yibao.repository.InstitutionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 机构接入接口
 */
@Slf4j
@RestController
@RequestMapping("/institution")
public class InstitutionController {

    @Autowired
    private InstitutionRepository institutionRepository;

    /**
     * 查询接入机构列表
     * GET /api/institution/list?keyword=铁岭&page=0&size=10
     */
    @GetMapping("/list")
    public Result<Page<Institution>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String district,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Institution> result;

        if (keyword != null && !keyword.isEmpty()) {
            result = institutionRepository.searchByKeyword(keyword, pageable);
        } else if (district != null && !district.isEmpty()) {
            result = institutionRepository.findByDistrictOrderByNameAsc(district, pageable);
        } else {
            result = institutionRepository.findAll(pageable);
        }

        return Result.success(result);
    }

    /**
     * 获取所有运行中的机构（不分页，用于地图展示）
     * GET /api/institution/all
     */
    @GetMapping("/all")
    public Result<List<Institution>> all() {
        List<Institution> list = institutionRepository.findByStatusOrderByNameAsc("running");
        return Result.success(list);
    }

    /**
     * 获取支持互认的机构列表
     * GET /api/institution/recognition
     */
    @GetMapping("/recognition")
    public Result<List<Institution>> recognitionList() {
        List<Institution> list = institutionRepository.findSupportingRecognition();
        return Result.success(list);
    }

    /**
     * 获取机构详情
     * GET /api/institution/{id}
     */
    @GetMapping("/{id}")
    public Result<Institution> detail(@PathVariable Long id) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("机构不存在"));
        return Result.success(institution);
    }

    /**
     * 获取平台统计数据（首页用）
     * GET /api/institution/stats
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalInstitutions", institutionRepository.count());
        stats.put("runningInstitutions", institutionRepository.countByStatus("running"));
        stats.put("recognitionInstitutions", institutionRepository.findSupportingRecognition().size());
        return Result.success(stats);
    }
}
