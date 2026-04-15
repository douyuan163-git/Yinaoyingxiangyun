package cn.tieling.yibao.repository;

import cn.tieling.yibao.entity.RecognitionRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecognitionRecordRepository extends JpaRepository<RecognitionRecord, Long> {

    Page<RecognitionRecord> findByIdCardOrderByRecognitionDateDesc(String idCard, Pageable pageable);

    @Query("SELECT COUNT(r) FROM RecognitionRecord r WHERE r.status = 'recognized'")
    long countRecognized();

    @Query("SELECT COUNT(DISTINCT r.idCard) FROM RecognitionRecord r")
    long countPatients();
}
