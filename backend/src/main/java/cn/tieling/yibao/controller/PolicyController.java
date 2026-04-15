package cn.tieling.yibao.controller;

import cn.tieling.yibao.dto.response.Result;
import cn.tieling.yibao.entity.Policy;
import cn.tieling.yibao.repository.PolicyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * 政策法规接口
 */
@Slf4j
@RestController
@RequestMapping("/policy")
public class PolicyController {

    @Autowired
    private PolicyRepository policyRepository;

    /**
     * 分页查询政策法规列表
     * GET /api/policy/list?level=national&page=0&size=10
     */
    @GetMapping("/list")
    public Result<Page<Policy>> list(
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Policy> result;

        if (keyword != null && !keyword.isEmpty()) {
            result = policyRepository.searchByKeyword(keyword, pageable);
        } else if (level != null && !level.isEmpty()) {
            result = policyRepository.findByLevelAndStatusOrderBySortOrderDescPublishDateDesc(level, 1, pageable);
        } else {
            result = policyRepository.findByStatusOrderBySortOrderDescPublishDateDesc(1, pageable);
        }

        return Result.success(result);
    }

    /**
     * 获取政策法规详情
     * GET /api/policy/{id}
     */
    @GetMapping("/{id}")
    public Result<Policy> detail(@PathVariable Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("政策文件不存在"));
        return Result.success(policy);
    }
}
