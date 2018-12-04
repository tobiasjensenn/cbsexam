package com.cbsexam;

import cache.UserCache;
import com.google.gson.Gson;
import controllers.UserController;
import java.util.ArrayList;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.Product;
import model.User;
import utils.Encryption;
import utils.Log;

@Path("user")
public class UserEndpoints {

  /**
   * @param idUser
   * @return Responses
   */
  @GET
  @Path("/{idUser}")
  public Response getUser(@PathParam("idUser") int idUser) {

    // Use the ID to get the user from the controller.
    User user = UserController.getUser(idUser);

    // TODO: Add Encryption to JSON - FIXED
    // Convert the user object to json in order to return the object
    String json = new Gson().toJson(user);
    json = Encryption.encryptDecryptXOR(json);


    // Return the user with the status code 200
    // TODO: What should happen if something breaks down? - FIXED
    //Hvis user afviger fra null, dvs. hvis der er et id 1, 2, 3 osv, skal den returnere 200, da det er succes
    //ellers skal den returnere nedenstående tekst.
    if (user != null) {
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("Could not fetch users").build();
    }
  }

  public static UserCache userCache = new UserCache();

  /**
   * @return Responses
   */
  @GET
  @Path("/")
  public Response getUsers() {

    // Write to log that we are here
    Log.writeLog(this.getClass().getName(), this, "Get all users", 0);

    // Get a list of users
    ArrayList<User> users = userCache.getUsers(false);


    // TODO: Add Encryption to JSON - FIXED
    // Transfer users to json in order to return it to the user
    String json = new Gson().toJson(users);
    json = Encryption.encryptDecryptXOR(json);

    // Return the users with the status code 200
    return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createUser(String body) {

    // Read the json from body and transfer it to a user class
    User newUser = new Gson().fromJson(body, User.class);

    // Use the controller to add the user
    User createUser = UserController.createUser(newUser);

    // Get the user back with the added ID and return it to the user
    String json = new Gson().toJson(createUser);

    // Return the data to the user
    if (createUser != null) {
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("Could not create user").build();
    }
  }

  // TODO: Make the system able to login users and assign them a token to use throughout the system. - FIXED
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response loginUser(String body) {

    // Read the json from body and transfer it to a user class
    User user = new Gson().fromJson(body, User.class);

    // Get the user back with the added ID and return it to the user
    String token = UserController.loginUser(user);

    /// Return the data to the user
    if (token != "") {
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(token).build();
    } else {
      return Response.status(400).entity("Could not create user").build();
    }

    // Return a response with status 200 and JSON as type
  }

  // TODO: Make the system able to delete users (FIX??)
  @DELETE
  @Path("{userId}/{token}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response deleteUser(@PathParam("userId") int userId, @PathParam("token") String token) {

    Boolean deleted = UserController.deleteUser(token);

    if (deleted) {
      // Return a response with status 200 and JSON as type
      return Response.status(200).entity("Delete successful").build();
    } else {
      return Response.status(400).entity("Delete not successful").build();
    }
  }

  // TODO: Make the system able to update users (FIX??)
  @PUT
  @Path("/idUser}/{token}")
  @Consumes(MediaType.APPLICATION_JSON)

  public Response updateUser(@PathParam("token") String token, String body) {

    User user = new Gson().fromJson(body, User.class);

    Boolean updated = UserController.updateUser(user, token);

    if (updated) {
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("Update successful").build();
    } else {
      return Response.status(400).entity("Update not successful").build();
    }
  }
}