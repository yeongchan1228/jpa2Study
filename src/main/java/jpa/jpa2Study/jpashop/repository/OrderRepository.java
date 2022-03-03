package jpa.jpa2Study.jpashop.repository;

import jpa.jpa2Study.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
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

    /*public List<Order> findAll(String name, String status){
        return em.createQuery("select o from Order o where o.member.name =: name",Order.class)
                .setParameter("name", name)
                .getResultList();
    }*/
}
