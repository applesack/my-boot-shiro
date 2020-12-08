package xyz.scootaloo.bootshiro.security.filter;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 11:14
 */
public class StatelessWebSubjectFactory extends DefaultWebSubjectFactory {

    @Override
    public Subject createSubject(SubjectContext context) {
        // 这里都不创建session
        context.setSessionCreationEnabled(Boolean.FALSE);
        return super.createSubject(context);
    }

}
