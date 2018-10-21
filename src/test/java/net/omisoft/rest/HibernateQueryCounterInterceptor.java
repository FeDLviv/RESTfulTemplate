package net.omisoft.rest;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;

public class HibernateQueryCounterInterceptor extends EmptyInterceptor implements SQLQueryCounter {

    private static final ThreadLocal<Integer> TOTAL_COUNT = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<Integer> INSERT_COUNT = ThreadLocal.withInitial(() -> 0);

    @Override
    public int getTotalCount() {
        return TOTAL_COUNT.get();
    }

    @Override
    public int getInsertCount() {
        return INSERT_COUNT.get();
    }

    @Override
    public void reset() {
        TOTAL_COUNT.remove();
        INSERT_COUNT.remove();
    }

    @Override
    public String onPrepareStatement(String sql) {
        TOTAL_COUNT.set(TOTAL_COUNT.get() + 1);
        return super.onPrepareStatement(sql);
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        INSERT_COUNT.set(INSERT_COUNT.get() + 1);
        return super.onSave(entity, id, state, propertyNames, types);
    }

}