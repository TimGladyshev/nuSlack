package com.neu.prattle;

import com.neu.prattle.controller.UserController;
import com.neu.prattle.model.User;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertTrue;

public class TestController {
  private UserService us;
  private UserController uc;
  private User newUser;

  @Before
  public void setUp() {
    System.setProperty("testing", "true");
    us = UserServiceImpl.getInstance();
    assertTrue(us.isTest());
    uc = new UserController();
    newUser = new User("TEST_USER_2");
  }

  @After
  public void tearDown(){
    User user = us.findUserByName("TEST_USER_2").get();
    us.deleteUser(user);
    System.setProperty("testing", "false");
  }

  @Test
  public void basicControllerTest(){
     Response responce = uc.createUserAccount(newUser);
     Assert.assertEquals(responce.getStatus(), Response.ok().build().getStatus());
     Response responce2 = uc.createUserAccount(newUser);
     Assert.assertEquals(responce2.getStatus(), Response.status(409).build().getStatus());
  }
}

