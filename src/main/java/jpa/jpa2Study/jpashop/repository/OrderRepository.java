package jpa.jpa2Study.jpashop.repository;

import jpa.jpa2Study.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orderSearch){
       /* return em.createQuery("select o from Order o join o.member m " +
                "where o.status =: status and m.name =: name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                //.setFirstResult(100) // 100번 째 부터
                .setMaxResults(1000) // 최대 1000개
                .getResultList();     정적 쿼리*/
        /**
         * 동적 쿼리 -> 최선은 쿼리 dsl을 사용한다.
         */

        //여기서는 language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    public List<Order> findAllWithDeliveryMember() {
        String jpql = "select o from Order o join fetch o.member m join fetch o.delivery d";
        List<Order> resultList = em.createQuery(jpql, Order.class)
                .getResultList();
        return resultList;
    }

    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery("select new jpa.jpa2Study.jpashop.repository.OrderSimpleQueryDto(" +
                        "o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o " +
                        "join o.member m" +
                        " join o.delivery d",
                OrderSimpleQueryDto.class).getResultList();

    }
}
