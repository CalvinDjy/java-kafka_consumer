package com.paramida.unit;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 *
 * Created by Calvin.Ding on 2017/3/1.
 */
@ContextConfiguration(locations={"classpath:application-test.xml"})
public class BaseTest extends AbstractTestNGSpringContextTests {

    @BeforeClass
    public void setUp(){
        System.out.println("begin to execute " + this.getClass().getName());
    }

    @AfterClass
    public void destroy() {
        System.out.println("退出，资源释放");
    }

}
