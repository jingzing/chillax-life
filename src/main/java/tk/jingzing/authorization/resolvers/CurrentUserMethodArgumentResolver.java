package tk.jingzing.authorization.resolvers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import tk.jingzing.authorization.annotation.CurrentUser;
import tk.jingzing.authorization.config.Constants;
import tk.jingzing.model.User;
import tk.jingzing.service.auth.UserAuthService;

/**
 * 增加方法注入，将含有CurrentUser注解的方法参数注入当前登录用户
 * Created by wangyunjing on 2016/11/3.
 */
@Component
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver{

    @Autowired
    private UserAuthService userAuthService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //如果参数类型是User并且有CurrentUser注解则支持
        if(parameter.getParameterType().isAssignableFrom(User.class) &&
                parameter.hasParameterAnnotation(CurrentUser.class)){
            return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        //取出鉴权时存入的登录用户Id
        Long currentUserId = (Long) webRequest.getAttribute(Constants.CURRENT_USER_ID, RequestAttributes.SCOPE_REQUEST);
        if (currentUserId != null) {
            //从数据库中查询并返回
            return userAuthService.findOne(currentUserId);
        }
        throw new MissingServletRequestPartException(Constants.CURRENT_USER_ID);
    }
}
