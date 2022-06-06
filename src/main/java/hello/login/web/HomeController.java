package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.argumentresolver.Login;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;

    // @GetMapping("/")
    public String home() {
        return "home";
    }

    // v1
    //@GetMapping("/")
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

    // v2
    // @GetMapping("/")
    public String homeLoginV2(HttpServletRequest request, Model model) {

        // 세션 관리자에 저장된 회원 정보 조회
        Member member = (Member) sessionManager.getSession(request);
        if (member == null) {
            return "home";
        }

        // 로그인
        model.addAttribute("member", member);
        return "loginHome";
    }

    // v3
    // @GetMapping("/")
    public String homeLoginV3(HttpServletRequest request, Model model) {

        // 세션이 없으면 home
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "home";
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        // 세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "home";
        }

        // 세션이 유지되면 로그인으로 이동. JSESSIONID 쿠키가 적절하게 생성되는것을 확인
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    // v4
    //@GetMapping("/")
    public String homeLoginV3Spring(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false)
                    Member loginMember, Model model
    ) {
        // 세션에 회원 데이터가 없으면 home으로 이동
        if (loginMember == null) {
            return "home";
        }

        // 세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    //v5 . ArgumentResolver로 로그인한 회원 찾기 -> 여기에 @Login 어노테이션 사용했다. 로그인 어노테이션이 있을 경우 우리가 만든 리졸버가 동작하도록
    // 인터셉터, 인증은 이미 로그인체크에서 처리
    // 여기서는 로그인 정보를 받아오는 역할만 한다.
    @GetMapping("/")
    public String homeLoginV3ArgumentResolver(@Login Member loginMember, Model model) {

        if (loginMember == null) {
            return "home";
        }

        model.addAttribute("member", loginMember);
        return "loginHome";
    }
}