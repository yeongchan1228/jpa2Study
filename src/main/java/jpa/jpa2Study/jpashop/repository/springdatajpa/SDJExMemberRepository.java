package jpa.jpa2Study.jpashop.repository.springdatajpa;

import jpa.jpa2Study.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * JpaRepository<사용할 객체, Pk 타입>을 넣어 준다.
 */
public interface SDJExMemberRepository extends JpaRepository<Member, Long> {

    // select m from Member m where m.name = ?
    // 자동으로 쿼리 생성
    List<Member> findByName(String name);
}
