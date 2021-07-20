package xyz.defe.sp.payment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.defe.sp.common.entity.spPayment.Wallet;

@Repository
public interface WalletDao extends JpaRepository<Wallet, String> {
    Wallet findByUserId(String uid);
}
