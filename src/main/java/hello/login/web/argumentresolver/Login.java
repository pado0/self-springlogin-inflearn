package hello.login.web.argumentresolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) // 파라미터에 붙는 어노테이션
@Retention(RetentionPolicy.RUNTIME) // 리플렉션을 할 수 있도록 런타임까지 어노테이션 정보가 남아있록
public @interface Login {
}
