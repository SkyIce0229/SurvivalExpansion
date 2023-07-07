package tmmi.skyice.survivalexpansion.db.service;

import tmmi.skyice.survivalexpansion.db.util.DB;
import tmmi.skyice.survivalexpansion.util.LogUtil;

import java.lang.reflect.*;

public class ServiceFactory {
    private ServiceFactory() {

    }

    @SuppressWarnings("unchecked")
    public static <T> T createService(Class<T> clazz) {
        Class<?>[] interfaces = clazz.getInterfaces();
        return (T) Proxy.newProxyInstance(interfaces[0].getClassLoader(), new Class[]{interfaces[0]}, new ServiceInterceptor<>(clazz));
    }
    private static class ServiceInterceptor<T> implements InvocationHandler {
        private final T target;

        public ServiceInterceptor(Class<T> clazz) {
            try {
                Constructor<T> c = clazz.getDeclaredConstructor();
                c.setAccessible(true);
                target = c.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //java动态代理 事务控制 拦截方法帮你提交（省去DBUtil.beginTransaction();........）
            LogUtil.debug("准备开启事务");
            DB.beginTransaction();
            LogUtil.debug("开启事务完成");
            try {
                Object result = method.invoke(target, args);
                LogUtil.debug("准备提交事务");
                DB.commitTransaction();
                LogUtil.debug("提交事务完成");
                return result;
            } catch (Throwable t) {
                LogUtil.debug("准备回滚事务");
                DB.rollbackTransaction();
                LogUtil.debug("回滚事务完成");
                throw t;
            } finally {
                LogUtil.debug("准备关闭会话");
                DB.closeConnection();
                LogUtil.debug("会话关闭完成");
            }

        }
    }
}
