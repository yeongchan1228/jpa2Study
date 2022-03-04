package jpa.jpa2Study.jpashop.controller;

import jpa.jpa2Study.jpashop.domain.Address;
import jpa.jpa2Study.jpashop.domain.Member;
import jpa.jpa2Study.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/new")
    public String createForm(Model model){
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/new")
    public String create(@Valid MemberForm memberForm, BindingResult result){ // @Valid -> MemberForm에 @NotNull, @NotEmpty 등을 확인하고 적용
        //@Valid 뒤에 BindingResult가 있으면 BindingResult에 오류가 담긴다.

        if(result.hasErrors()){
            return "members/createMemberForm";
        }

        Member member = new Member();

        member.setName(memberForm.getName());
        member.setAddress(new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode()));

        memberService.join(member);

        return "redirect:/";
    }

    @GetMapping("")
    public String list(Model model){
        List<Member> members = memberService.findMembers(); // API로 만들 때는 절대 Entity를 넘기지 말고 DTO를 만들어 넘겨야 한다.
        model.addAttribute("members", members);

        return "members/memberList";
    }

}
