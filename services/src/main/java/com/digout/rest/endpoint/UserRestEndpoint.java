package com.digout.rest.endpoint;

import com.digout.artifact.*;
import com.digout.converter.ApplicationVersionConverter;
import com.digout.converter.RegistrationDataConverter;
import com.digout.converter.UserTokenConverter;
import com.digout.exception.*;
import com.digout.manager.ApplicationManager;
import com.digout.manager.UsersManager;
import com.digout.model.UserRole;
import com.digout.model.entity.user.UserTokenEntity;
import com.digout.model.meta.Authenticated;
import com.digout.processor.UserProfileInputProcessor;
import com.digout.validation.*;
import com.google.common.io.Files;
import com.sun.jersey.multipart.FormDataParam;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Path("/")
public class UserRestEndpoint {

    @Autowired
    private UsersManager userManager;
    @Autowired
    private RegistrationDataConverter registrationDataConverter;
    @Autowired
    private ApplicationManager appManager;
    @Autowired
    private ApplicationVersionConverter appVersionConverter;
    @Autowired
    private UserTokenConverter userTokenConverter;
    @Autowired
    private UserProfileValidator userProfileValidator;
    @Autowired
    private RegistrationValidator registrationValidator;
    @Autowired
    private MessageValidator messageValidator;
    @Autowired
    private PasswordChangeValidator passwordChangeValidator;
    @Autowired
    private AddressValidator addressValidator;
    @Autowired
    private UserProfileInputProcessor userProfileInputProcessor;

    // todo:refactor consider address assignment
    @Path("/user/address/add")
    @POST
    @Authenticated(UserRole.USER)
    @Transactional
    public Address addAddress(final Address address) throws ApplicationException {
        this.addressValidator.validateAndRaise(address);
        return this.userManager.addAddress(address);
    }

    @POST
    @Path("/authenticate")
    @Produces({ "application/json" })
    @Consumes({ "application/json" })
    @Transactional
    public UserSession authenticate(final UserCredentials userCredentials) throws InvalidCredentialsException {
        return this.userTokenConverter.createTO(this.userManager.signInUser(userCredentials.getUsername(),
                userCredentials.getPassword()));
    }

    @GET
    @Path("/authenticateFB/{accessToken}")
    @Produces({ "application/json" })
    @Transactional
    public UserSession authenticateFacebook(@PathParam("accessToken") final String accessToken)
            throws ApplicationException {
        final UserSession userSession = this.userTokenConverter.createTO(this.userManager.signInFacebook(accessToken));
        final User user = userSession.getUser();
        user.getCredentials().setUsername(user.getFullname());
        return userSession;
    }

    @GET
    @Path("/authenticateGuest")
    @Produces({ "application/json" })
    @Transactional
    public UserSession authenticateGuest() throws InvalidAuthenticationType, InvalidCredentialsException {
        UserTokenEntity userTokenEntity = this.userManager.signInGuest();
        UserSession userSession = new UserSession();
        userSession.setSessionToken(userTokenEntity.getTokenId());

        return userSession;
    }

    @Path("user/password/change")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated(UserRole.USER)
    @Transactional
    public Response changePassword(final PasswordChange passwordChange) throws ApplicationException {
        this.passwordChangeValidator.validateAndRaise(passwordChange);
        this.userManager.changePassword(passwordChange);
        return Response.ok().build();
    }

    @Path("/user/countingInfo")
    @GET
    @Produces("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public UserCountingInformation countingInfo() {
        return this.userManager.getUserCountingInformation();
    }

    @Path("/user/address/{addressId}/delete")
    @DELETE
    @Authenticated(UserRole.USER)
    @Transactional
    public Response deleteAddress(@PathParam("addressId") final Long addressId) throws ApplicationException {
        this.userManager.deleteAddress(addressId);
        return Response.ok().build();
    }

    @Path("message/{interlocutorId}/dialog/clear")
    @DELETE
    @Authenticated(UserRole.USER)
    @Transactional
    public Response deleteDialogByInterlocutorId(@PathParam("interlocutorId") final Long interlocutorId) {
        this.userManager.deleteDialogMessages(interlocutorId);
        return Response.ok().build();
    }

    @Path("/user/image/{imageId}/delete")
    @DELETE
    @Authenticated(UserRole.USER)
    @Produces("application/json")
    @Transactional
    public Response deleteUserPhoto(@PathParam("imageId") final Long imageId) throws ApplicationException {
        this.userManager.deleteImageByImageId(imageId);
        return Response.ok().build();
    }

    @Path("user/find")
    @GET
    @Produces("application/json")
    @Authenticated(UserRole.USER)
    @Transactional(readOnly = true)
    public Users findByUserName(@QueryParam("userName") final String userName,
            @QueryParam("pageNum") final Integer pageNum, @QueryParam("pageSize") final Integer pageSize)
            throws ApplicationException {
        return this.userManager.findUsersByUserName(userName, pageNum, pageSize);
    }

    @Path("user/{userId}/follow")
    @GET
    @Authenticated(UserRole.USER)
    @Transactional
    public Response follow(@PathParam("userId") final Long userId) throws ApplicationException {
        this.userManager.follow(userId);
        return Response.ok().build();
    }

    @Path("tag/tags")
    @GET
    @Produces("application/json")
    @Authenticated(UserRole.USER)
    @Transactional(readOnly = true)
    public Tags getAllTags(@QueryParam("pageNum") final Integer pageNum, @QueryParam("pageSize") final Integer pageSize) {
        return this.userManager.getAllTags(pageNum, pageSize);
    }

    /*
     * @GET
     * 
     * @Path("user/changeDefaultLocale")
     * 
     * @Produces("application/json")
     * 
     * @Authenticated(UserRole.USER)
     * 
     * @Transactional public Response changeDefaultLocale() { userManager.changeUserLanguage();
     * return Response.ok().build(); }
     */

    @Path("message/{interlocutorId}/dialog")
    @GET
    @Produces("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public Messages getDialogMessages(@QueryParam("pageNum") final Integer pageNum,
            @QueryParam("pageSize") final Integer pageSize, @PathParam("interlocutorId") final Long interlocutorId) {
        Messages messages = null;
        try {
            messages = this.userManager.getMessagesDialog(interlocutorId, pageNum, pageSize);
        } catch (ApplicationException ignored) {
        }
        return messages;
    }

    /*
     * @Path("message/{messageId}/delete")
     * 
     * @DELETE
     * 
     * @Produces("application/json")
     * 
     * @Authenticated(UserRole.USER)
     * 
     * @Transactional public Response deleteMessage(@PathParam("messageId") Long messageId) throws
     * ApplicationException { userManager.deleteMessage(messageId); return Response.ok().build(); }
     */

    @Path("user/followed")
    @GET
    @Authenticated(UserRole.USER)
    @Transactional
    public/* UserFollows */UserFollowing getFollowed(@QueryParam("pageNum") final Integer pageNum,
            @QueryParam("pageSize") final Integer pageSize) {
        return this.userManager.getFollowed(pageNum, pageSize);
    }

    @Path("user/{userId}/followed")
    @GET
    @Authenticated(UserRole.USER)
    @Transactional
    public/* UserFollows */UserFollowing getFollowedByUserId(@PathParam("userId") final Long userId,
            @QueryParam("pageNum") final Integer pageNum, @QueryParam("pageSize") final Integer pageSize) {
        return this.userManager.getFollowed(userId, pageNum, pageSize);
    }

    @Path("user/followers")
    @GET
    @Authenticated(UserRole.USER)
    @Transactional
    public/* UserFollows */UserFollowing getFollowers(@QueryParam("pageNum") final Integer pageNum,
            @QueryParam("pageSize") final Integer pageSize) {
        return this.userManager.getFollowers(pageNum, pageSize);
    }

    @Path("user/{userId}/followers")
    @GET
    @Authenticated(UserRole.USER)
    @Transactional
    public/* UserFollows */UserFollowing getFollowersByUserId(@PathParam("userId") final Long userId,
            @QueryParam("pageNum") final Integer pageNum, @QueryParam("pageSize") final Integer pageSize) {
        return this.userManager.getFollowers(userId, pageNum, pageSize);
    }

    @Path("message/messages")
    @GET
    @Produces("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public Messages getMessages(@QueryParam("pageNum") final Integer pageNum,
            @QueryParam("pageSize") final Integer pageSize) throws ApplicationException {
        return this.userManager.getMainMessages(pageNum, pageSize);
    }

    @Path("user/settings/notifications")
    @GET
    @Authenticated(UserRole.USER)
    @Produces("application/json")
    @Transactional
    public UserSettings getUserNotificationSettings() {
        return this.userManager.getUserNotificationSettings();
    }

    @GET
    @Path("user/{userId}/phoneNumber")
    @Produces("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public UserProfile getUserPhoneNumber(@PathParam("userId") final Long userId) throws UserNotFoundException {
        return this.userManager.getUserPhoneNumber(userId);
    }

    @GET
    @Path("/user/profile")
    @Produces("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public UserProfile getUserProfile() {
        return this.userManager.getUserProfile();
    }

    @GET
    @Path("/user/{userId}/profile")
    @Produces("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public UserProfile getUserProfileById(@PathParam("userId") final Long userId) throws UserNotFoundException {
        return this.userManager.getUserProfile(userId);
    }

    // test service
    /*
     * @Path("user/friends")
     * 
     * @GET
     * 
     * @Authenticated(UserRole.USER)
     * 
     * @Transactional public UserFollows getFriends(){ return userManager.getFriends(); }
     */

    @POST
    @Path("/init")
    @Consumes({ "application/json" })
    @Transactional(readOnly = true)
    public void init(final Version version) throws InvalidVersionException {
        this.appManager.getByClientPlatformTypeAndVersionAndServerVersion(this.appVersionConverter
                .createEntity(version));
    }

    @Path("/user/{email}/password/recover")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response recoverPassword(@PathParam("email") final String email) throws UserNotFoundException {
        this.userManager.recoverPasswordByEmail(email);
        return Response.ok().build();
    }

    @POST
    @Path("/register")
    @Consumes({ "application/json" })
    @Transactional
    public void register(final Registration registration) throws RegistrationException, ValidationException {
        this.registrationValidator.validateAndRaise(registration);
        this.userManager.regsiterLocalUser(this.registrationDataConverter.createEntity(registration));
    }

    @POST
    @Path("/user/saveProfile")
    @Consumes("application/json")
    @Produces("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public Info saveProfile(final UserProfile userProfile) throws ValidationException {
        final UserProfile profile = this.userProfileInputProcessor.preProcess(userProfile);
        this.userProfileValidator.validateAndRaise(profile);
        return this.userManager.saveProfile(profile);
    }

    @Path("message/send")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public Response sendMessage(final Message message) throws ApplicationException {
        this.messageValidator.validateAndRaise(message);
        this.userManager.sendMessage(message);
        return Response.ok().build();
    }

    @Path("user/settings/notifications/update")
    @POST
    @Produces("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public Response setUserNotificationSettings(final UserSettings userSettings) {
        this.userManager.setUserNotificationSettings(userSettings);
        return Response.ok().build();
    }

    @Path("user/{userId}/unfollow")
    @DELETE
    @Authenticated(UserRole.USER)
    @Transactional
    public Response unfollow(@PathParam("userId") final Long userId) throws ApplicationException {
        this.userManager.unfollow(userId);
        return Response.ok().build();
    }

    /*
     * @Path("/user/photo/delete")
     * 
     * @DELETE
     * 
     * @Authenticated(UserRole.USER)
     * 
     * @Transactional public Response deleteUserPhoto() throws ApplicationException {
     * userManager.deleteUserImage(); return Response.ok().build(); }
     */

    @Path("/user/uploadPhoto")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Authenticated(UserRole.USER)
    @Transactional
    public ImagesGroup uploadUserPhoto(@FormDataParam("file") final InputStream inputStream) throws IOException,
            ApplicationException {
        return this.userManager.uploadUserPhoto(inputStream);
    }

    @Path("user/image/{userImageId}")
    @GET
    @Produces("*/*")
    @Consumes("*/*")
    @Transactional(readOnly = true)
    public Response userImage(@PathParam("userImageId") final Long userImageId) {
        try {
            File imageFile = this.userManager.getUserImage(userImageId);
            if (imageFile == null) {
                return Response.noContent().build();
            }

            byte[] fileData = Files.toByteArray(imageFile);
            new Magic();
            MagicMatch magicMatch = Magic.getMagicMatch(fileData, true);
            return Response.ok(fileData, magicMatch.getMimeType()).build();
        } catch (Exception e) {
            return Response.noContent().build();
        }
    }

}
