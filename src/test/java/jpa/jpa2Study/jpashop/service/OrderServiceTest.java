package jpa.jpa2Study.jpashop.service;

import jpa.jpa2Study.jpashop.domain.Address;
import jpa.jpa2Study.jpashop.domain.Member;
import jpa.jpa2Study.jpashop.domain.Order;
import jpa.jpa2Study.jpashop.domain.OrderStatus;
import jpa.jpa2Study.jpashop.domain.item.Book;
import jpa.jpa2Study.jpashop.domain.item.Item;
import jpa.jpa2Study.jpashop.exception.NotEnoughStockException;
import jpa.jpa2Study.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception{

        //give
        Member member = createMember("회원");

        Book book = createBook("시골 JPA", 10000, 10);

        //when
        int bookCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), bookCount);

        //then
        Order findOrder = orderRepository.findOne(orderId);

        assertThat(findOrder.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(findOrder.getId()).isEqualTo(orderId);
        assertThat(findOrder.getTotalPrice()).isEqualTo(10000 * bookCount);
        assertThat(book.getStockQuantity()).isEqualTo(8);

    }

    private Book createBook(String name, int price, int count) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(count);
        em.persist(book);
        return book;
    }

    private Member createMember(String name) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address("서울", "강가", "123123"));
        em.persist(member);
        return member;
    }

    @Test()
    public void 주문수량_재고초과() throws Exception{
        //given
        Member member = createMember("회원");

        Item item = createBook("시골 JPA", 10000, 10);
        int orderCount = 11;

        //when

        //then
        assertThrows(NotEnoughStockException.class, () ->  orderService.order(member.getId(), item.getId(), orderCount));
    }

    @Test
    public void 주문취소() throws Exception{
        //give
        Member member = createMember("회원");

        Item item = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(item.getStockQuantity()).isEqualTo(10);
    }

}