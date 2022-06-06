package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;

    // 로그인 페이지를 띄운다
    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

    // v1 로그인 정보를 입력받는다
    // @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form,
                        BindingResult bindingResult,
                        HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        // 서비스는 컨트롤러에서 사용.
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("login? {}", loginMember);

        // service에서 비밀번호 불일치시 null 리턴하기로 함
        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다");
            return "login/loginForm";
        }

        // 로그인 성공처리
        log.info("login success {}", loginMember);

        // 쿠키 생성
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        response.addCookie(idCookie);

        return "redirect:/";
    }

    // v2
    // @PostMapping("/login")
    public String loginV2(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
        if(bindingResult.hasErrors()){
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("login? {}", loginMember);

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다");
            return "login/loginForm";
        }
        // 로그인 성공처리. 세션생성 및 쿠키추가 (sessionManager 내에서 처리)
        sessionManager.createSession(loginMember, response);
        return "redirect:/";
    }

    // v3
    //@PostMapping("/login")
    public String loginV3(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("login? {}", loginMember);

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        //로그인 성공처리
        // 세션이 있으면 세션 반환, 없으면 신규 세션 생성
        HttpSession session = request.getSession();

        // 세션에 로그인 회원정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
        return "redirect:/";
    }

    //v4
    @PostMapping("/login")
    public String loginV4(
            @Valid @ModelAttribute LoginForm form, BindingResult bindingResult,
            @RequestParam(defaultValue = "/") String redirectURL,
            HttpServletRequest request
    ){
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("login?{}", loginMember);

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return "redirect:" + redirectURL;
    }

    // v1 logout
    //@PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        expireCookie(response, "memberId");
        return "redirect:/";
    }

    // v2 logout
    // @PostMapping("/logout")
    public String logoutV2(HttpServletRequest request) {
        sessionManager.expire(request);
        return "redirect:/";
    }

    // v3 logout
    @PostMapping("/logout")
    public String logooutV3(HttpServletRequest request) {
        // 세션을 삭제한다. false옵션 사용시 세션이 있으면 기존 세션 반환, 없으면 세션 생성하지 않고 null 반환
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // 세션 제거
        }
        return "redirect:/";

    }

    private void expireCookie(HttpServletResponse response, String cookieName) {

        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0); // 세션쿠키이므로 웹브라우저 종료시 세션만료. maxage 0으로 세팅
        response.addCookie(cookie); // 로그아웃 후 세션을 만료시키니, home으로 리다이렉트시 memberId null 되어 로그아웃 상태
    }
}
