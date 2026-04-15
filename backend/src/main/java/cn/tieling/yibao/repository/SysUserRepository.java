package cn.tieling.yibao.repository;

import cn.tieling.yibao.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long> {
    Optional<SysUser> findByUsername(String username);
    Optional<SysUser> findByPhone(String phone);
    Optional<SysUser> findByIdCard(String idCard);
    Optional<SysUser> findByInsuranceNo(String insuranceNo);
    boolean existsByUsername(String username);
}
