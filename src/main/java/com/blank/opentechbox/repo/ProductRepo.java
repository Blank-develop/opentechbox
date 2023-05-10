package com.blank.opentechbox.repo;

import com.blank.opentechbox.entity.Account;
import com.blank.opentechbox.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product,Long> {
    List<Product> findByAccount(Account account);
    List<Product> findByAccountAndId(Account account,Long id);
    String deleteByAccountAndId(Account account, Long id);

    List<Product> findByOrderByPriceAsc();

    List<Product> findByOrderByPriceDesc();
    List<Product> findAllByOrderByProductDateDesc();

}
