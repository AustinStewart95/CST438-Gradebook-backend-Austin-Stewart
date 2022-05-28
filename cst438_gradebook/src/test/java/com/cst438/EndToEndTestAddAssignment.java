package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;

/*
 * This example shows how to use selenium testing using the web driver 
 * with Chrome browser.
 * 
 *  - Buttons, input, and anchor elements are located using XPATH expression.
 *  - onClick( ) method is used with buttons and anchor tags.
 *  - Input fields are located and sendKeys( ) method is used to enter test data.
 *  - Spring Boot JPA is used to initialize, verify and reset the database before
 *      and after testing.
 *      
 *  In SpringBootTest environment, the test program may use Spring repositories to 
 *  setup the database for the test and to verify the result.
 */

@SpringBootTest
public class EndToEndTestAddAssignment {

	public static final String CHROME_DRIVER_FILE_LOCATION = "C:/chromedriver_win32/chromedriver.exe";

	public static final String URL = "http://localhost:3000";
	public static final String TEST_USER_EMAIL = "test@csumb.edu";
	public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
	public static final int SLEEP_DURATION = 1000; // 1 second.
	public static final String TEST_ASSIGNMENT_NAME = "Test Assignment";
	public static final String TEST_COURSE_TITLE = "Test Course";
	public static final String TEST_STUDENT_NAME = "Test";
	public static final String TEST_DUE_DATE = "2020-01-10";
	public static final int TEST_COURSE_ID = 40443; 

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	AssignmentGradeRepository assignnmentGradeRepository;

	@Autowired
	AssignmentRepository assignmentRepository;

	@Test
	public void addAssignmentTest() throws Exception {

	   /*
       * if assignment already exists, then delete the assignment.
       */
      
      Assignment x = null;
      do {
         x = assignmentRepository.findByNameAndCourseId(TEST_ASSIGNMENT_NAME, TEST_COURSE_ID);
         if (x != null)
            assignmentRepository.delete(x);
      } while (x != null);

		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on
		
		/*
		 * initialize the WebDriver and get the home page. 
		 */

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);
		

		try {
		   // Locate and click "Add Assignment" button which is the last button on the page.
		   System.out.println("before add assignment button click"); // for debug
		   driver.findElement(By.xpath("//button[@id='addAssignment']")).click();
         System.out.println("after add assignment button click"); // for debug
         Thread.sleep(SLEEP_DURATION);
         
         // enter assignment information and click Add button
         
         driver.findElement(By.xpath("//input[@name='course_id']")).sendKeys(Integer.toString(TEST_COURSE_ID));
         driver.findElement(By.xpath("//input[@name='assignment_name']")).sendKeys(TEST_ASSIGNMENT_NAME);
         driver.findElement(By.xpath("//input[@name='due_date']")).sendKeys(TEST_DUE_DATE);
         driver.findElement(By.xpath("//button[@id='Add']")).click();
         Thread.sleep(SLEEP_DURATION);
         
         /*
          * verify that the new assignment shows in the assignment list.
          */ 
         
         List<WebElement> elements  = driver.findElements(By.xpath("//div[@data-field='assignmentName']/div"));
         boolean found = false;
         for (WebElement we : elements) {
            System.out.println(we.getText()); // for debug
            if (we.getText().equals(TEST_ASSIGNMENT_NAME)) {
               found=true;
               break;
            }
         }
         assertTrue( found, "Unable to locate TEST ASSIGNMENT in list of assignments to be graded.");
         
         // verify that the assignment row has been inserted to database.
         
         Assignment a = assignmentRepository.findByNameAndCourseId(TEST_ASSIGNMENT_NAME, TEST_COURSE_ID);
         assertNotNull(a, "Course assignment not found in database.");

		} catch (Exception ex) {
			throw ex;
		} finally {

		   // clean up database.
         
         Assignment a = assignmentRepository.findByNameAndCourseId(TEST_ASSIGNMENT_NAME, TEST_COURSE_ID);
         if (a != null) {
            assignmentRepository.delete(a);
            System.out.println("assignment found and deleted"); // for debug
         }
         else {
            System.out.println("assignment could not be found"); // for debug
         }

         driver.quit();
		}

	}
}