package cn.tieling.yibao.controller;

import cn.tieling.yibao.dto.response.Result;
import cn.tieling.yibao.entity.RecognitionRecord;
import cn.tieling.yibao.repository.RecognitionRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 检查互认接口
 */
@Slf4j
@RestController
@RequestMapping("/recognition")
public class RecognitionController {

    @Autowired
    private RecognitionRecordRepository recognitionRepository;

    /**
     * 根据身份证号查询互认记录
     * GET /api/recognition/query?idCard=xxxxxx&page=0&size=10
     */
    @GetMapping("/query")
    public Result<Page<RecognitionRecord>> query(
            @RequestParam String idCard,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (idCard == null || idCard.length() < 15) {
            return Result.error(400, "请输入有效的身份证号");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<RecognitionRecord> records = recognitionRepository.findByIdCardOrderByRecognitionDateDesc(idCard, pageable);
        return Result.success(records);
    }

    /**
     * 获取互认统计数据
     * GET /api/recognition/stats
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecognized", recognitionRepository.countRecognized());
        stats.put("totalPatients", recognitionRepository.countPatients());
        return Result.success(stats);
    }
}
