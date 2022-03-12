package jpa.jpa2Study.jpashop.repository.api;

import jpa.jpa2Study.jpashop.repository.dto.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderApiRepository {

    private final EntityManager em;

    @Transactional(readOnly = true) // 읽기는 생략 가능하다.
    public List<OrderSimpleQueryDto> findOrders(){
        String jpql = "select new jpa.jpa2Study.jpashop.repository.dto.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +
                "from Order o " +
                "join o.member m " +
                "join o.delivery d";
        return em.createQuery(jpql, OrderSimpleQueryDto.class).getResultList();
    }
}
