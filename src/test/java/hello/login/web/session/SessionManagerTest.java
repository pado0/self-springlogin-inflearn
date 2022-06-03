package hello.login.web.session;

import hello.login.domain.member.Member;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
class SessionManagerTest {

    SessionManager sessionManager = new SessionManager();

    @Test
    void sessionTest(){

        // 가짜 세션 생성
        MockHttpServletResponse response = new MockHttpServletResponse();
        Member member = new Member();

        // 응답에 쿠키 추가
        sessionManager.createSession(member, response);

        // 요청에 응답 쿠키 저장 -> 왜 요청에 저장?
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(response.getCookies());

        // 세션 조회
        Object result = sessionManager.getSession(request);
        assertThat(result).isEqualTo(member); // 왜 멤버랑 비교?

        // 세션 만료, 세션id가 키, 객체가 value
        sessionManager.expire(request);
        Object expired = sessionManager.getSession(request);
        assertThat(expired).isNull();

    }

}