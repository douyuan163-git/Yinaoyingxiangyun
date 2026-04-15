package cn.tieling.yibao.repository.admin;

import cn.tieling.yibao.entity.admin.PortalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortalEntryRepository extends JpaRepository<PortalEntry, Long> {
    List<PortalEntry> findByStatusOrderByRoleGroupAscSortOrderAsc(Integer status);
    List<PortalEntry> findByRoleGroupAndStatusOrderBySortOrderAsc(String roleGroup, Integer status);
    List<PortalEntry> findAllByOrderByRoleGroupAscSortOrderAsc();
}
