package cn.tieling.yibao.repository.admin;

import cn.tieling.yibao.entity.admin.DevApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DevApiRepository extends JpaRepository<DevApi, Long> {
    List<DevApi> findByStatusOrderByCategoryAscSortOrderAsc(Integer status);
    List<DevApi> findByCategoryAndStatusOrderBySortOrderAsc(String category, Integer status);
    List<DevApi> findAllByOrderByCategoryAscSortOrderAsc();
}
