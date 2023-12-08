package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class Main {

    public static final String INPUT_ID = "your_id";
    public static final String INPUT_PSD = "your_psd";
    //若系统卡顿，比如卡点选课，必须延长这个时间，建议给100s以上。
    public static final int TIME_OUT_IN_SECONDS = 10;
    private static ExecutorService executorService;

    public static void main(String[] args) {
        List<CoursePreference> coursePreferences = new ArrayList<>();
        coursePreferences.add(new CoursePreference("your_course1", List.of("t1", "t2")));
        executorService = applyCourses(coursePreferences);

    }

    private static ExecutorService applyCourses(List<CoursePreference> coursePreferences) {
        if(coursePreferences == null) return null;
        ExecutorService executorService = Executors.newFixedThreadPool(coursePreferences.size());
        coursePreferences.forEach(o-> executorService.submit(o::apply));
        try {
            if (!executorService.awaitTermination(30L, TimeUnit.MINUTES)) {
                throw new InterruptedException();
            }
        } catch (InterruptedException e) {
            System.out.println("选课被打断或异常结束");
        }
        return executorService;
    }

}
