package cn.junbao.yubao.live.framework.web.strater.context;

import cn.junbao.yubao.live.common.interfaces.enums.WebRequestEnum;

import java.util.HashMap;
import java.util.Map;

public class WebRequestContext {

    private static final ThreadLocal<Map<Object, Object>> resources = new InheritableThreadLocalMap<>();

    public static void set(Object key, Object value) {

        if (key == null) {
            throw new IllegalArgumentException("key can not be null");
        }
        if (value == null) {
            resources.get().remove(key);
        }
        resources.get().put(key, value);
    }

    public static Long getUserId() {
        Object userId = get(WebRequestEnum.WEB_REQUEST_USER_ID.getName());
        return userId == null ? null : (Long) userId;
    }

    //设计一个get方法
    public static Object get(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("key can not be null");
        }
        return resources.get().get(key);
    }

    //clear方法，防止内存泄漏
    public static void clear() {
        resources.remove();
    }

    //实现父子线程之间的线程本地变量传递
    private static final class InheritableThreadLocalMap<T extends Map<Object, Object>> extends InheritableThreadLocal<Map<Object, Object>> {

        @Override
        protected Map<Object, Object> initialValue() {
            return new HashMap();
        }

        @Override
        protected Map<Object, Object> childValue(Map<Object, Object> parentValue) {
            if (parentValue != null) {
                return (Map<Object, Object>) ((HashMap<Object, Object>) parentValue).clone();
            } else {
                return null;
            }
        }
    }

}
