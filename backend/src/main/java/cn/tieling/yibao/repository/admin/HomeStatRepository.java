package cn.tieling.yibao.repository.admin;

import cn.tieling.yibao.entity.admin.HomeStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeStatRepository extends JpaRepository<HomeStat, Long> {
    List<HomeStat> findByStatusOrderBySortOrderAsc(Integer status);
    List<HomeStat> findAllByOrderBySortOrderAsc();
}
