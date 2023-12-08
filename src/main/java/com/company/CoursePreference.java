package com.company;

import java.util.Arrays;
import java.util.List;

public record CoursePreference(String courseName,List<String> preferredTeachers) {

    public static void chooseClass(CoursePreference coursePreference) {
        chooseClass(1, coursePreference);
    }

    private static void chooseClass(int k, CoursePreference coursePreference) {
        ClassSelector classSelector = new ClassSelector(Main.TIME_OUT_IN_SECONDS);
        // 执行登录操作
        classSelector.login(Main.INPUT_ID, Main.INPUT_PSD, Main.TIME_OUT_IN_SECONDS);
        // 执行选课流程
        boolean selectionPageOpened = classSelector.selectCourse(Main.TIME_OUT_IN_SECONDS);
        if (!selectionPageOpened) {
            System.out.println("无法打开选课界面。");
            return;
        }
        // 获取所有可选课程
        List<List<String>> allAvailableCourses = classSelector.getAllAvailableCourses(Main.TIME_OUT_IN_SECONDS);
        // 此处可以打印或处理获取到的课程列表
        allAvailableCourses.forEach(o-> System.out.println(Arrays.toString(o.toArray())));
        // 选择指定的课程
        String desiredCourse = coursePreference.courseName();
        try {
            boolean b = classSelector.selectCourseByName(desiredCourse, Main.TIME_OUT_IN_SECONDS);
            System.out.println("是否成功选择课程："+ b);
            if (!b) {
                System.out.println("正在重试（第"+k+"次）.");
                classSelector.driver.quit();
                chooseClass(++k, coursePreference);
                return;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 获取可选的教师列表并选择教师
        List<String> preferredTeachers = coursePreference.preferredTeachers();
        List<String> teacherList = classSelector.getAvailableTeachers(Main.TIME_OUT_IN_SECONDS);
        try {
            System.out.println(Arrays.toString(teacherList.toArray()));
            if (!classSelector.selectTeacher(preferredTeachers, Main.TIME_OUT_IN_SECONDS)) {
                System.out.println("老师选择失败, 正在重试（第"+k+"次）.");
                classSelector.driver.quit();
                chooseClass(++k, coursePreference);
                return;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 确认选课
        try {
            classSelector.confirmCourseSelection();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 验证课程是否成功添加
        boolean isAdded = false;
        try {
            isAdded = classSelector.isCourseAdded(desiredCourse);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        classSelector.driver.quit();
        if (isAdded) {
            System.out.println("课程成功添加到课程列表");
        } else {
            if(k > 5) System.out.println("课程未添加到课程列表,不再重试");
            System.out.println("课程未添加到课程列表,正在重试（第"+k+"次）.");
            chooseClass(++k, coursePreference);
        }
    }

    public void apply(){
        chooseClass(this);
    }
    // 构造函数、getters 和 setters 省略
}
