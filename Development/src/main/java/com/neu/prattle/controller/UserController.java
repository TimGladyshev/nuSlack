package com.neu.prattle.controller;

import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.model.User;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;

import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/***
 * A Resource class responsible for handling CRUD operations
 * on User objects.
 *
 * @author CS5500 Fall 2019 Teaching staff
 * @version dated 2019-10-06
 */
@Path(value = "/user")
public class UserController {

    // Usually Dependency injection will be used to inject the service at run-time
    private UserService accountService = UserServiceImpl.getInstance();

    private static Logger logger = Logger.getLogger(UserController.class.getName());

    /***
     * Handles a HTTP POST request for user creation
     * 
     * @param user -> The User object decoded from the payload of POST request.
     * @return -> A Response indicating the outcome of the requested operation.
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUserAccount(User user) {
        try {
            accountService.addUser(user);
        } catch (UserAlreadyPresentException e) {
            return Response.status(409).build();
        }

        return Response.ok().build();
    }

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findUserByName(@PathParam("name") String name){
        Optional<User> res = accountService.findUserByName(name);
        ObjectMapper mapper = new ObjectMapper();
        if (!res.isPresent()) return Response.status(404).build();
        User user = res.get();
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(user);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.ok().type(MediaType.APPLICATION_JSON).entity(jsonString).build();
    }
}
