package cn.tieling.yibao.repository;

import cn.tieling.yibao.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    Page<News> findByStatusOrderByIsTopDescSortOrderDescPublishDateDesc(Integer status, Pageable pageable);

    Page<News> findByCategoryAndStatusOrderByIsTopDescPublishDateDesc(String category, Integer status, Pageable pageable);

    List<News> findTop5ByStatusOrderByIsTopDescPublishDateDesc(Integer status);

    @Query("SELECT n FROM News n WHERE n.status = 1 AND n.title LIKE %:keyword% ORDER BY n.publishDate DESC")
    Page<News> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE News n SET n.viewCount = n.viewCount + 1 WHERE n.id = :id")
    void incrementViewCount(@Param("id") Long id);
}
