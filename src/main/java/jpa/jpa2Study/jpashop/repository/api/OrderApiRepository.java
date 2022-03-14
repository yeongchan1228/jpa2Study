package jpa.jpa2Study.jpashop.repository.api;

import jpa.jpa2Study.jpashop.repository.dto.OrderFlatDto;
import jpa.jpa2Study.jpashop.repository.dto.OrderItemQueryDto;
import jpa.jpa2Study.jpashop.repository.dto.OrderQueryDto;
import jpa.jpa2Study.jpashop.repository.dto.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public List<OrderQueryDto> findOrderQueryDtos(){ // items 생성
        List<OrderQueryDto> results = findOrdersWithOrderQueryDto();
        results.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });

        return results;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {

        String jpql = "select new jpa.jpa2Study.jpashop.repository.dto" +
                ".OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count) " +
                "from OrderItem oi join oi.item i " +
                "where oi.order.id  = : orderId";

        return em.createQuery(jpql, OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();

    }

    @Transactional(readOnly = true)
    public List<OrderQueryDto> findOrdersWithOrderQueryDto(){
        String jpql = "select new jpa.jpa2Study.jpashop.repository.dto" +
                ".OrderQueryDto(o.id, o.member.name, o.orderDate, o.status, o.delivery.address) " +
                "from Order o join o.member m join o.delivery d";

        return em.createQuery(jpql, OrderQueryDto.class).getResultList();
    }

    public List<OrderQueryDto> findAllByDto_optimization() {

        List<OrderQueryDto> result = findOrdersWithOrderQueryDto();
        List<Long> orderIds = result.stream().map(o -> o.getOrderId()).collect(Collectors.toList());

        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(orderIds);

        result.stream().forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        String jpql = "select new jpa.jpa2Study.jpashop.repository.dto" +
                ".OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count) " +
                "from OrderItem oi join oi.item i where oi.order.id in :orderIds ";

        List<OrderItemQueryDto> orderItems = em.createQuery(jpql, OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        Map<Long, List<OrderItemQueryDto>> orderItemMap =
                orderItems.stream().collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
        return orderItemMap;
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        String jpql = "select new jpa.jpa2Study.jpashop.repository.dto" +
                ".OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                " from Order o " +
                "join o.member m " +
                "join o.delivery d " +
                "join o.orderItems oi " +
                "join oi.item i";

        return em.createQuery(jpql, OrderFlatDto.class).getResultList();
    }
}
