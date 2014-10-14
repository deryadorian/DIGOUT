package com.digout.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.digout.artifact.Address;
import com.digout.artifact.Image;
import com.digout.artifact.ImagesGroup;
import com.digout.artifact.Info;
import com.digout.artifact.Message;
import com.digout.artifact.Messages;
import com.digout.artifact.Notification;
import com.digout.artifact.PasswordChange;
import com.digout.artifact.Tags;
import com.digout.artifact.User;
import com.digout.artifact.UserCountingInformation;
import com.digout.artifact.UserCredentials;
import com.digout.artifact.UserFollow;
import com.digout.artifact.UserFollowing;
import com.digout.artifact.UserFollows;
import com.digout.artifact.UserProfile;
import com.digout.artifact.UserSettings;
import com.digout.artifact.Users;
import com.digout.config.ConfigurationProvider;
import com.digout.converter.UserAddressConverter;
import com.digout.converter.UserFollowConverter;
import com.digout.converter.UserFollowerConverter;
import com.digout.converter.UserMessageConverter;
import com.digout.converter.UserPhotoConverter;
import com.digout.converter.UserProfileConverter;
import com.digout.event.RecoverPasswordEvent;
import com.digout.event.WelcomeEmailEvent;
import com.digout.event.source.RecoverPasswordEventSource;
import com.digout.event.source.WelcomeEmailEventSource;
import com.digout.exception.ApplicationException;
import com.digout.exception.FacebookPermissionException;
import com.digout.exception.InvalidCredentialsException;
import com.digout.exception.RegistrationException;
import com.digout.exception.UserNotFoundException;
import com.digout.model.UserOrigin;
import com.digout.model.UserRole;
import com.digout.model.common.AddressAssignment;
import com.digout.model.common.ImageFormat;
import com.digout.model.common.ProductStatus;
import com.digout.model.entity.common.ImageInfoAccessor;
import com.digout.model.entity.product.ProductTagEntity;
import com.digout.model.entity.user.UserAddressEntity;
import com.digout.model.entity.user.UserConversationEntity;
import com.digout.model.entity.user.UserCredentialsEntity;
import com.digout.model.entity.user.UserEntity;
import com.digout.model.entity.user.UserFollowerEntity;
import com.digout.model.entity.user.UserImageEntity;
import com.digout.model.entity.user.UserImageInfoAccessor;
import com.digout.model.entity.user.UserMessageEntity;
import com.digout.model.entity.user.UserTokenEntity;
import com.digout.repository.FavouriteProductRepository;
import com.digout.repository.ImageRepository;
import com.digout.repository.OrderRepository;
import com.digout.repository.ProductRepository;
import com.digout.repository.ProductTagRepository;
import com.digout.repository.UserAddressRepository;
import com.digout.repository.UserConversationRepository;
import com.digout.repository.UserFollowerRepository;
import com.digout.repository.UserImageRepository;
import com.digout.repository.UserMessageRepository;
import com.digout.repository.UserRepository;
import com.digout.repository.UserSessionRepository;
import com.digout.service.FacebookService;
import com.digout.support.context.MessageContext;
import com.digout.support.context.MessageContextFactory;
import com.digout.support.i18n.I18nMessageSource;
import com.digout.support.mail.BuySellProcessMailer;
import com.digout.utils.FileUtils;
import com.digout.utils.ImageUtils;
import com.digout.utils.PagingUtils;
import com.digout.utils.SecureUtils;
import com.digout.utils.StringsHelper;
import com.digout.validation.Patterns;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.restfb.exception.FacebookException;
import com.restfb.exception.FacebookOAuthException;

public class UsersManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(UsersManager.class);

    private static final int MAX_MESSAGE_SIZE = 50;
    private static final int MAX_USER_FOLLOWS_SIZE = 50;
    private static final int MAX_DEFAULT_SIZE = 50;
    private static final int MAX_ADDRESSES_SIZE = 3;
    private static final int MAX_TAGS_DEFAULT_SIZE = 1000000;

    @Autowired
    private UserSessionRepository userSessionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserSessionHolder userSessionHolder;
    @Autowired
    private UserAddressRepository userAddressRepository;
    @Autowired
    private I18nMessageSource i18n;
    @Autowired
    private UserProfileConverter userProfileConverter;
    @Autowired
    private UserMessageRepository userMessageRepository;
    @Autowired
    private UserMessageConverter userMessageConverter;
    @Autowired
    private UserFollowConverter userFollowConverter;
    @Autowired
    private UserFollowerRepository userFollowerRepository;
    @Autowired
    private ProductTagRepository productTagRepository;
    @Autowired
    private FavouriteProductRepository favouriteProductRepository;
    @Autowired
    private UserConversationRepository userConversationRepository;
    @Autowired
    private UserAddressConverter userAddressConverter;
    @Autowired
    private UserImageRepository userImageRepository;
    @Autowired
    private RequestSessionHolder requestSessionHolder;
    @Autowired
    private UserPhotoConverter userPhotoConverter;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserFollowerConverter userFollowerConverter;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private BuySellProcessMailer buySellProcessMailer;
    @Autowired
    private FacebookService facebookService;
    @Autowired
    private ConfigurationProvider configurationProvider;

    @Transactional
    public Address addAddress(final Address address) throws ApplicationException {
	Long userId = this.userSessionHolder.getUserId();
	if (!(userAddressRepository.countUserAddresses(userId, AddressAssignment.FOR_USER) < MAX_ADDRESSES_SIZE)) {
	    throw new ApplicationException("user.cant.has.more.than.3.addresses");
	}
	UserAddressEntity addressEntity = this.userAddressConverter.createEntity(address);
	addressEntity.setAssignment(AddressAssignment.FOR_USER);
	addressEntity.setUser(this.userSessionHolder.getUser());
	return this.userAddressConverter.createTO(this.userAddressRepository.save(addressEntity));
    }

    private UserCountingInformation addUserPhotoIntoCountingInfo(final UserCountingInformation countingInformation,
	    final ImageFormat imageFormat, final Set<UserImageEntity> images) {
	if (CollectionUtils.isEmpty(images)) {
	    return countingInformation;
	}
	String userImageURL = this.requestSessionHolder.getServerAddress() + "/user/image/";
	for (UserImageEntity imageEntity : images) {
	    if (imageEntity.getFormat() == imageFormat) {
		Image image = fillImage(imageEntity.getId(), userImageURL, imageEntity.getGroup());
		countingInformation.setUserThumbImage(image);
	    }
	}
	return countingInformation;
    }

    @Transactional
    public void changePassword(final PasswordChange passwordChange) throws ApplicationException {
	UserEntity user = this.userSessionHolder.getUser();
	String hashedOldPassword = SecureUtils.hash(passwordChange.getOldPassword());
	if (!hashedOldPassword.equals(user.getUserCredentials().getPassword())) {
	    throw new ApplicationException(this.i18n.getMessage("password.change.invalid.password"));
	}
	user.getUserCredentials().setPassword(SecureUtils.hash(passwordChange.getNewPassword()));
	this.userRepository.save(user);
    }

    private UserMessageEntity convertMessage(final Message message) throws ApplicationException {
	UserMessageEntity userMessageEntity = new UserMessageEntity();
	UserEntity sender = this.userSessionHolder.getUser();// userRepository.findOne(userSessionHolder.getUserId());
	UserEntity receiver = this.userRepository.findOne(message.getReceiverId());
	if (sender.getId().equals(receiver.getId())) {
	    throw new ApplicationException(this.i18n.getMessage("user.can.not.send.message.itself"));
	}
	userMessageEntity.setText(message.getData());
	userMessageEntity.setSentDate(DateTime.now());
	userMessageEntity.setReceiver(receiver);
	userMessageEntity.setSender(sender);
	sender.getSentMessages().add(userMessageEntity);
	receiver.getReceivedMessages().add(userMessageEntity);
	UserConversationEntity userConversationEntity = this.userConversationRepository.find(sender.getId(),
		receiver.getId());
	if (userConversationEntity == null) {
	    userConversationEntity = new UserConversationEntity();
	    userConversationEntity.setUser(sender);
	    userConversationEntity.setInterlocutor(receiver);
	    userConversationEntity.setLastMessage(userMessageEntity);
	    userMessageEntity.setConversation(userConversationEntity);
	} else {
	    userConversationEntity.setLastMessage(userMessageEntity);
	    userMessageEntity.setConversation(userConversationEntity);
	}
	return userMessageEntity;
    }

    @Transactional
    public void deleteAddress(final Long addressId) throws ApplicationException {
	Long userId = this.userSessionHolder.getUserId();
	this.userAddressRepository.delete(addressId, userId);
    }

    @Transactional
    public void deleteDialogMessages(final Long interlocutorId) {
	Long userId = this.userSessionHolder.getUserId();
	this.userMessageRepository.setVisibleDialogByReceiver(true, userId, interlocutorId);
	this.userMessageRepository.setVisibleDialogBySender(true, userId, interlocutorId);
    }

    private void deleteImage(final String dirPath, final Collection<? extends ImageInfoAccessor> collection) {
	for (ImageInfoAccessor infoAccessor : collection) {
	    this.imageRepository.deleteImage(infoAccessor.getId());
	    FileUtils.deleteQuietly(StringsHelper.appendAll(dirPath, infoAccessor.getImagePath()));
	}
    }

    @Transactional
    public void deleteImageByImageId(final Long imageId) throws ApplicationException {
	Long userId = this.userSessionHolder.getUserId();
	List<UserImageEntity> images = this.userImageRepository.findUserImage(imageId, userId);
	if (CollectionUtils.isEmpty(images)) {
	    throw new ApplicationException(this.i18n.getMessage("image.not.exists"));
	}
	String dirPath = StringsHelper.appendAll(configurationProvider.getUserImagesStorePath(), File.separator);
	deleteImage(dirPath, images);
    }

    @Transactional
    public void deleteMessage(final Long messageId) throws ApplicationException {
	UserEntity user = this.userSessionHolder.getUser();
	UserMessageEntity userMessageEntity = this.userMessageRepository.findOne(messageId);
	if (userMessageEntity.getReceiver() == user) {
	    userMessageEntity.setDeletedByReceiver(true);
	} else if (userMessageEntity.getSender() == user) {
	    userMessageEntity.setDeletedBySender(true);
	}
	this.userMessageRepository.save(userMessageEntity);
    }

    @Transactional
    public void deleteUserImage() throws ApplicationException {
	UserEntity user = this.userSessionHolder.getUser();
	Set<UserImageEntity> userImages = user.getImages();
	String dirPath = StringsHelper.appendAll(configurationProvider.getUserImagesStorePath(), File.separator);
	deleteImage(dirPath, userImages);
    }

    private Image fillImage(final Long imageId, final String userImageUrl, final String group) {
	Image image = new Image();
	image.setId(imageId);
	image.setUrl(userImageUrl + imageId);
	return image;
    }

    @Transactional
    public Users findUsersByUserName(String userName, final Integer pageNum, final Integer pageSize)
	    throws ApplicationException {
	if (userName == null || userName.isEmpty()) {
	    throw new ApplicationException(this.i18n.getMessage("search.string.empty"));
	}
	userName = StringsHelper.addPrefixSuffixToString(userName, "%", "%");
	final int offset = PagingUtils.offset(pageNum, pageSize);
	final int limit = PagingUtils.limit(pageSize, MAX_DEFAULT_SIZE);
	Page<UserEntity> users = this.userRepository.findByUserName(userName, new PageRequest(offset, limit));
	if (CollectionUtils.isEmpty(users.getContent())) {
	    return null;
	}
	return getUsersSimpleFromEntity(users);
    }

    @Transactional
    public void follow(final Long followedId) throws ApplicationException {
	Long followerId = this.userSessionHolder.getUserId();

	if (this.userFollowerRepository.isFollowed(followerId, followedId)) {
	    throw new ApplicationException(this.i18n.getMessage("user.already.followed"));
	}
	if (followerId.equals(followedId)) {
	    throw new ApplicationException(this.i18n.getMessage("user.can.not.follow.itself"));
	}
	UserEntity follower = this.userSessionHolder.getUser();// userRepository.findOne(followerId);
	UserEntity followed = this.userRepository.findOne(followedId);
	UserFollowerEntity userFollowerEntity = new UserFollowerEntity();
	userFollowerEntity.setFollower(follower);
	userFollowerEntity.setFollowed(followed);
	if (this.userFollowerRepository.setFollowing(followerId, followedId, true) == 0) {
	    this.userFollowerRepository.save(userFollowerEntity);
	} else {
	    userFollowerEntity.setFollowing(true);
	    this.userFollowerRepository.save(userFollowerEntity);
	}
    }

    private UserEntity fromFacebookUser(final com.restfb.types.User fbUser, final UserEntity userEntity) {
	userEntity.setFullname(fbUser.getFirstName() + " " + fbUser.getLastName());
	UserCredentialsEntity userCredentialsEntity = userEntity.getUserCredentials();
	if (userCredentialsEntity == null) {
	    userCredentialsEntity = new UserCredentialsEntity();
	    userEntity.setUserCredentials(userCredentialsEntity);
	}
	userCredentialsEntity.setUsername(fbUser.getUsername());
	userCredentialsEntity.setEmail(fbUser.getEmail());
	userCredentialsEntity.setPassword(SecureUtils.hash(configurationProvider.getFacebookPassword()));
	userEntity.setExternalId(fbUser.getId());
	return userEntity;
    }

    @Transactional(readOnly = true)
    public Tags getAllTags(final Integer pageNum, final Integer pageSize) {
    //default size for getting tags 1 000 000
	final int limit = PagingUtils.limit(pageSize, MAX_TAGS_DEFAULT_SIZE);
	final int offset = PagingUtils.offset(pageNum, pageSize);
	return getTagsFromEntity(this.productTagRepository.findAll(new PageRequest(offset, limit)));
    }

    private Messages getDialog(final Long userId, final Long interlocutorId, final Pageable pageable)
	    throws ApplicationException {
	this.userMessageRepository.markAllMessagesAsRead(userId, interlocutorId);
	Page<UserMessageEntity> userMessages = this.userMessageRepository.getDialogMessages(userId, interlocutorId,
		pageable);
	return getMessagesFromEntities(userMessages);
    }

    @Transactional
    public UserFollowing getFollowed(final Integer pageNum, final Integer pageSize) {
	UserEntity userEntity = userSessionHolder.getUser();
	Long userId = userEntity.getId();
	final int limit = PagingUtils.limit(pageSize, MAX_USER_FOLLOWS_SIZE);
	final int offset = PagingUtils.offset(pageNum, pageSize);
	Page<UserFollowerEntity> followed = this.userFollowerRepository.getFollowed(userId, new PageRequest(offset,
		limit));
	return createTOsConsiderCurrentUser(userEntity, followed.getContent(), true, null);
    }

    @Transactional
    public UserFollowing getFollowed(final Long followerId, final Integer pageNum, final Integer pageSize) {
	UserEntity userEntity = this.userSessionHolder.getUser();
	UserEntity followerUserEntity = this.userRepository.findOne(followerId);
	final int limit = PagingUtils.limit(pageSize, MAX_USER_FOLLOWS_SIZE);
	final int offset = PagingUtils.offset(pageNum, pageSize);
	Page<UserFollowerEntity> followed = this.userFollowerRepository.getFollowed(followerId, new PageRequest(offset,
		limit));
	return createTOsConsiderCurrentUser(followerUserEntity, followed.getContent(), true, userEntity.getId());
    }

    private UserFollowing createTOsConsiderCurrentUser(UserEntity userEntity,
	    Collection<UserFollowerEntity> followerEntitySet, final boolean isGettingFollowed, final Long currentUserId) {
	UserFollowing userFollowing = new UserFollowing();
	if (isGettingFollowed) {
	    userFollowing.getFollowers().add(userFollowerConverter.createTO(userEntity));
	    for (UserFollowerEntity f : followerEntitySet) {
		userFollowing.getFolloweds().add(
			userFollowerConverter.createTO(
				f.getFollowed(),
				currentUserId != null ? userFollowerRepository.isFollowed(currentUserId, f
					.getFollowed().getId()) : true));
	    }
	} else {
	    userFollowing.getFolloweds().add(userFollowerConverter.createTO(userEntity));
	    for (UserFollowerEntity f : followerEntitySet) {
		userFollowing.getFollowers().add(
			userFollowerConverter.createTO(
				f.getFollower(),
				currentUserId != null ? userFollowerRepository.isFollowed(currentUserId, f
					.getFollower().getId()) : f.isFollowing()));
	    }
	}
	return userFollowing;
    }

    @Transactional
    public UserFollowing getFollowers(final Integer pageNum, final Integer pageSize) {
	Long userId = this.userSessionHolder.getUserId();
	final int limit = PagingUtils.limit(pageSize, MAX_USER_FOLLOWS_SIZE);
	final int offset = PagingUtils.offset(pageNum, pageSize);
	Page<UserFollowerEntity> followers = this.userFollowerRepository.getFollowers(userId, new PageRequest(offset,
		limit));
	return createTOsConsiderCurrentUser(this.userSessionHolder.getUser(), followers.getContent(), false, null);
    }

    @Transactional
    public UserFollowing getFollowers(final Long followedId, final Integer pageNum, final Integer pageSize) {
	UserEntity user = this.userSessionHolder.getUser();
	UserEntity followedUserEntity = this.userRepository.findOne(followedId);
	final int limit = PagingUtils.limit(pageSize, MAX_USER_FOLLOWS_SIZE);
	final int offset = PagingUtils.offset(pageNum, pageSize);
	Page<UserFollowerEntity> followers = this.userFollowerRepository.getFollowers(followedId, new PageRequest(
		offset, limit));
	return createTOsConsiderCurrentUser(followedUserEntity, followers.getContent(), false, user.getId());
    }

    private ImagesGroup getImagesGroup(final Map<ImageFormat, ImageInfoAccessor> imageIds) {
	ImagesGroup imagesGroup = new ImagesGroup();
	final String productImgUrl = this.requestSessionHolder.getServerAddress() + "/user/image/";

	ImageInfoAccessor image = imageIds.get(ImageFormat.ORIGINAL);
	if (image != null) {
	    imagesGroup.setOriginalImage(fillImage(image.getId(), productImgUrl, image.getGroup()));
	}

	image = imageIds.get(ImageFormat.STANDARD);
	if (image != null) {
	    imagesGroup.setStandardImage(fillImage(image.getId(), productImgUrl, image.getGroup()));
	}

	image = imageIds.get(ImageFormat.THUMB);
	if (image != null) {
	    imagesGroup.setThumbImage(fillImage(image.getId(), productImgUrl, image.getGroup()));
	}

	return imagesGroup;
    }

    private List<String> getListFromEntity(final Page<ProductTagEntity> entities) {
	List<String> list = new ArrayList<String>();
	for (ProductTagEntity p : entities) {
	    list.add(p.getTag());
	}
	return list;
    }

    @Transactional
    public Messages getMainMessages(final Integer pageNum, final Integer pageSize) throws ApplicationException {
	final int offset = PagingUtils.offset(pageNum, pageSize);
	final int limit = PagingUtils.limit(pageSize, MAX_MESSAGE_SIZE);
	return getMessagesFromEntities(this.userConversationRepository.getConversations(
		this.userSessionHolder.getUserId(), new PageRequest(offset, limit)));
    }

    @Transactional
    public Messages getMessagesDialog(final Long interlocutorId, final Integer pageNum, final Integer pageSize)
	    throws ApplicationException {
	Long userId = this.userSessionHolder.getUserId();
	final int offset = PagingUtils.offset(pageNum, pageSize);
	final int limit = PagingUtils.limit(pageSize, MAX_DEFAULT_SIZE);
	return getDialog(userId, interlocutorId, new PageRequest(offset, limit));
    }

    private Messages getMessagesFromEntities(final Page<UserMessageEntity> entityPage) throws ApplicationException {
	List<Message> list = this.userMessageConverter.createTOList(entityPage.getContent());
	Messages messages = null;
	if (!CollectionUtils.isEmpty(list)) {
	    messages = new Messages();
	    messages.getMessages().addAll(list);
	    messages.setPaging(PagingUtils.pageOf(entityPage));
	}
	return messages;
    }

    private Tags getTagsFromEntity(final Page<ProductTagEntity> entity) {
	List<String> list = getListFromEntity(entity);
	Tags tags = null;
	if (!CollectionUtils.isEmpty(list)) {
	    tags = new Tags();
	    tags.getTags().addAll(list);
	    tags.setPaging(PagingUtils.pageOf(entity));
	}
	return tags;
    }

    private UserCountingInformation getUserCountingInfo(final Long userId, final boolean isCurrentUser) {
	UserCountingInformation counting = new UserCountingInformation();
	Long currentUser = this.userSessionHolder.getUserId();
	if (isCurrentUser) {
	    counting.setFollowers(this.userFollowerRepository.countFollowers(userId));
	    counting.setFollowings(this.userFollowerRepository.countFollowed(userId));
	    counting.setUnreadMessages(this.userMessageRepository.countUnreadMessages(userId));
	    counting.setSelling(this.orderRepository.countSelling(userId));
	    counting.setSold(this.productRepository.countProductsByStatus(userId, ProductStatus.SHIPPING));
	    counting.setOrders(this.orderRepository.countOrders(userId));
	} else {
	    counting.setFollowers(this.userFollowerRepository.countFollowersConsiderCurrentUser(userId, currentUser));
	    counting.setFollowings(this.userFollowerRepository.countFollowedConsiderCurrentUser(userId, currentUser));
	    // todo: remove counting messages another user, and notify client
	    // side
	    counting.setUnreadMessages(this.userMessageRepository.countUnreadMessages(userId));
	    counting.setSelling(this.orderRepository.countSelling(userId));
	    counting.setSold(this.productRepository.countProductsByStatus(userId, ProductStatus.SOLD));
	}
	counting.setShortlistedProducts(this.favouriteProductRepository.countShortlistedProductsByUser(userId));
	return counting;
    }

    @Transactional
    public UserCountingInformation getUserCountingInformation() {
	UserEntity user = this.userSessionHolder.getUser();
	UserCountingInformation countingInformation = getUserCountingInfo(user.getId(), true);
	countingInformation = addUserPhotoIntoCountingInfo(countingInformation, ImageFormat.THUMB, user.getImages());
	return countingInformation;
    }

    @Transactional
    public File getUserImage(final Long userImageId) {
	UserImageEntity userImageEntity = this.userImageRepository.findOne(userImageId);
	if (userImageEntity == null) {
	    return null;
	}
	String path = StringsHelper.appendAll(configurationProvider.getUserImagesStorePath(), File.separator,
		userImageEntity.getImagePath());
	return new File(path);
    }

    @Transactional(readOnly = true)
    public UserSettings getUserNotificationSettings() {
	UserEntity user = this.userSessionHolder.getUser();// userRepository.findOne(userSessionHolder.getUserId());
	UserSettings userSettings = new UserSettings();
	userSettings.setNotifications(syncNotification(user));
	return userSettings;
    }

    @Transactional
    public UserProfile getUserPhoneNumber(final Long userId) throws UserNotFoundException {
	UserEntity user = this.userRepository.findOne(userId);
	if (user == null) {
	    throw new UserNotFoundException(this.i18n.getMessage("user.not.found"));
	}
	String phoneNumber = user.getMobileNumber();
	UserProfile userProfile = new UserProfile();
	userProfile.setPhone(phoneNumber);
	return userProfile;
    }

    @Transactional(readOnly = true)
    public UserProfile getUserProfile() {
	UserEntity userEntity = this.userSessionHolder.getUser();
	UserProfile user = this.userProfileConverter.createTO(userEntity);
	user.setIban(userEntity.getIban());
	user.setUserCountingInfo(getUserCountingInfo(user.getId(), true));
	return user;
    }

    @Transactional(readOnly = true)
    public UserProfile getUserProfile(final Long userId) throws UserNotFoundException {
	UserEntity userEntity = this.userRepository.findOne(userId);
	if (userEntity == null) {
	    throw new UserNotFoundException(this.i18n.getMessage("user.not.found"));
	}
	UserProfile user = this.userProfileConverter.createTO(userEntity);
	user.setFollowed(this.userFollowerRepository.isFollowed(this.userSessionHolder.getUserId(), userId));
	user.setUserCountingInfo(getUserCountingInfo(user.getId(), true));
	return user;
    }

    // todo: refactor using converter class
    private Users getUsersSimpleFromEntity(final Page<UserEntity> entities) {
	Users users = new Users();
	for (UserEntity entity : entities) {
	    User user = new User();
	    user.setUserId(entity.getId());
	    UserCredentials credentials = new UserCredentials();
	    credentials.setUsername(entity.getUserCredentials().getUsername());
	    user.setCredentials(credentials);
	    user.setUserThumbImage(this.userPhotoConverter.convertUserImageEntity(entity.getImages(), ImageFormat.THUMB));
	    users.getUsers().add(user);
	}
	users.setPaging(PagingUtils.pageOf(entities));
	return users;
    }

    // todo:refactor
    // use only for saveProfile(considering address assignment)
    private boolean isSyncUserAddress(final Address address, UserAddressEntity userAddressEntity) {
	if (address.isSetCity()) {
	    userAddressEntity.setCity(address.getCity());
	}
	if (address.isSetDescription()) {
	    userAddressEntity.setAddressDefinition(address.getDescription());
	}
	if (address.isSetLine()) {
	    userAddressEntity.setAddressLine(address.getLine());
	}
	if (address.isSetPostCode()) {
	    userAddressEntity.setPostalCode(address.getPostCode());
	}
	if (address.isSetRegion()) {
	    userAddressEntity.setRegion(address.getRegion());
	}
	if (address.isSetLatitude()) {
	    userAddressEntity.setLatitude(address.getLatitude());
	}
	if (address.isSetLongitude()) {
	    userAddressEntity.setLongitude(address.getLongitude());
	}
	if (address.isSetDistrict()) {
	    userAddressEntity.setDistrict(address.getDistrict());
	}

	return (address.isSetCity() || address.isSetDescription() || address.isSetLine() || address.isSetPostCode()
		|| address.isSetRegion() || address.isSetLatitude() || address.isSetLongitude() || address
		    .isSetDistrict());
    }

    private UserAddressEntity syncAddress(final UserAddressEntity userAddressEntity, final Address address) {
	boolean isChanged = false;
	if (address.isSetCity()) {
	    userAddressEntity.setCity(address.getCity());
	    isChanged = true;
	}
	if (address.isSetDescription()) {
	    userAddressEntity.setAddressDefinition(address.getDescription());
	    isChanged = true;
	}
	if (address.isSetLine()) {
	    userAddressEntity.setAddressLine(address.getLine());
	    isChanged = true;
	}
	if (address.isSetPostCode()) {
	    userAddressEntity.setPostalCode(address.getPostCode());
	    isChanged = true;
	}
	if (address.isSetRegion()) {
	    userAddressEntity.setRegion(address.getRegion());
	    isChanged = true;
	}
	if (address.isSetLatitude()) {
	    userAddressEntity.setLatitude(address.getLatitude());
	    isChanged = true;
	}
	if (address.isSetLongitude()) {
	    userAddressEntity.setLongitude(address.getLongitude());
	    isChanged = true;
	}
	if (address.isSetDistrict()) {
	    userAddressEntity.setDistrict(address.getDistrict());
	    isChanged = true;
	}
	userAddressEntity.setChanged(isChanged);
	return userAddressEntity;
    }

    private boolean isSyncUserBaseProfile(final UserProfile userProfile, final UserEntity userEntity) {
	if (userProfile.isSetFullname() && userEntity.getOrigin() == UserOrigin.LOCAL) {
	    userEntity.setFullname(userProfile.getFullname());
	}
	if (userProfile.isSetPhone()) {
	    userEntity.setMobileNumber(userProfile.getPhone());
	}
	if (userProfile.isSetIban()) {
	    userEntity.setIban(userProfile.getIban());
	}
	return (userProfile.isSetFullname() || userProfile.isSetPhone());
    }

    @Transactional
    public void recoverPasswordByEmail(final String email) throws UserNotFoundException {
	UserEntity user = this.userRepository.findByEmail(email, false, UserOrigin.LOCAL);
	if (user == null) {
	    throw new UserNotFoundException(this.i18n.getMessage("password.by.email.recover.such.email.not.exists"));
	}
	final String tempPassword = SecureUtils.generateRandomSequence(Patterns.PASSWORD_COMPLEXITY);
	user.getUserCredentials().setPassword(SecureUtils.hash(tempPassword));
	this.userRepository.save(user);
	final RecoverPasswordEventSource source = new RecoverPasswordEventSource.Builder()
		.addEmail(user.getUserCredentials().getEmail()).addFullname(user.getFullname())
		.addGenPassword(tempPassword).build();
	this.applicationContext.publishEvent(new RecoverPasswordEvent(source));
    }

    private UserEntity registerFacebookUser(final com.restfb.types.User fbUser) throws RegistrationException {
	UserEntity user = fromFacebookUser(fbUser, new UserEntity());
	registerInternal(user, UserOrigin.FACEBOOK, false);
	return user;
    }

    private void registerInternal(final UserEntity user, final UserOrigin userOrigin, final boolean isSystemUser)
	    throws RegistrationException {
	UserCredentialsEntity userCred = user.getUserCredentials();

	boolean userExists = this.userRepository.isUserExistsByUsernameOrEmail(userCred.getUsername(),
		userCred.getEmail(), isSystemUser, userOrigin);
	if (userExists) {
	    throw new RegistrationException(this.i18n.getMessage("login.or.email.exists"));
	}

	user.getUserCredentials().setPassword(SecureUtils.hash(user.getUserCredentials().getPassword()));
	user.setRole(UserRole.USER);
	user.setOrigin(userOrigin);
	this.userRepository.save(user);
	if (UserOrigin.LOCAL == userOrigin && !isSystemUser) {
	    final WelcomeEmailEventSource source = new WelcomeEmailEventSource.Builder().addEmail(userCred.getEmail())
		    .addUsername(userCred.getUsername()).build();
	    this.applicationContext.publishEvent(new WelcomeEmailEvent(source));
	}
    }

    @Transactional
    public void regsiterLocalUser(final UserEntity user) throws RegistrationException {
	user.setLocale(this.requestSessionHolder.getLocale());
	registerInternal(user, UserOrigin.LOCAL, false);
    }

    private boolean resolveAddress(final UserEntity userEntity, final Set<UserAddressEntity> userAddressEntities,
	    final Address userAddress) {
	UserAddressEntity userAddrEntity = Iterables.find(userAddressEntities, new Predicate<UserAddressEntity>() {
	    @Override
	    public boolean apply(@Nullable final UserAddressEntity input) {
		return input.getId().equals(userAddress.getId());
	    }
	});

	boolean isNewAddress = (userAddrEntity == null);
	if (isNewAddress) {
	    userAddrEntity = new UserAddressEntity();
	    userEntity.getAddresses().add(userAddrEntity);
	}

	return isSyncUserAddress(userAddress, userAddrEntity);
    }

    @Transactional
    public Info saveProfile(final UserProfile userProfile) {
	boolean isSyncUserProfile;
	boolean isSyncUserAddresses = false;
	List<Address> addresses = null;
	// todo: normal structure for addresses in userProfileTo
	if (userProfile.isSetAddresses()) {
	    addresses = userProfile.getAddresses().getAddresses();
	}

	UserEntity userEntity = this.userSessionHolder.getUser();

	isSyncUserProfile = isSyncUserBaseProfile(userProfile, userEntity);

	if (!CollectionUtils.isEmpty(addresses)) {
	    for (Address userAddress : addresses) {
		if (!userAddress.isSetId()) {
		    UserAddressEntity address = new UserAddressEntity();
		    if (isSyncUserAddress(userAddress, address)) {
			address.setUser(userEntity);
			address.setAssignment(AddressAssignment.FOR_USER);
			userEntity.getAddresses().add(address);
			isSyncUserAddresses = true;
		    }
		} else {
		    UserAddressEntity address = this.userAddressRepository.findOne(userAddress.getId());
		    address = syncAddress(address, userAddress);
		    isSyncUserAddresses = isSyncUserAddresses || address.isChanged();
		}
	    }
	}
	if (isSyncUserProfile || isSyncUserAddresses) {
	    this.userRepository.save(userEntity);
	}
	Info info = new Info();
	info.getMessages().add(this.i18n.getMessage("profile.save.status"));
	return info;
    }

    private UserImageInfoAccessor saveUserImageInfo(final UserEntity user, final String imageFilePath,
	    final ImageFormat imageFormat, final String group) {
	UserImageEntity userImageEntity = new UserImageEntity();
	userImageEntity.setUser(user);
	userImageEntity.setFormat(imageFormat);
	userImageEntity.setImagePath(imageFilePath);
	userImageEntity.setGroup(group);
	return this.userImageRepository.save(userImageEntity);
    }

    @Transactional
    public void sendMessage(final Message message) throws ApplicationException {
	UserMessageEntity userMessageEntity = convertMessage(message);
	this.userMessageRepository.save(userMessageEntity);
    }

    @Transactional(readOnly = true)
    public void setUserNotificationSettings(final UserSettings userSettings) {
	Notification notification = userSettings.getNotifications();
	if (notification != null) {
	    UserEntity user = this.userSessionHolder.getUser();// userRepository.findOne(userSessionHolder.getUserId());
	    if (notification.isSetFollowing()) {
		user.setFollowingNotification(notification.isFollowing());
	    }
	    if (notification.isSetFriends()) {
		user.setFriendsNotification(notification.isFriends());
	    }
	    if (notification.isSetShortlists()) {
		user.setShortlistsNotification(notification.isShortlists());
	    }
	    this.userRepository.save(user);
	}
    }

    @Transactional
    public UserTokenEntity signInFacebook(final String facebookAccessToken) throws ApplicationException {
	try {
	    com.restfb.types.User fbUser = this.facebookService.getUser(facebookAccessToken);
	    if (fbUser != null) {
		final MessageContext messageContext = MessageContextFactory.newContext();
		if (fbUser.getEmail() == null) {
		    messageContext.addMessage(this.i18n.getMessage("fb.email.permission.is.protected"));
		}
		if (!messageContext.isEmpty()) {
		    throw new FacebookPermissionException(messageContext);
		}
		UserEntity userEntity = this.userRepository.getUserByExternalId(fbUser.getId(), UserOrigin.FACEBOOK);
		if (userEntity == null) {
		    userEntity = registerFacebookUser(fbUser);
		} else {
		    userEntity = fromFacebookUser(fbUser, userEntity);
		}
		this.userRepository.save(userEntity);

//		final InputStream fbUserPicture = this.facebookService.getUserPicture(fbUser.getId());
//		if (fbUserPicture != null) {
//		    uploadUserPhoto(userEntity, fbUserPicture);
//		}
		final UserTokenEntity userTokenEntity = signInInternalUser(fbUser.getEmail(),
			configurationProvider.getFacebookPassword(), UserOrigin.FACEBOOK, false);

		return userTokenEntity;
	    }
	    throw new InvalidCredentialsException(this.i18n.getMessage("user.creden.invalid"));
	} catch (FacebookOAuthException e) {
	    LOGGER.debug("Facebook Authentication didn't succeed", e);
	    throw new InvalidCredentialsException(this.i18n.getMessage("user.creden.invalid"));
	} catch (FacebookException e) {
	    LOGGER.debug("Facebook interacting error", e);
	    throw e;
	} 
//	catch (IOException e) {
//	    LOGGER.debug("Failed uploading Facebook user picture", e);
//	    throw new RuntimeException(e);
//	}
    }

    @Transactional
    public UserTokenEntity signInGuest() throws InvalidCredentialsException {
	return signInInternalUser(configurationProvider.getGuestLogin(), configurationProvider.getGuestPassword(),
		UserOrigin.LOCAL, true);
    }

    private UserTokenEntity signInInternalUser(final String username, final String password,
	    final UserOrigin userOrigin, final boolean isSystemUser) throws InvalidCredentialsException {
	UserEntity user = this.userRepository.findByUsernameOrEmailAndPassword(username, SecureUtils.hash(password),
		isSystemUser, userOrigin);
	if (user == null) {
	    throw new InvalidCredentialsException(this.i18n.getMessage("user.login.or.password.invalid"));
	}
	user.setLocale(requestSessionHolder.getLocale());

	UserTokenEntity userToken = new UserTokenEntity();
	userToken.setUser(user);
	userToken.setTokenId(SecureUtils.generateSecureId());
	userToken.setExpireTime(userToken.getLastActionTime().plusMinutes(
		configurationProvider.getUserSessionTimeoutMins()));
	return this.userSessionRepository.save(userToken);
    }

    @Transactional
    public UserTokenEntity signInUser(final String username, final String password) throws InvalidCredentialsException {
	return signInInternalUser(username, password, UserOrigin.LOCAL, false);
    }

    private Notification syncNotification(final UserEntity userEntity) {
	Notification notification = new Notification();
	notification.setFollowing(userEntity.isFollowingNotification());
	notification.setFriends(userEntity.isFriendsNotification());
	notification.setShortlists(userEntity.isShortlistsNotification());
	return notification;
    }

    @Transactional
    public void unfollow(final Long followedId) throws ApplicationException {
	Long followerId = this.userSessionHolder.getUserId();
	if (!this.userFollowerRepository.isFollowed(followerId, followedId)) {
	    throw new ApplicationException(this.i18n.getMessage("user.unfollowed"));
	}
	this.userFollowerRepository.setFollowing(followerId, followedId, false);
	this.userFollowerRepository.unfollow(followerId, followedId);
    }

    @Transactional
    public ImagesGroup uploadUserPhoto(final InputStream inputStream) throws IOException, ApplicationException {
	final UserEntity user = this.userSessionHolder.getUser();
	if (user.getOrigin() != UserOrigin.LOCAL) {
	    throw new ApplicationException(i18n.getMessage("facebook.user.has.no.permission"));
	}
	return uploadUserPhoto(user, inputStream);
    }

    @Transactional
    public ImagesGroup uploadUserPhoto(final UserEntity userEntity, final InputStream inputStream) throws IOException,
	    ApplicationException {
	Set<UserImageEntity> images = userEntity.getImages();
	if (images != null && !images.isEmpty()) {
	    deleteImage(StringsHelper.appendAll(configurationProvider.getUserImagesStorePath(), File.separator), images);
	}
	final Map<ImageFormat, ImageInfoAccessor> userImages = new EnumMap<ImageFormat, ImageInfoAccessor>(
		ImageFormat.class);
	byte[] bytes = IOUtils.toByteArray(inputStream);
	try {
	    String dateFolder = DateTime.now().toString("ddMMyyyy");
	    String imageGroupId = SecureUtils.generateSecureId();
	    for (ImageFormat imageFormat : ImageFormat.values()) {
		String fileName = FileUtils.generateUniqueFileName("");
		String relativeDir = StringsHelper.appendAll(dateFolder, File.separator, imageFormat.name());
		String fileDir = StringsHelper.appendAll(configurationProvider.getUserImagesStorePath(),
			File.separator, relativeDir);
		OutputStream outputStream = ImageUtils.resizeTo(bytes, fileDir, fileName, imageFormat);
		outputStream.flush();
		IOUtils.closeQuietly(outputStream);
		UserImageInfoAccessor imageEntity = saveUserImageInfo(userEntity,
			StringsHelper.appendAll(relativeDir, File.separator, fileName), imageFormat, imageGroupId);
		userImages.put(imageFormat, imageEntity);
	    }
	} catch (Exception e) {
	    LOGGER.debug("Upload Failed", e);	
	    throw new ApplicationException(this.i18n.getMessage("file.upload.failed"), e);
	} finally {
	    bytes = null;
	    IOUtils.closeQuietly(inputStream);
	}
	return getImagesGroup(userImages);

    }

}
