package com.nineleaps.leaps;

import org.junit.jupiter.api.extension.*;
import java.util.HashMap;
import java.util.Map;

public class RuntimeBenchmarkExtension implements BeforeAllCallback, AfterAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private Map<Class<?>, Long> totalExecutionTimeMap = new HashMap<>();
    private Map<Class<?>, Integer> testCountMap = new HashMap<>();

    @Override
    public void beforeAll(ExtensionContext context) {
        totalExecutionTimeMap.clear();
        testCountMap.clear();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        totalExecutionTimeMap.forEach((testClass, totalExecutionTime) -> {
            int testCount = testCountMap.get(testClass);
            double averageTime = (double) totalExecutionTime / testCount;
            System.out.println(testClass.getSimpleName() + " Average runtime for " + testCount + " tests: " + averageTime + " ms");
        });
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        // Capture the start time of each test
        Class<?> testClass = context.getTestClass().orElseThrow(IllegalStateException::new);
        context.getStore(ExtensionContext.Namespace.create(getClass(), testClass))
                .put("startTime", System.currentTimeMillis());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        Class<?> testClass = context.getTestClass().orElseThrow(IllegalStateException::new);
        long startTime = context.getStore(ExtensionContext.Namespace.create(getClass(), testClass))
                .remove("startTime", long.class);

        long duration = System.currentTimeMillis() - startTime;

        totalExecutionTimeMap.merge(testClass, duration, Long::sum);
        testCountMap.merge(testClass, 1, Integer::sum);
    }
}
