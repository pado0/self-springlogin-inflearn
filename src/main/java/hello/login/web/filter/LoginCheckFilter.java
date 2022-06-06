package hello.login.web.filter;

import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class LoginCheckFilter implements Filter {

    private static final String[] whiteList = {"/", "/members/add", "/login", "/logout", "/css/*"};

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            log.info("인증체크 필터 시작 {}", requestURI);

            if (isLoginCheckPath(requestURI)){
                HttpSession session = httpRequest.getSession(false);

                // 미인증 사용자일 경우
                if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
                    log.info("미인증 사용자 요청{}", requestURI);

                    // 미인증 사용자는 로그인 페이지로 리다이렉트. 리다이렉트 된 로그인 페이지에서 로그인 이후 다시 상품 관리 화면으로 돌아가게 한다.
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI);
                    return; // 미인증 사용자는 다음으로 진행하지 않고 끝냄
                }
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            throw e;
        }finally {
            log.info("인증체크 필터 종료", requestURI);
        }

    }

    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whiteList, requestURI);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
