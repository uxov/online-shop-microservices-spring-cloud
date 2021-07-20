package xyz.defe.sp.common.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.defe.sp.common.entity.general.ErrorLog;

@Repository
public interface ErrorLogDao extends JpaRepository<ErrorLog, Long> {

}
