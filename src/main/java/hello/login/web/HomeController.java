package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;

   // @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/")
    public String homeLogin(
            // 로그인하지 않은 사용자도 홈에 접근할 수 있음. required = false
            // @CookieValue로 쿠키 조회. memberId 이름의 쿠키가 있어야함.
            @CookieValue(name = "memberId", required = false) Long memberId, Model model) {

        log.info("memberId = {}", memberId);
        if (memberId == null) {
            return "home";

        }

        // 아까는 return home이 안돼서 아래 findById에 Null이 들어감.
        Member loginMember = memberRepository.findById(memberId);
        if (loginMember == null) {
            return "home";
        }

        model.addAttribute("member", loginMember);
        return "loginHome";
    }
}