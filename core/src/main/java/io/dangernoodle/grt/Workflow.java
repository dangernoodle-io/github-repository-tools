package io.dangernoodle.grt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public interface Workflow
{
    void execute(Repository project, Context context) throws Exception;

    String getName();

    public class Context
    {
        private Map<Class<?>, Object> map = new HashMap<>();

        private boolean org;

        public void add(Object object)
        {
            map.put(object.getClass(), object);
        }

        @SuppressWarnings("unchecked")
        public <T> T get(Class<T> clazz)
        {
            return (T) map.get(clazz);
        }

        public boolean isOrg()
        {
            return org;
        }

        public void setOrg(boolean org)
        {
            this.org = org;
        }
    }

    public interface Step
    {
        void execute(Repository repository, Context context) throws IOException;
    }
}
