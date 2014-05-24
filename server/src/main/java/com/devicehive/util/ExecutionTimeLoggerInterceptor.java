package com.devicehive.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.Calendar;

@LogExecutionTime
@Interceptor
public class ExecutionTimeLoggerInterceptor {

    @AroundInvoke
    public Object log(InvocationContext ctx) throws Exception {
        Method method = ctx.getMethod();
        String name = method.getDeclaringClass().getSimpleName() + "." + method.getName();
        Timer t = new Timer(name);
        try {
            return ctx.proceed();
        } finally {
            t.logExecuted();
        }
    }


    public static class Timer {

        private long start;

        private static final Logger logger = LoggerFactory.getLogger(Timer.class);

        private static final int MAX_METHOD_EXECUTION_TIME = 50000; // 50ms

        private final String methodName;

        /**
         * Creates new instance
         */
        public static Timer newInstance(String methodName) {
            return new Timer(methodName);
        }

        private Timer(String methodName) {
            this.methodName = methodName;
            logger.debug("Execution of {} is started", methodName);
            start = System.nanoTime();
        }


        /**
         * @return number of milliseconds, passed from creation, or reset call
         */
        private long click() {
            return System.nanoTime() - start;
        }


        /**
         * logs message like "Execution of methodName  took 100 milliseconds"
         * if execution time is less or equal MAX_METHOD_EXECUTION_TIME will log with debug priority,
         * Will log with warning priority otherwise
         *
         */
        public void logExecuted() {
            long time = click() / 1000;
            if (time > MAX_METHOD_EXECUTION_TIME) {
                logger.warn("Execution of {} took {} microseconds", methodName, time);
            } else {
                logger.debug("Execution of {} took {} microseconds", methodName, time);
            }

        }

    }

}
