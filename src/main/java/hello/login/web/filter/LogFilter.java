package hello.login.web.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("log filter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // ServletRequest는 더 범용적이므로, HTTP를 사용할 경우 다운캐스팅 하여 사용
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        // 각 요청을 구분하기 위해 uuid 생성
        String uuid = UUID.randomUUID().toString();

        try{
            log.info("REQUEST [{}] [{}]" , uuid, requestURI);
            // 다음 필터가 있으면 호출하고 없으면 서블릿 호출
            chain.doFilter(request, response);

        } catch (Exception e) {
            throw e;
        }finally {
            log.info("RESPONSE [{}][{}]", uuid, requestURI);
        }
    }

    @Override
    public void destroy() {
        log.info("log filter destroy");
    }
}
