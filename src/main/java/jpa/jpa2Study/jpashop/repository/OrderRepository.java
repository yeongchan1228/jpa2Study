package jpa.jpa2Study.jpashop.repository;

import jpa.jpa2Study.jpashop.domain.Order;
import jpa.jpa2Study.jpashop.repository.dto.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

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

    public List<Order> findAllWithItem() {

        // distinct를 사용해서 order의 중복을 제거함 -> 4개가 2개 됨.
        String jpql = "select distinct o from Order o join fetch o.member m join fetch o.delivery d join fetch o.orderItems oi "
                + "join fetch oi.item i"; // -> Order는 2개 OrderItem은 4개여서 총 결과가 4개로 뻥튀기 된다. -> 가장 많은 개수를 따라감.

        return em.createQuery(jpql, Order.class).getResultList();
    }



    /**
     * 패치 조인 페이징 하기
     * 1. @ToOne 관계를 전부 패치 조인한다. -> 여기서 Order랑 @ToOne 관계는 Member, Delivery 그래서 findAllWithDeliveryMember 사용
     * 2. 컬렉션은 지연 로딩으로 냅두기
     * 3. batchsize로 지연로딩 최적화 -> hirbernate.default_batch_fetch_size = 100~1000, @Batchsize()로 최적화
     *  -> 정해진 개수만큼 미리 땡겨온다. -> 즉, 프록시 객체를 지정한 사이즈만큼 in 쿼리(하나의 쿼리)로 불러 놓는다. -> N+1에서 조금 해방 -> 사이즈에 따라 1+1로 최적화
     */
    public List<Order> findAllWithDeliveryMember(int offset, int limit) {
        String jpql = "select o from Order o join fetch o.member m join fetch o.delivery d";
        return em.createQuery(jpql, Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    /*public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery("select new jpa.jpa2Study.jpashop.repository.dto.OrderSimpleQueryDto(" +
                        "o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o " +
                        "join o.member m" +
                        " join o.delivery d",
                OrderSimpleQueryDto.class).getResultList();

    }*/
}
