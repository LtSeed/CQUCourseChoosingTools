package com.company;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.*;
import java.util.logging.Level;

public class ClassSelector {
    // 主修专业课程的选择器
    public static final String MAJOR_COURSES_SELECTOR = "#app > section > section > section > main > div.u-layout-content > div > div.u-page-body > div > div > div.select-course-content > div > div > div > div > div.select-course > div > div > div.content-wrap > div.course-list-area > div.course-table-item-wrap > div:nth-child(1) > div > div.small-tab.select-tab.ant-table-wrapper > div > div > div > div > div > table > tbody";
    // 通识教育课程的选择器
    public static final String GENERAL_EDUCATION_COURSES_SELECTOR = "#app > section > section > section > main > div.u-layout-content > div > div.u-page-body > div > div > div.select-course-content > div > div > div > div > div.select-course > div > div > div.content-wrap > div.course-list-area > div.course-table-item-wrap > div:nth-child(2) > div > div.small-tab.select-tab.ant-table-wrapper > div > div > div > div > div > table > tbody";
    // 非限制选修课程的选择器
    public static final String ELECTIVE_COURSES_SELECTOR = "#app > section > section > section > main > div.u-layout-content > div > div.u-page-body > div > div > div.select-course-content > div > div > div > div > div.select-course > div > div > div.content-wrap > div.course-list-area > div.course-table-item-wrap > div:nth-child(3) > div > div.small-tab.select-tab.ant-table-wrapper > div > div > div > div > div > table > tbody";
    final WebDriver driver;
    final WebDriverWait wait;

    ClassSelector(int timeOutInSeconds){
        // 设置日志记录级别
        System.setProperty("webdriver.edge.driver", "G:\\SERVER\\msedgedriver.exe");
        System.setProperty("webdriver.chrome.driver", "G:\\SERVER\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        options.setCapability("goog:loggingPrefs", logPrefs);
        options.setCapability("acceptInsecureCerts", true);
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, timeOutInSeconds);
        driver.get("https://sso.cqu.edu.cn/login?service=https:%2F%2Fmy.cqu.edu.cn%2Fauthserver%2Fauthentication%2Fcas");
    }
    public boolean login(String id, String password, int timeOutInSeconds) {
        try {
            do {
                WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds); // 等待最多10秒
                WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#login-normal > div:nth-child(2) > form > div.login-normal-item.ant-row.ng-star-inserted > nz-input-group > input")));
                // 使用XPath定位用户名和密码输入框
                WebElement usernameInput = driver.findElement(By.cssSelector("#login-normal > div:nth-child(2) > form > div.login-normal-item.ant-row.ng-star-inserted > nz-input-group > input"));
                WebElement passwordInput = driver.findElement(By.cssSelector("#login-normal > div:nth-child(2) > form > div.login-normal-item.passwordInput.ant-row > nz-input-group > input"));

                // 输入用户名和密码
                usernameInput.sendKeys(id);
                passwordInput.sendKeys(password);

                // 定位并点击登录按钮
                WebElement loginButton = driver.findElement(By.cssSelector("#login-normal > div:nth-child(2) > form > div.login-normal-button.ant-row > div > button"));
                loginButton.click();
            } while (driver.getCurrentUrl().contains("sso.cqu.edu.cn"));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 验证所选课程是否成功添加到用户的课程列表中。
     *
     * @param courseName 选定的课程名称。
     * @return 如果课程成功添加，返回true；否则返回false。
     */
    public boolean isCourseAdded(String courseName) throws InterruptedException {
        // 课程列表的选择器
        String coursesListSelector = "#resultList > div > div.ant-collapse.ant-collapse-borderless.ant-collapse-icon-position-left";
        // 获取课程列表
        List<WebElement> enrolledCourses = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(coursesListSelector + " > div")));
        for (WebElement course : enrolledCourses) {
            // 课程名可能位于每个列表项的特定位置
            String enrolledCourseName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span.resultList-name > span:nth-child(1) > a"))).getText();
            if (enrolledCourseName.equals(courseName)) {
                return true; // 找到了课程，说明已成功添加
            }
        }
        return false; // 未找到课程，可能没有成功添加
    }

    /**
     * 点击勾选框以选择课程，然后点击选课按钮并确认。
     *
     */
    public void confirmCourseSelection() throws InterruptedException {
        // 选课按钮的选择器
        String selectCourseButtonSelector = "body > div:nth-child(21) > div > div.ant-drawer-content-wrapper > div > div > div.ant-drawer-body > div:nth-child(3) > div > div > div.select-class-info-modal > div.btn-right > button";
        WebElement selectCourseButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selectCourseButtonSelector)));
        selectCourseButton.click();
        // 确认选课按钮的选择器
        String confirmButtonSelector = "body > div:nth-child(23) > div > div.ant-modal-wrap > div > div.ant-modal-content > div > div > div.ant-modal-confirm-btns > button.ant-btn.ant-btn-primary";
        WebElement confirmButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(confirmButtonSelector)));
        confirmButton.click();
    }

    /**
     * 在老师列表中选择指定的老师。
     *
     * @param preferredTeachers 用户定义的老师优先级列表。
     * @param timeOutInSeconds
     */
    public boolean selectTeacher(List<String> preferredTeachers, int timeOutInSeconds) throws InterruptedException {
        // 老师列表的选择器
        String teachersListSelector = "body > div:nth-child(21) > div > div.ant-drawer-content-wrapper > div > div > div.ant-drawer-body > div:nth-child(3) > div > div > div.select-class-info-modal > div.tab-modal > div > div.ant-table-wrapper > div > div > div > div > div > table > tbody";

        // 创建一个WebDriverWait对象，设置最大等待时间为100秒
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);

        // 等待老师列表中的元素加载完成，使用ExpectedConditions.presenceOfAllElementsLocatedBy方法
        List<WebElement> teacherElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(teachersListSelector + " > tr")));
        for (String preferredTeacher : preferredTeachers) {
            for (WebElement teacherElement : teacherElements) {
                // 假设老师的名字位于每行的第4列
                String teacherName = teacherElement.findElement(By.cssSelector("td:nth-child(4)")).getText();
                System.out.println(teacherName);
                if (teacherName.equals(preferredTeacher)) {
                    // 找到匹配的老师，进行选择操作
                    // 假设选课状态栏目位于第9列
                    try {
                        WebElement element = teacherElement.findElement(By.cssSelector("td:nth-child(9) > span  > span"));
                        if(!element.getText().equals("容量已满")) {
                            throw new InterruptedException();
                        }
                    } catch (Exception e) {
                        WebElement selectBox = teacherElement.findElement(By.cssSelector("td:nth-child(9) > span > label > span"));
                        selectBox.click();
                        System.out.println("选中了老师");
                        return true; // 找到并选择了老师，结束函数
                    }
                }
            }
        }

        System.out.println("未找到优先级内的老师或所有优先级老师均不可选");
        return false;
    }


    /**
     * 获取特定课程的可选教师列表。
     *
     * @return 老师名称的列表。
     * @param timeOutInSeconds
     */
    public List<String> getAvailableTeachers(int timeOutInSeconds) {
        List<String> teachers = new ArrayList<>();

        // 老师列表的选择器
        String teachersListSelector = "body > div:nth-child(21) > div > div.ant-drawer-content-wrapper > div > div > div.ant-drawer-body > div:nth-child(3) > div > div > div.select-class-info-modal > div.tab-modal > div > div.ant-table-wrapper > div > div > div > div > div > table > tbody";

        // 创建一个WebDriverWait对象，设置最大等待时间为10秒
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);

        // 等待老师列表中的元素加载完成，使用ExpectedConditions.presenceOfAllElementsLocatedBy方法
        List<WebElement> teacherElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(teachersListSelector + " > tr")));
        for (WebElement teacherElement : teacherElements) {
            // 假设老师的名字位于每行的第4列
            String teacherName = teacherElement.findElement(By.cssSelector("td:nth-child(4)")).getText();
            teachers.add(teacherName);
        }

        return teachers;
    }


    /**
     * 从可选课程列表中选择指定的课程并点击进入选课界面。
     *
     * @param courseName 用户希望选择的课程名称。
     * @param timeOutInSeconds
     */
    public boolean selectCourseByName(String courseName, int timeOutInSeconds) throws InterruptedException {

        // 创建一个WebDriverWait对象，设置最大等待时间为100秒
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);

        // 获取主修专业课程列表，使用ExpectedConditions.presenceOfAllElementsLocatedBy方法
        if (clickCourses(MAJOR_COURSES_SELECTOR, wait, courseName)) {
            return true;
        }

        // 获取通识教育课程列表，使用ExpectedConditions.presenceOfAllElementsLocatedBy方法
        if (clickCourses(GENERAL_EDUCATION_COURSES_SELECTOR, wait, courseName)) {
            return true;
        }

        // 获取非限制选修课程列表，使用ExpectedConditions.presenceOfAllElementsLocatedBy方法
        return clickCourses(ELECTIVE_COURSES_SELECTOR, wait, courseName);
    }

    /**
     * 获取所有可选的课程列表。
     *
     * @return 三个分类的课程列表的集合。
     * @param timeOutInSeconds
     */
    public List<List<String>> getAllAvailableCourses(int timeOutInSeconds) {
        // 存储三类课程的列表
        List<List<String>> allCourses = new ArrayList<>();

        // 创建一个WebDriverWait对象，设置最大等待时间为100秒
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);

        // 获取主修专业课程列表，使用ExpectedConditions.presenceOfAllElementsLocatedBy方法
        List<String> majorCourses = getCourses(MAJOR_COURSES_SELECTOR, wait);
        allCourses.add(majorCourses);

        // 获取通识教育课程列表，使用ExpectedConditions.presenceOfAllElementsLocatedBy方法
        List<String> generalEducationCourses = getCourses(GENERAL_EDUCATION_COURSES_SELECTOR, wait);
        allCourses.add(generalEducationCourses);

        // 获取非限制选修课程列表，使用ExpectedConditions.presenceOfAllElementsLocatedBy方法
        List<String> electiveCourses = getCourses(ELECTIVE_COURSES_SELECTOR, wait);
        allCourses.add(electiveCourses);

        return allCourses;
    }

    /**
     * 根据给定的选择器，获取课程列表。
     *
     * @param coursesSelector 课程列表的选择器。
     * @param wait WebDriverWait对象。
     * @return 课程名称的列表。
     */
    public List<String> getCourses(String coursesSelector, WebDriverWait wait) {
        List<String> courses = new ArrayList<>();

        // 等待课程列表中的元素加载完成
        List<WebElement> courseElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(coursesSelector + " > tr")));
        for (WebElement courseElement : courseElements) {
            // 假设课程名位于每行的第一列
            String courseName = courseElement.findElement(By.cssSelector("td:nth-child(1)")).getText();
            courses.add(courseName);
        }

        return courses;
    }

    /**
     * 根据给定的名字，从课程列表点击课程。
     *
     * @param coursesSelector 课程列表的选择器。
     * @param wait WebDriverWait对象。
     * @return 课程名称的列表。
     */
    public boolean clickCourses(String coursesSelector, WebDriverWait wait, String name) {
        // 等待课程列表中的元素加载完成
        List<WebElement> courseElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(coursesSelector + " > tr")));
        for (WebElement courseElement : courseElements) {
            // 假设课程名位于每行的第一列
            WebElement element = courseElement.findElement(By.cssSelector("td:nth-child(1)"));
            String courseName = element.getText();
            if(courseName.equals(name)){
                element.click();
                element.findElement(By.cssSelector("a")).click();
                return true;
            }
        }
        return false;
    }

    /**
     * 执行选课的前两个步骤：点击选课管理按钮和选课按钮。
     *
     * @return 如果成功进入选课界面返回true，否则返回false。
     * @param timeOutInSeconds
     */
    public boolean selectCourse(int timeOutInSeconds) {
        try {
            // 创建一个WebDriverWait对象，设置最大等待时间为100秒
            WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds+100);

            // 1. 点击选课管理按钮
            WebElement courseManagementButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#app > section > section > main > div > div > div > div:nth-child(1) > div > div:nth-child(2) > div > div.ant-card-body > div > div.body-container.body-padding.vb-content.blur-bottom > div > div:nth-child(5) > div")));
            courseManagementButton.click();

            // 等待选课界面加载，使用ExpectedConditions.urlContains方法
            String c = driver.getWindowHandle();

            // 切换到新打开的标签页，使用driver.getWindowHandles和driver.switchTo方法
            Set<String> windowHandles = driver.getWindowHandles(); // 获取所有的窗口句柄
            for (String windowHandle : windowHandles) {
                if (!windowHandle.equals(c)) { // 如果不是当前的窗口句柄，就切换到新的窗口
                    driver.switchTo().window(windowHandle); // 切换到新的窗口
                    break;
                }
            }
            driver.manage().window().maximize();
            // 等待选课按钮出现，使用ExpectedConditions.visibilityOfElementLocated方法
            WebElement selectCourseButton;
            String cssSelector = "#app > section > section > section > main > div.u-layout-content > div > div > div > div > div > div.container-all.stu-select-class-tag > div:nth-child(1) > div:nth-child(2) > div > div > div:nth-child(3) > div > div > div.selected-course-info > div > div.select-right > span > button";
            selectCourseButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(cssSelector)));

            Thread.sleep(1000);//歇会，不然会报错
            // 2. 点击选课按钮
            selectCourseButton.click();
            return true;
        } catch (NoSuchElementException e) {
            System.out.println("发生错误：" + e.getMessage());
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }


}
