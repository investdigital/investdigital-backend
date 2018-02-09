package com.oxchains.message.dao;

import com.oxchains.message.domain.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by huohuo on 2017/10/23.
 * @author huohuo
 */
@Repository
public interface OrderRepo extends CrudRepository<Orders,String> {
    @Query(value = " select  s from Orders as s where  (s.buyerId = :id or s.sellerId = :ids ) and s.orderStatus in ( :status) ")
    Page<Orders> findOrdersByBuyerIdOrSellerIdAndOrderStatus(@Param("id") Long id, @Param("ids") Long ids, @Param("status") List<Long> status, Pageable pageable);
    @Query(value = " select  s from Orders as s where  (s.buyerId = :id or s.sellerId = :ids ) and s.orderStatus not in ( :status)")
    Page<Orders> findOrdersByBuyerIdOrSellerIdAndOrderStatusIsNotIn(@Param("id") Long id, @Param("ids") Long ids, @Param("status") List<Long> status, Pageable pageable);
    List<Orders> findOrdersByOrderStatus(Long status);
    List<Orders> findByArbitrate(Integer status);

    List<Orders> findByBuyerIdOrSellerId(Long buyId, Long sellerId);
    int countByBuyerIdOrSellerId(Long buyId, Long sellerId);

    Orders findById(String orderId);
}
