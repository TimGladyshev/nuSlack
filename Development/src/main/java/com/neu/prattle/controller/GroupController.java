package com.neu.prattle.controller;

import com.neu.prattle.exceptions.GroupAlreadyPresentException;
import com.neu.prattle.model.BasicGroup;

import com.neu.prattle.model.Request.GroupRequest;
import com.neu.prattle.model.User;
import com.neu.prattle.service.UserServiceWithGroups;
import com.neu.prattle.service.UserServiceWithGroupsImpl;
import com.sun.org.apache.xpath.internal.operations.Bool;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/***
 * A Resource class responsible for handling CRUD operations
 * on User objects.
 *
 * @author CS5500 Fall 2019 Teaching staff
 * @version dated 2019-10-06
 */
@Path(value = "/group")
public class GroupController {

  // Usually Dependency injection will be used to inject the service at run-time
  private UserServiceWithGroups accountService = UserServiceWithGroupsImpl.getInstance();

  /***
   * Handles a HTTP POST request for group creation
   *
   * @param group -> A Group object decoded from JSON Post.
   * @return -> A Response indicating the outcome of the requested operation.
   */
  @POST
  @Path("/create")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createGroup(BasicGroup group) {
    StringBuilder message = new StringBuilder();
    try {
      accountService.addGroup(group);
    } catch (GroupAlreadyPresentException e) {
      message.append("This group already exists");
      return Response.status(409).entity(message.toString()).build();
    } catch (IllegalArgumentException arg) {
      message.append("No members listed");
      return Response.status(409).entity(message.toString()).build();
    }
    message.append("Group Created: ");
    message.append(group.getName());
    return Response.ok().entity(message.toString()).build();
  }

  /***
   * Handles an HTTP DELETE request to remove group.
   *
   * @param request -> A Group object decoded from JSON Post. Has two strings.
   * @return -> A Response indicating the outcome of the requested operation.
   */
  @DELETE
  @Path("/delete")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response deleteGroup(GroupRequest request) {
    StringBuilder message = new StringBuilder();

    //query records
    Optional<BasicGroup> foundGroup = accountService
        .findGroupByName(request.getUser(), request.getGroup());
    Optional<User> sender = accountService.findUserByName(request.getSender());

    //check if found
    boolean allClear = true;
    if (!sender.isPresent()) {
      message.append("you have been lost");
      allClear = false;
    }
    if (!foundGroup.isPresent()) {
      message.append("group ").append(request.getGroup()).append(" not found.\n");
      allClear = false;
    }
    if (!allClear) {
      return Response.status(409).entity(message.toString()).build();
    }

    try {
      accountService.deleteGroup(sender.get(), foundGroup.get());
    } catch (GroupAlreadyPresentException e) {
      return Response.status(409).build();
    }
    message.append("Group Deleted: ");
    message.append(foundGroup.get().getName());
    return Response.ok().entity(message.toString()).build();
  }

  /***
   * Handles a HTTP PUT for adding a user.
   *
   * @param request -> A GroupRequest object decoded from JSON Post. Has three strings.
   * @return -> A Response indicating the outcome of the requested operation.
   */
  @PUT
  @Path("/addUser")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response extendGroup(GroupRequest request) {
    StringBuilder message = new StringBuilder();

    //query records
    Optional<BasicGroup> foundGroup = accountService
        .findGroupByName(request.getUser(), request.getGroup());
    Optional<User> foundUser = accountService.findUserByName(request.getUser());
    Optional<User> sender = accountService.findUserByName(request.getSender());

    //check if found
    boolean allClear = true;
    if (!foundUser.isPresent()) {
      message.append("user ").append(request.getUser()).append(" not found.\n");
      allClear = false;
    }
    if (!foundGroup.isPresent()) {
      message.append("group ").append(request.getGroup()).append(" not found.\n");
      allClear = false;
    }
    if (!sender.isPresent()) {
      message.append("you have been lost");
      allClear = false;
    }
    if (!allClear) {
      return Response.status(409).entity(message.toString()).build();
    }

    try {
      //attempt request
      accountService.extendUsers(sender.get(), foundUser.get(), foundGroup.get());
    } catch (Exception e) {
      return Response.status(409).build();
    }

    message.append("members of ");
    message.append(foundGroup.get().getName());
    message.append(" appended: ");
    message.append(foundUser.get().getName());
    // !!! Issue - no way to send a message to everyone except from JS or calling ChatEncPoint
    return Response.ok().entity(message.toString()).build();
  }

  /***
   * Handles a HTTP PUT for adding a Moderator.
   *
   * @param request -> A GroupRequest object decoded from JSON Post. Has three strings.
   * @return -> A Response indicating the outcome of the requested operation.
   */
  @PUT
  @Path("/addModerator")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response extendModerators(GroupRequest request) {
    StringBuilder message = new StringBuilder();

    //query records
    Optional<BasicGroup> foundGroup = accountService
        .findGroupByName(request.getUser(), request.getGroup());
    Optional<User> foundUser = accountService.findUserByName(request.getUser());
    Optional<User> sender = accountService.findUserByName(request.getSender());

    //check if found
    boolean allClear = true;
    if (!foundUser.isPresent()) {
      message.append("user ").append(request.getUser()).append(" not found.\n");
      allClear = false;
    }
    if (!foundGroup.isPresent()) {
      message.append("group ").append(request.getGroup()).append(" not found.\n");
      allClear = false;
    }
    if (!sender.isPresent()) {
      message.append("you have been lost");
      allClear = false;
    }
    if (!allClear) {
      return Response.status(409).entity(message.toString()).build();
    }

    try {
      //attempt request
      accountService.extendModerators(sender.get(), foundUser.get(), foundGroup.get());
    } catch (Exception e) {
      return Response.status(409).build();
    }

    message.append("moderators of ");
    message.append(foundGroup.get().getName());
    message.append(" appended: ");
    message.append(foundUser.get().getName());
    // !!! Issue - no way to send a message to everyone except from JS or calling ChatEncPoint
    return Response.ok().entity(message.toString()).build();
  }

  /***
   * Handles a HTTP PUT for removing a user.
   *
   * @param request -> A GroupRequest object decoded from JSON Post. Has three strings.
   * @return -> A Response indicating the outcome of the requested operation.
   */
  @PUT
  @Path("/removeUser")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response removeUser(GroupRequest request) {
    StringBuilder message = new StringBuilder();

    //query records
    Optional<BasicGroup> foundGroup = accountService
        .findGroupByName(request.getUser(), request.getGroup());
    Optional<User> foundUser = accountService.findUserByName(request.getUser());
    Optional<User> sender = accountService.findUserByName(request.getSender());

    //check if found
    boolean allClear = true;
    if (!foundUser.isPresent()) {
      message.append("user ").append(request.getUser()).append(" not found.\n");
      allClear = false;
    }
    if (!foundGroup.isPresent()) {
      message.append("group ").append(request.getGroup()).append(" not found.\n");
      allClear = false;
    }
    if (!sender.isPresent()) {
      message.append("you have been lost");
      allClear = false;
    }
    if (!allClear) {
      return Response.status(409).entity(message.toString()).build();
    }

    try {
      //attempt request
      accountService.removeUser(sender.get(), foundUser.get(), foundGroup.get());
    } catch (Exception e) {
      return Response.status(409).build();
    }

    message.append("member of ");
    message.append(foundGroup.get().getName());
    message.append(" removed: ");
    message.append(foundUser.get().getName());
    // !!! Issue - no way to send a message to everyone except from JS or calling ChatEncPoint
    return Response.ok().entity(message.toString()).build();
  }

  /***
   * Handles a HTTP PUT for removing a user.
   *
   * @param request -> A GroupRequest object decoded from JSON Post. Has three strings.
   * @return -> A Response indicating the outcome of the requested operation.
   */
  @PUT
  @Path("/removeModerator")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response removeModerator(GroupRequest request) {
    StringBuilder message = new StringBuilder();

    //query records
    Optional<BasicGroup> foundGroup = accountService
        .findGroupByName(request.getUser(), request.getGroup());
    Optional<User> foundUser = accountService.findUserByName(request.getUser());
    Optional<User> sender = accountService.findUserByName(request.getSender());

    //check if found
    boolean allClear = true;
    if (!foundUser.isPresent()) {
      message.append("user ").append(request.getUser()).append(" not found.\n");
      allClear = false;
    }
    if (!foundGroup.isPresent()) {
      message.append("group ").append(request.getGroup()).append(" not found.\n");
      allClear = false;
    }
    if (!sender.isPresent()) {
      message.append("you have been lost");
      allClear = false;
    }
    if (!allClear) {
      return Response.status(409).entity(message.toString()).build();
    }

    try {
      //attempt request
      accountService.removeUser(sender.get(), foundUser.get(), foundGroup.get());
    } catch (Exception e) {
      return Response.status(409).build();
    }

    message.append("moderator of ");
    message.append(foundGroup.get().getName());
    message.append(" removed: ");
    message.append(foundUser.get().getName());
    // !!! Issue - no way to send a message to everyone except from JS or calling ChatEncPoint
    return Response.ok().entity(message.toString()).build();
  }





}