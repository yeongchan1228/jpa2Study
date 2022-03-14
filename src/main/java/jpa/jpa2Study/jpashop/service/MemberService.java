package jpa.jpa2Study.jpashop.service;

import jpa.jpa2Study.jpashop.domain.Member;
import jpa.jpa2Study.jpashop.repository.MemberRepository;
import jpa.jpa2Study.jpashop.repository.springdatajpa.SDJExMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
//@Transactional // 트랜잭션 안에서 데이터 변경이 일어나야 한다.
@Transactional(readOnly = true) // 2번
public class MemberService {

    private final MemberRepository memberRepository;
    private final SDJExMemberRepository repository;

    //회원 가입
    // 1. @Transactional
    @Transactional // 2번
    public Long join(Member member){

        //중복회원 탐색
        validateDuplicateMember(member);

        memberRepository.save(member);
//        repository.save(member); // 스프링 데이터 JPA 사용
        return member.getId();
    }

    private void validateDuplicateMember(Member member) { // 이 로직은 memberA의 이름을 가진 사람이 동시에 회원가입을 하면 통과된다.
        // -> 멀티 스레드를 고려해서 데이터 베이스의 name을 unique로 잡는게 낫다.

        //Exception
        List<Member> findMembers = memberRepository.findByName(member.getName());
//        Member findMember = repository.findByName(member.getName()).get();
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    // 1. @Transactional(readOnly = true) // 조회하는 곳에선 readOnly를 사용하는 것이 성능 최적화
    public List<Member> findMembers(){
//        repository.findAll();
        return memberRepository.findAll();
    }

    //회원 1명 조회
    // 1. @Transactional(readOnly = true) // 조회하는 곳에선 readOnly를 사용하는 것이 성능 최적화
    public Member findOne(Long id){
//        repository.findById(id);
        return memberRepository.findOne(id);
    }

    @Transactional
    public void update(Long id, String name) {
        Member findMember = memberRepository.findOne(id);
        findMember.setName(name);
    }

}
