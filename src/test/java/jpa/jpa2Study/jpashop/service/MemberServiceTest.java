package jpa.jpa2Study.jpashop.service;

import jpa.jpa2Study.jpashop.domain.Member;
import jpa.jpa2Study.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // 테스트 종료 후 자동 롤백
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    public void 회원가입() throws Exception{
        //give
        Member member = new Member();
        member.setName("kim");

        //when
        Long saveId = memberService.join(member);

        //then
        em.flush(); // Test의 @Trasactional은 기본적으로 롤백하기 때문에 insert문을 하고 싶으면 flush를 사용해서 영속성 컨텍스트를 비워준다.
        assertThat(member.getId()).isEqualTo(saveId);

    }
    
    @Test
    public void 중복_회원_예외() throws Exception{
        //give
        Member memberA = new Member();
        memberA.setName("kim");

        Member memberB = new Member();
        memberB.setName("kim");
        //when
        memberService.join(memberA);

        //then
        assertThrows(IllegalStateException.class, () -> memberService.join(memberB));
    }
    
}