package cash.xcl.util;

import net.openhft.chronicle.core.util.ObjectUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public abstract class OneArgInvocationHandler implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(proxy, args);
        }
        if (args == null || args.length != 1)
            throw new UnsupportedOperationException("Needs to be exactly one arg");
        Object ret = doInvoke(proxy, method, args[0]);
        if (ret != null)
            return ret;
        Class<?> returnType = method.getReturnType();
        if (proxy.getClass().isAssignableFrom(returnType))
            return this;
        return ObjectUtils.defaultValue(returnType);
    }

    protected abstract Object doInvoke(Object proxy, Method method, Object arg);
}
