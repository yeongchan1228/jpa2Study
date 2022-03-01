package jpa.jpa2Study.jpashop;

import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;


    @Test
    @Transactional // 쿼리문은 항상 트랜잭션에서 이루어져야 하므로 필요하다
    // @Transactional이 Test에 있으면 모든 실행 후 롤백한다.
    //@Rollback(false) //-> @Transactional이 있어도 Test가 끝난 후 롤백을 하지 않는다.
    public void testMember() throws Exception{
        //give
        Member memberA = new Member();
        memberA.setUsername("memberA");

        //when
        Long saveId = memberRepository.save(memberA);
        Member findMember = memberRepository.find(saveId);

        //then
        assertThat(memberA.getId()).isEqualTo(findMember.getId());
        assertThat(findMember).isEqualTo(memberA); // 같은 영속성 컨텍스트에 있기 때문에 true
        System.out.println("findMember == memberA" +(findMember == memberA));
    }

}