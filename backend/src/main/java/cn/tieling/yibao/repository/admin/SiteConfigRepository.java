package cn.tieling.yibao.repository.admin;

import cn.tieling.yibao.entity.admin.SiteConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteConfigRepository extends JpaRepository<SiteConfig, Long> {
    Optional<SiteConfig> findByConfigKey(String configKey);
    List<SiteConfig> findByGroupKeyOrderByConfigKey(String groupKey);
}
