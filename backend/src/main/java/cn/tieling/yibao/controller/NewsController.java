package cn.tieling.yibao.controller;

import cn.tieling.yibao.dto.response.Result;
import cn.tieling.yibao.entity.News;
import cn.tieling.yibao.repository.NewsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 新闻动态接口
 */
@Slf4j
@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsRepository newsRepository;

    /**
     * 分页查询新闻列表
     * GET /api/news/list?category=notice&page=0&size=10
     */
    @GetMapping("/list")
    public Result<Page<News>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<News> result;

        if (keyword != null && !keyword.isEmpty()) {
            result = newsRepository.searchByKeyword(keyword, pageable);
        } else if (category != null && !category.isEmpty()) {
            result = newsRepository.findByCategoryAndStatusOrderByIsTopDescPublishDateDesc(category, 1, pageable);
        } else {
            result = newsRepository.findByStatusOrderByIsTopDescSortOrderDescPublishDateDesc(1, pageable);
        }

        return Result.success(result);
    }

    /**
     * 获取最新5条新闻（首页用）
     * GET /api/news/latest
     */
    @GetMapping("/latest")
    public Result<List<News>> latest() {
        List<News> list = newsRepository.findTop5ByStatusOrderByIsTopDescPublishDateDesc(1);
        return Result.success(list);
    }

    /**
     * 获取新闻详情
     * GET /api/news/{id}
     */
    @GetMapping("/{id}")
    public Result<News> detail(@PathVariable Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("新闻不存在"));
        // 浏览量+1
        newsRepository.incrementViewCount(id);
        return Result.success(news);
    }
}
