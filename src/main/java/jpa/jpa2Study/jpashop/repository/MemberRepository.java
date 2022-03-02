package jpa.jpa2Study.jpashop.repository;

import jpa.jpa2Study.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

/*    @PersistenceContext
    private EntityManager em;*/

    private final EntityManager em;

    /*@PersistenceUnit
    private EntityManagerFactory emf;*/

    public void save(Member member){ // 등록
        em.persist(member);
    }

    public Member findOne(Long id){ // id 검색
        return em.find(Member.class, id);
    }

    public List<Member> findAll(){ // 전부 검색
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name){ // 이름 검색
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }

}
