package cn.tieling.yibao.repository;

import cn.tieling.yibao.entity.Institution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {

    List<Institution> findByStatusOrderByNameAsc(String status);

    Page<Institution> findByDistrictOrderByNameAsc(String district, Pageable pageable);

    @Query("SELECT i FROM Institution i WHERE " +
           "(i.name LIKE %:keyword% OR i.district LIKE %:keyword%) " +
           "ORDER BY i.name ASC")
    Page<Institution> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    long countByStatus(String status);

    @Query("SELECT i FROM Institution i WHERE i.supportRecognition = 1 ORDER BY i.name ASC")
    List<Institution> findSupportingRecognition();
}
