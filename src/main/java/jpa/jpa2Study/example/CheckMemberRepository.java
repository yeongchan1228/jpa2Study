package jpa.jpa2Study.example;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class CheckMemberRepository {

    @PersistenceContext // 스프링 부트가 EntityManager 주입, 스프링 부트가 설정 파일을 통해 EntityManager를 생성함.
    private EntityManager em;

    public Long save(CheckMember member){
        em.persist(member);
        return member.getId();
    }

    public CheckMember find(Long id){
        return em.find(CheckMember.class, id);
    }
    

}
