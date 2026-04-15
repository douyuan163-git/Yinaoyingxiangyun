package cn.tieling.yibao.repository;

import cn.tieling.yibao.entity.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    Page<Policy> findByStatusOrderBySortOrderDescPublishDateDesc(Integer status, Pageable pageable);

    Page<Policy> findByLevelAndStatusOrderBySortOrderDescPublishDateDesc(String level, Integer status, Pageable pageable);

    @Query("SELECT p FROM Policy p WHERE p.status = 1 AND " +
           "(p.title LIKE %:keyword% OR p.issuer LIKE %:keyword% OR p.docNo LIKE %:keyword%) " +
           "ORDER BY p.sortOrder DESC, p.publishDate DESC")
    Page<Policy> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
