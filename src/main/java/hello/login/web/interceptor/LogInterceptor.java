package hello.login.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    public static final String LOG_ID = "logId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String uuid = UUID.randomUUID().toString();
        request.setAttribute(LOG_ID, uuid); // preHandle의 값을 post와 completion에서도 쓰기 위해 어딘가에 담아두어야 한다.

        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler; // @Controller, @RequestMapping을 활용한 핸들러 매핑의 경우 핸들러 정보로 HandlerMethod가 넘어온다.
        }

        log.info("REQUEST  [{}][{}][{}]", uuid, requestURI, handler);
        return true; // 다음 인터셉터나 컨트롤러가 호출된다.
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle [{}]", modelAndView);
    }

    // 종료로그는 Completion에서 호출했다. 예외가 발생한 경우 post가 호출되지 않기 때문.
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String logId = (String)request.getAttribute(LOG_ID);
        log.info("RESPONSE [{}][{}]", logId, requestURI);
        if (ex != null) {
            log.error("afterCompletion error!!", ex);
        }
    }
}
