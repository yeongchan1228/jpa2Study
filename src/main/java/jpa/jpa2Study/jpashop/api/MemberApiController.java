package jpa.jpa2Study.jpashop.api;

import jpa.jpa2Study.jpashop.domain.Address;
import jpa.jpa2Study.jpashop.domain.Member;
import jpa.jpa2Study.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//@Controller
//@ResponseBody
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 1. 회원 등록 API
     */

    /**
     * v1 : api용 전용 클래스 없이 Entity와 1:1로 매핑한다
     * 엔티티는 어디서나 쓰이는 것으로 엔티티를 활용해서 api 통신을 할 때
     * api 통신을 위한 엔티티 부분의 수정이 어플 전체에 영향을 줄 수 있기 때문에,
     * 한 엔티티의 모든 api 통신을 위한 제약 조건을 담기 힘들기 때문에
     * api에서 전용 요청 클래스(Dto)를 만들어서 사용한다.
     */
    @PostMapping("/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        //@RequestBody -> JSON(설정에서 변경 가능)으로 온 Body를 전부 옆의 객체에 매핑해서 넣어준다.
        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    /**
     * 다른 요청 클래스(Dto)로 값을 받지 않고 Entity로 요청 값을 받으면 요청으로 어떤 값들이 넘어오는지 파악하기 힘들다.
     */
    @PostMapping("/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    /**
     * 2. 회원 수정 API
     */

    /**
     *  무조건 전체 덮어쓰기 patch, post > put
     */
    @PutMapping("/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request
    ){
        memberService.update(id, request.getName());

        Member findMember = memberService.findOne(id);

        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    /**
     *  부분 수정 적용
     */
    @PatchMapping("/v3/members/{id}")
    public UpdateMemberResponse updateMemberV3(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request
    ){
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);

        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }


    /**
     * 3. 회원 조회
     */

    @GetMapping("/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();
    }

    /*
    @GetMapping("/v2/members")
    public List<ResponesMemberFind> membersV2(){
        List<Member> members = memberService.findMembers();

        List<ResponesMemberFind> result = new ArrayList<>();

        for (Member member : members) {
            ResponesMemberFind memberFind = new ResponesMemberFind(member.getName(), member.getAddress());
            result.add(memberFind);
        }
        return result;
    }
    */

    @GetMapping("/v2/members")
    public Result<MemberDto> membersV2(){
        List<Member> findMembers = memberService.findMembers();

        List<MemberDto> collect = findMembers.stream().map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);
    }

    @Data
    static class CreateMemberRequest{

        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{

        private Long id;
        private String name;
    }

    @Data
    static class UpdateMemberRequest{

        @NotEmpty
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class ResponesMemberFind{

        private String name;
        private Address address;
    }

    @Data
    @AllArgsConstructor
    static class Result<T>{ // JSON 형태를 감싸주기 위해 { data : [ { ~~~ } ] }
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto{

        private String name;
    }
}
