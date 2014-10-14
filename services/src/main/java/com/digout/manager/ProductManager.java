package com.digout.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.digout.artifact.Address;
import com.digout.artifact.Comment;
import com.digout.artifact.Comments;
import com.digout.artifact.Image;
import com.digout.artifact.ImagesGroup;
import com.digout.artifact.Issue;
import com.digout.artifact.Order;
import com.digout.artifact.OrderDetail;
import com.digout.artifact.Orders;
import com.digout.artifact.Product;
import com.digout.artifact.ProductCountingInformation;
import com.digout.artifact.Products;
import com.digout.config.ConfigurationProvider;
import com.digout.converter.OrderConverter;
import com.digout.converter.ProductCommentConverter;
import com.digout.converter.ProductConverter;
import com.digout.converter.ProductOrderConverter;
import com.digout.converter.ProductSearchConverter;
import com.digout.converter.ProductSoldConverter;
import com.digout.converter.ProductUpdateConverter;
import com.digout.converter.SimpleConverterFactory;
import com.digout.converter.UserAddressConverter;
import com.digout.event.ApprovalEmailEvent;
import com.digout.event.InappropriateProductEmailEvent;
import com.digout.event.IssueEmailEvent;
import com.digout.event.OrderSoldEmailEvent;
import com.digout.event.source.ApprovalEmailEventSource;
import com.digout.event.source.InappropriateProductEmailSource;
import com.digout.event.source.IssueEmailSource;
import com.digout.event.source.OrderSoldEmailSource;
import com.digout.exception.ApplicationException;
import com.digout.exception.ProductNotExistsException;
import com.digout.exception.ValidationException;
import com.digout.model.common.AddressAssignment;
import com.digout.model.common.ImageFormat;
import com.digout.model.common.IssueType;
import com.digout.model.common.ProductStatus;
import com.digout.model.common.SellType;
import com.digout.model.entity.common.BankInfoEntity;
import com.digout.model.entity.common.BankTransactionFailEntity;
import com.digout.model.entity.common.ImageEntity;
import com.digout.model.entity.common.ImageInfoAccessor;
import com.digout.model.entity.product.FavouriteProductEntity;
import com.digout.model.entity.product.ProductCommentEntity;
import com.digout.model.entity.product.ProductEntity;
import com.digout.model.entity.product.ProductImageInfoAccessor;
import com.digout.model.entity.product.ProductMainImageEntity;
import com.digout.model.entity.product.ProductSecondaryImageEntity;
import com.digout.model.entity.product.ProductTagEntity;
import com.digout.model.entity.user.IssueEntity;
import com.digout.model.entity.user.UserAddressEntity;
import com.digout.model.entity.user.UserEntity;
import com.digout.model.entity.user.UserOrderEntity;
import com.digout.repository.BankInfoRepository;
import com.digout.repository.BankTransactionFailRepository;
import com.digout.repository.FavouriteProductRepository;
import com.digout.repository.ImageRepository;
import com.digout.repository.IssueRepository;
import com.digout.repository.OrderRepository;
import com.digout.repository.ProductCommentRepository;
import com.digout.repository.ProductMainImageRepository;
import com.digout.repository.ProductRepository;
import com.digout.repository.ProductSecondaryImageRepository;
import com.digout.repository.ProductTagRepository;
import com.digout.repository.UserAddressRepository;
import com.digout.repository.UserRepository;
import com.digout.support.context.MessageContext;
import com.digout.support.context.MessageContextFactory;
import com.digout.support.i18n.I18nMessageSource;
import com.digout.support.mail.BuySellProcessMailer;
import com.digout.utils.BankUtils;
import com.digout.utils.FileUtils;
import com.digout.utils.HttpsUtils;
import com.digout.utils.ImageUtils;
import com.digout.utils.PagingUtils;
import com.digout.utils.SecureUtils;
import com.digout.utils.StringsHelper;
import com.digout.utils.XMLParser;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class ProductManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductManager.class);

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserSessionHolder userSessionHolder;
    @Autowired
    private FavouriteProductRepository favouriteProductRepository;
    @Autowired
    private ProductMainImageRepository productMainImageRepository;
    @Autowired
    private ProductSecondaryImageRepository productSecondaryImageRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private I18nMessageSource i18n;
    @Autowired
    private ProductCommentRepository productCommentRepository;
    @Autowired
    private ProductCommentConverter productCommentConverter;
    @Autowired
    private ProductTagRepository productTagRepository;
    @Autowired
    private ProductConverter productConverter;
    @Autowired
    private ProductUpdateConverter productUpdateConverter;
    @Autowired
    private ProductSearchConverter productSearchConverter;
    @Autowired
    private UserAddressRepository userAddressRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserAddressConverter userAddressConverter;
    @Autowired
    private ProductOrderConverter productOrderConverter;
    @Autowired
    private OrderConverter orderConverter;
    @Autowired
    private ProductSoldConverter productSoldConverter;
    @Autowired
    private RequestSessionHolder requestSessionHolder;
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private BuySellProcessMailer buySellProcessMailer;
    @Autowired
    private BankInfoRepository bankInfoRepository;
    @Autowired
    private BankTransactionFailRepository bankTransactionFailRepository;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ConfigurationProvider configurationProvider;

    // TODO: externalize the value using spring
    private static final Integer MAX_PRODUCTS_SIZE = 50;

    private static final Integer MAX_DEFAULT_SIZE = 50;

    private static final Integer MAX_ADDRESSES_SIZE = 3;

    @Transactional
    public void addComment(final Comment comment) {
        ProductCommentEntity productCommentEntity = convertComment(comment);
        this.productCommentRepository.save(productCommentEntity);
    }

    @Transactional
    public void addProduct2Favourites(final Long productId) throws ApplicationException {
        if (this.favouriteProductRepository.isProductInShortlist(this.userSessionHolder.getUserId(), productId)) {
            throw new ApplicationException(this.i18n.getMessage("product.already.in.shortlist"));
        }
        ProductEntity product = this.productRepository.findOne(productId);
        if (product == null) {
            throw new ProductNotExistsException(this.i18n.getMessage("product.not.exists"));
        }
        FavouriteProductEntity favouriteProduct = new FavouriteProductEntity();
        favouriteProduct.setProduct(product);
        UserEntity user = this.userSessionHolder.getUser();// userRepository.findOne(userSessionHolder.getUserId());
        favouriteProduct.setOwner(user);
        this.favouriteProductRepository.save(favouriteProduct);
    }

    private Products addProductCountingInfo(final Products products) {
        if (products != null && !CollectionUtils.isEmpty(products.getProducts())) {
            for (Product p : products.getProducts()) {
                p.setProductCountingInfo(getProductCountingInfo(p.getProductId()));
            }
        }
        return products;
    }

    @Transactional
    public void approvePurchase(final Long orderId) throws ApplicationException {
        UserOrderEntity orderEntity = this.orderRepository.findOne(orderId);
        if (orderEntity == null) {
            throw new ApplicationException(this.i18n.getMessage("order.not.found"));
        }
        ProductEntity productEntity = orderEntity.getProduct();
        UserEntity user = this.userSessionHolder.getUser();
        if (productEntity.getStatus() == ProductStatus.SHIPPING || user.getId().equals(orderEntity.getBuyer().getId())) {
            productEntity.setStatus(ProductStatus.APPROVED);
            DateTime dateTime = new DateTime();
            productEntity.setSoldDate(dateTime);
            this.orderRepository.save(orderEntity);

            final UserEntity buyer = orderEntity.getBuyer();
            final UserEntity seller = orderEntity.getSeller();
            final ApprovalEmailEventSource source = ApprovalEmailEventSource.builder().buyerName(buyer.getFullname())
                    .buyerEmail(buyer.getUserCredentials().getEmail()).buyerMobile(buyer.getMobileNumber())
                    .sellerName(seller.getFullname()).sellerEmail(seller.getUserCredentials().getEmail())
                    .sellerIban(seller.getIban()).productName(orderEntity.getProduct().getName())
                    .price(orderEntity.getProduct().getPrice()).approvalTime(dateTime).build();

            this.applicationContext.publishEvent(new ApprovalEmailEvent(source));
        } else if (productEntity.getStatus() == ProductStatus.CANCELED
                || user.getId().equals(orderEntity.getSeller().getId())) {
            productEntity.setStatus(ProductStatus.NEW);
            this.orderRepository.save(orderEntity);
        } else {
            throw new ApplicationException(this.i18n.getMessage("user.can.not.approve.purchase"));
        }
    }

    /*
     * @Transactional public ProductEntity publishProduct(Product product){ productTags(product);
     * return null; }
     */
    private ProductEntity commonSaveUpdateProduct(final Product product) throws ProductNotExistsException,
            ValidationException {
        ProductEntity productEntity = product.isSetProductId() ? updateProduct(product) : this.productConverter
                .createEntity(product);
        if (product.isSetTags()) {
            productEntity.setTags(productTags(product));
        }
        if (product.isSetAddress()) {
            Address address = product.getAddress();
            UserAddressEntity addressEntity = null;
            if (!address.isSetId()) {
                addressEntity = this.userAddressConverter.createEntity(product.getAddress());
            } else {
                addressEntity = this.userAddressRepository.findOne(address.getId());
                if (addressEntity.getUser() != null) {
                    addressEntity = addressEntity.cloneEntity();
                }
                addressEntity = syncAddress(addressEntity, address);
            }
            addressEntity.setUser(null);
            addressEntity.setAssignment(AddressAssignment.FOR_PRODUCT);
            productEntity.setAddress(addressEntity);
        }
        return productEntity;
    }

    private ProductCommentEntity convertComment(final Comment comment) {
        ProductEntity productEntity = this.productRepository.findOne(comment.getProductId());
        ProductCommentEntity commentEntity = new ProductCommentEntity();
        UserEntity userEntity = this.userSessionHolder.getUser();// userRepository.findOne(userSessionHolder.getUserId());

        commentEntity.setComment(comment.getText());
        commentEntity.setPostedBy(userEntity);
        commentEntity.setProduct(productEntity);
        commentEntity.setPublishedDate(DateTime.now());

        productEntity.getComments().add(commentEntity);
        userEntity.getProductComments().add(commentEntity);
        return commentEntity;
    }

    @Transactional
    private UserAddressEntity convertOrderAddress(final Address address, final UserEntity buyer) {
        UserAddressEntity userAddressEntity = null;
        if (address.isSetId()) {
            userAddressEntity = this.userAddressRepository.findOne(address.getId());
        } else {
            userAddressEntity = this.userAddressRepository.save(this.userAddressConverter.createEntity(address));
            userAddressEntity.setUser(buyer);
        }
        return userAddressEntity;
    }

    private Orders convertOrders(final Page<UserOrderEntity> orderEntities) {
        List<OrderDetail> orderDetailList = this.orderConverter.createTOList(orderEntities.getContent());
        Orders orders = null;
        if (!CollectionUtils.isEmpty(orderDetailList)) {
            orders = new Orders();
            orders.getOrdersDetails().addAll(orderDetailList);
            orders.setPaging(PagingUtils.pageOf(orderEntities));
        }
        return orders;
    }

    @Transactional
    public void deleteComment(final Long commentId) throws ApplicationException {
        ProductCommentEntity commentEntity = this.productCommentRepository.findOne(commentId);
        if (commentEntity == null) {
            throw new ApplicationException(this.i18n.getMessage("comment.not.exist"));
        }
        Long userId = this.userSessionHolder.getUserId();
        Long ownerId = commentEntity.getPostedBy().getId();
        Long productOwnerId = commentEntity.getProduct().getOwner().getId();
        if (userId.equals(ownerId) || userId.equals(productOwnerId)) {
            commentEntity.getProduct().getComments().remove(commentEntity);
            commentEntity.getPostedBy().getProductComments().remove(commentEntity);
            this.productCommentRepository.delete(commentId);
        } else {
            throw new ApplicationException(this.i18n.getMessage("user.has.no.permission"));
        }
    }

    private void deleteImage(final String dirPath, final Collection<? extends ImageInfoAccessor> collection) {
        for (ImageInfoAccessor infoAccessor : collection) {
            this.imageRepository.deleteImage(infoAccessor.getId());
            FileUtils.deleteQuietly(StringsHelper.appendAll(dirPath, infoAccessor.getImagePath()));
        }
    }

    @Transactional
    public void deleteProductImageByImageId(final Long imageId) throws ApplicationException {
        List<ImageEntity> images = this.imageRepository.getImagesByImageId(imageId);
        Long userId = this.userSessionHolder.getUserId();
        for (ImageEntity entity : images) {
            if (entity instanceof ProductMainImageEntity || entity instanceof ProductSecondaryImageEntity) {
                ProductImageInfoAccessor productImageInfoAccessor = (ProductImageInfoAccessor) entity;
                if (!productImageInfoAccessor.getProduct().getOwner().getId().equals(userId)) {
                    throw new ApplicationException(this.i18n.getMessage("user.has.no.permission"));
                }
            }
        }
        if (CollectionUtils.isEmpty(images)) {
            throw new ApplicationException(this.i18n.getMessage("image.not.exists"));
        }
        String dirPath = StringsHelper.appendAll(configurationProvider.getProductsImagesStorePath(), File.separator);
        deleteImage(dirPath, images);
    }

    private Image fillImage(final Long imageId, final String productImageUrl, final String group) {
        Image image = new Image();
        image.setId(imageId);
        image.setUrl(productImageUrl + imageId);
        return image;
    }

    @Transactional
    public Products findProductsByName(String searchWord, final Integer pageNum, final Integer pageSize,
            Boolean tagSearch, final String sortType) {

        Sort order = null;
        if (sortType == null || sortType.equals("dateDesc")) {
            order = new Sort(new Sort.Order(Sort.Direction.DESC, "publishedDate"));
        } else if (sortType.equals("priceAsc")) {
            order = new Sort(new Sort.Order(Sort.Direction.ASC, "price"));
        } else if (sortType.equals("priceDesc")) {
            order = new Sort(new Sort.Order(Sort.Direction.DESC, "price"));
        }

        searchWord = StringsHelper.addPrefixSuffixToString(searchWord, "%", "%");
        tagSearch = tagSearch != null;
        int offset = PagingUtils.offset(pageNum, pageSize);
        int limit = PagingUtils.limit(pageSize, MAX_PRODUCTS_SIZE);
        Page<ProductEntity> products = null;

        if (tagSearch) {
            products = this.productRepository.findProductByTagAfter(searchWord, new PageRequest(offset, limit, order));
        } else {
            products = this.productRepository.findProductsByName(searchWord, new PageRequest(offset, limit, order));
        }
        return getProducts(products, this.productSearchConverter);
    }

    private String generateXmlRequestToBank(final String cardNumber, final String expDate, final String cvc,
            final String orderIdForBank, final String groupId, final String amount, final String currency,
            final String ipAddress, final String customerEmail, final BankInfoEntity bankInfoEntity)
            throws ApplicationException {
        if (bankInfoEntity == null) {
            throw new ApplicationException(this.i18n.getMessage("no.bank.info.in.db"));
        }
        String hashData = BankUtils.generateHashData(bankInfoEntity.getPassword(), bankInfoEntity.getTerminalId(),
                orderIdForBank, cardNumber, amount);
        return BankUtils.generateXML(bankInfoEntity.getMode(), bankInfoEntity.getVersion(),
                bankInfoEntity.getProvUserId(), hashData, bankInfoEntity.getUserId(), bankInfoEntity.getMerchantId(),
                bankInfoEntity.getTerminalId(), ipAddress, customerEmail, cardNumber, expDate, cvc, orderIdForBank,
                groupId, bankInfoEntity.getTransactionType(), bankInfoEntity.getInstallmentCnt(), amount, currency,
                bankInfoEntity.getCardHolderPresentCode(), bankInfoEntity.getMotoInd(),
                bankInfoEntity.getOriginalRetrefNum());
    }

    @Transactional
    public Comments getCommentsByProductId(final Long productId, final Integer pageNum, final Integer pageSize)
            throws ApplicationException {
        final int offset = PagingUtils.offset(pageNum, pageSize);
        final int limit = PagingUtils.limit(pageSize, MAX_DEFAULT_SIZE);
        Page<ProductCommentEntity> comments = this.productCommentRepository.getCommentsByProductId(productId,
                new PageRequest(offset, limit, new Sort(Sort.Direction.ASC, "publishedDate")));
        return getListOfComments(comments);
    }

    @Transactional(readOnly = true)
    public Products getFavoriteProducts(final Integer pageNum, final Integer pageSize) throws ApplicationException {
        final int offset = PagingUtils.offset(pageNum, pageSize);
        final int limit = PagingUtils.limit(pageSize, MAX_PRODUCTS_SIZE);
        Page<ProductEntity> products = this.favouriteProductRepository.getFavoriteProducts(
                this.userSessionHolder.getUserId(), new PageRequest(offset, limit));
        if (products == null || !products.hasContent()) {
            throw new ProductNotExistsException(this.i18n.getMessage("product.favorites.empty"));
        }
        return addProductCountingInfo(getProducts(products, this.productConverter));
    }

    @Transactional(readOnly = true)
    public List<ProductEntity> getFavouriteProducts(final String sellerName) {
        return this.favouriteProductRepository.getSellerFavouriteProducts(sellerName);
    }

    private ImagesGroup getImagesGroup(final Map<ImageFormat, ? extends ImageInfoAccessor> images) throws IOException,
            ApplicationException {
        // final Map<ImageFormat, ImageInfoAccessor> images =
        // productManager.uploadProductImage(productId,
        // isPrimaryPhoto, inputStream);

        // TODO: do refactoring
        ImagesGroup imagesGroup = new ImagesGroup();
        final String productImgUrl = this.requestSessionHolder.getServerAddress() + "/product/image/";

        ImageInfoAccessor image = images.get(ImageFormat.ORIGINAL);
        if (image != null) {
            imagesGroup.setOriginalImage(fillImage(image.getId(), productImgUrl, image.getGroup()));
        }

        image = images.get(ImageFormat.STANDARD);
        if (image != null) {
            imagesGroup.setStandardImage(fillImage(image.getId(), productImgUrl, image.getGroup()));
        }

        image = images.get(ImageFormat.THUMB);
        if (image != null) {
            imagesGroup.setThumbImage(fillImage(image.getId(), productImgUrl, image.getGroup()));
        }

        return imagesGroup;
    }

    private Comments getListOfComments(final Page<ProductCommentEntity> entityPage) throws ApplicationException {
        List<Comment> list = this.productCommentConverter.createTOList(entityPage.getContent());
        Comments comments = null;
        if (!CollectionUtils.isEmpty(list)) {
            comments = new Comments();
            comments.getComments().addAll(list);
            comments.setPaging(PagingUtils.pageOf(entityPage));
        } else {
            throw new ApplicationException(this.i18n.getMessage("product.has.no.comments"));
        }
        return comments;
    }

    // todo:refactor getting last comment
    @Transactional(readOnly = true)
    public Product getMarketProduct(final Long productId) throws ProductNotExistsException {
        ProductEntity productEntity = this.productRepository.findInMarket(productId);
        if (productEntity == null) {
            throw new ProductNotExistsException(this.i18n.getMessage("product.not.exists"));
        }
        Product product = this.productConverter.createTO(productEntity);
        List<ProductCommentEntity> commentEntities = this.productCommentRepository.getLastComment(productId,
                new PageRequest(0, 1));
        if (!commentEntities.isEmpty()) {
            ProductCommentEntity commentEntity = commentEntities.get(0);
            if (commentEntity != null) {
                product.setLastComment(commentEntity.getComment());
                product.setLastCommentUserName(commentEntity.getPostedBy().getUserCredentials().getUsername());
            }
        }
        product.setProductCountingInfo(getProductCountingInfo(productId));
        return product;
    }

    @Transactional(readOnly = true)
    // TODO: refactor
    public Products getMarketProducts(final Integer pageNum, final Integer pageSize) {
        final int offset = PagingUtils.offset(pageNum, pageSize);
        final int limit = PagingUtils.limit(pageSize, MAX_PRODUCTS_SIZE);
        Products products = getProducts(
                this.productRepository.findMarketProducts(ProductStatus.FOR_SALE, new PageRequest(offset, limit)),
                this.productConverter);
        return addProductCountingInfo(products);
    }

    @Transactional
    public OrderDetail getOrder(final Long orderId) throws ApplicationException {
        final UserOrderEntity order = getOrderEntity(orderId);
        return this.orderConverter.createTO(order);
    }

    private UserOrderEntity getOrderEntity(final Long orderId) throws ApplicationException {
        UserOrderEntity order = this.orderRepository.findOrderById(orderId, this.userSessionHolder.getUserId());
        if (order == null) {
            throw new ApplicationException(this.i18n.getMessage("order.not.found"));
        }
        return order;
    }

    @Transactional
    public Orders getOrders(final Integer pageNum, final Integer pageSize) {
        int offset = PagingUtils.offset(pageNum, pageSize);
        int limit = PagingUtils.limit(pageSize, MAX_PRODUCTS_SIZE);
        List<ProductStatus> statuses = new ArrayList<ProductStatus>();

        statuses.add(ProductStatus.CANCELED);
        statuses.add(ProductStatus.SOLD);
        statuses.add(ProductStatus.SHIPPING);
        statuses.add(ProductStatus.ISSUED);
        statuses.add(ProductStatus.APPROVED);

        Page<UserOrderEntity> ordersEntityPage = this.orderRepository.getOrders(this.userSessionHolder.getUserId(),
                statuses, new PageRequest(offset, limit));
        return convertOrders(ordersEntityPage);
    }

    @Transactional
    public ProductCountingInformation getProductCountingInfo(final Long productId) {
        ProductCountingInformation p = new ProductCountingInformation();
        p.setComments(this.productCommentRepository.countCommentsByProduct(productId));
        p.setInShortlist(this.favouriteProductRepository.isProductInShortlist(this.userSessionHolder.getUserId(),
                productId));
        p.setShortlistedByUsers(this.favouriteProductRepository.countShortlistedProduct(productId));
        return p;
    }

    public File getProductImage(final Long productImageId) throws IOException {
        ImageEntity imageEntity = this.imageRepository.findOne(productImageId);
        if (imageEntity == null) {
            return null;
        }
        String path = StringsHelper.appendAll(configurationProvider.getProductsImagesStorePath(), File.separator,
                imageEntity.getImagePath());
        return new File(path);
    }

    private Products getProducts(final Page<ProductEntity> productsPage,
            final SimpleConverterFactory<Product, ProductEntity> converter) {
        List<Product> productList = converter.createTOList(productsPage.getContent());
        Products products = null;
        if (!CollectionUtils.isEmpty(productList)) {
            products = new Products();
            products.getProducts().addAll(productList);
            products.setPaging(PagingUtils.pageOf(productsPage));
        }
        return products;
    }

    public Products getProductsByFollowedUsers(final Integer pageNum, final Integer pageSize) {
        Long userId = this.userSessionHolder.getUserId();
        final int offset = PagingUtils.offset(pageNum, pageSize);
        final int limit = PagingUtils.limit(pageSize, MAX_PRODUCTS_SIZE);
        Page<ProductEntity> productEntities = this.productRepository.findMarketProducts(userId, new PageRequest(offset,
                limit));
        return getProducts(productEntities, this.productSearchConverter);
    }

    @Transactional
    public Products getPublishedProducts(final Integer pageNum, final Integer pageSize) {
        Long userId = this.userSessionHolder.getUserId();
        return publishedProducts(userId, pageNum, pageSize);
    }

    @Transactional
    public Products getPublishedProducts(final Long userId, final Integer pageNum, final Integer pageSize) {
        return publishedProducts(userId, pageNum, pageSize);
    }

    @Transactional
    public Orders getSellings(final Integer pageNum, final Integer pageSize) {
        int offset = PagingUtils.offset(pageNum, pageSize);
        int limit = PagingUtils.limit(pageSize, MAX_PRODUCTS_SIZE);
        List<ProductStatus> statuses = new ArrayList<ProductStatus>();

        statuses.add(ProductStatus.CANCELED);
        statuses.add(ProductStatus.SOLD);
        statuses.add(ProductStatus.SHIPPING);
        statuses.add(ProductStatus.ISSUED);
        statuses.add(ProductStatus.APPROVED);

        Page<UserOrderEntity> ordersEntityPage = this.orderRepository.getSellings(this.userSessionHolder.getUserId(),
                statuses, new PageRequest(offset, limit));
        return convertOrders(ordersEntityPage);
    }

    @Transactional
    public Products getSoldById(final Long userId, final Integer pageNum, final Integer pageSize) {
        int offset = PagingUtils.offset(pageNum, pageSize);
        int limit = PagingUtils.limit(pageSize, MAX_PRODUCTS_SIZE);
        Page<ProductEntity> products = this.productRepository.getSoldByUserId(userId, ProductStatus.SOLD,
                new PageRequest(offset, limit));
        return getProducts(products, this.productSoldConverter);
    }

    private Map<ImageFormat, ImageInfoAccessor> getUploadedProductImagesIds(final ProductEntity product,
            final InputStream inputStream, final boolean asMain, final Integer sequence) throws ApplicationException,
            IOException {
        final Map<ImageFormat, ImageInfoAccessor> productImageIds = Maps.newEnumMap(ImageFormat.class);
        byte[] bytes = IOUtils.toByteArray(inputStream);
        try {
            String dateFolder = DateTime.now().toString("ddMMyyyy");
            String groupId = SecureUtils.generateSecureId();
            for (ImageFormat imageFormat : ImageFormat.values()) {
                String fileName = FileUtils.generateUniqueFileName("");
                String relativeDir = StringsHelper.appendAll(dateFolder, File.separator, imageFormat.name());
                String fileDir = StringsHelper.appendAll(configurationProvider.getProductsImagesStorePath(),
                        File.separator, relativeDir);
                OutputStream outputStream = ImageUtils.resizeTo(bytes, fileDir, fileName, imageFormat);
                outputStream.flush();
                IOUtils.closeQuietly(outputStream);
                ImageInfoAccessor imageEntity = saveProductImageInfo(product,
                        StringsHelper.appendAll(relativeDir, File.separator, fileName), imageFormat, groupId, asMain,
                        sequence);
                productImageIds.put(imageFormat, imageEntity);
            }
        } catch (Exception e) {
            throw new ApplicationException(this.i18n.getMessage("file.upload.failed"), e);
        } finally {
            bytes = null;
            IOUtils.closeQuietly(inputStream);
        }
        return productImageIds;
    }

    @Transactional(readOnly = true)
    // TODO: refactor
    public Products getUserDraftProducts(final Integer pageNum, final Integer pageSize) {
        final int offset = PagingUtils.offset(pageNum, pageSize);
        final int limit = PagingUtils.limit(pageSize, MAX_PRODUCTS_SIZE);
        return getProducts(this.productRepository.findUserDraftProducts(this.userSessionHolder.getUserId(),
                ProductStatus.NEW, new PageRequest(offset, limit)), this.productConverter);
    }

    @Transactional
    public void makeOrder(final Order order) throws ApplicationException {
        UserEntity buyer = this.userSessionHolder.getUser();
        ProductEntity product = this.productRepository.findOne(order.getProductId());
        if (product == null) {
            throw new ProductNotExistsException(this.i18n.getMessage("product.not.exists"));
        }

        UserEntity seller = product.getOwner();
        if (seller.getId().equals(buyer.getId()) || product.getStatus() != ProductStatus.FOR_SALE) {
            throw new ApplicationException(this.i18n.getMessage("can.not.order.product"));
        }

        String uniqueOrderIdForBank = SecureUtils.generateSecureId();
        // todo:get bank info from file or use some name id, not 1L
        BankInfoEntity bankInfoEntity = this.bankInfoRepository.findOne(1L);

        // todo:use Jaxb to generate
        String requestXmlToBank = generateXmlRequestToBank(order.getCreditCardNumber(), order.getExpirationMonth()
                + order.getExpirationYear(), order.getSecurityCode(), uniqueOrderIdForBank, "",
                StringsHelper.convertDoubleToBankAmountType(product.getPrice()), product.getCurrency().getCode(),
                this.requestSessionHolder.getSession().getIpAddress(), buyer.getUserCredentials().getEmail(),
                bankInfoEntity);
        String bankResponse = sendRequestToBank(requestXmlToBank, bankInfoEntity.getUri());
        String responseCode = XMLParser.getNodeValue(bankResponse, "/GVPSResponse/Transaction/Response/Code/text()");

        // todo: send email with error transaction message, think about storage
        // for
        // bank.approval.code
        if (!responseCode.equals(this.i18n.getMessage("bank.approval.code"))) {
            MessageContext messageContext = MessageContextFactory.newContext();
            String responseErrorMessage = XMLParser.getNodeValue(bankResponse,
                    "/GVPSResponse/Transaction/Response/ErrorMsg/text()");
            String responseSysErrMsg = XMLParser.getNodeValue(bankResponse,
                    "/GVPSResponse/Transaction/Response/SysErrMsg/text()");
            messageContext.addMessage(this.i18n.getMessage("bank.transaction.error"));
            messageContext.addMessage(this.i18n.getMessage("error.message.from.bank") + ": " + responseErrorMessage
                    + ". " + responseSysErrMsg);
            BankTransactionFailEntity transactionFailEntity = new BankTransactionFailEntity();
            transactionFailEntity.setBuyer(buyer);
            transactionFailEntity.setProduct(product);
            transactionFailEntity.setTransactionDate(DateTime.now());
            transactionFailEntity.setErrorMessage(responseErrorMessage);
            transactionFailEntity.setRequestXml(requestXmlToBank);
            transactionFailEntity.setResponseXml(bankResponse);
            this.bankTransactionFailRepository.save(transactionFailEntity);
            throw new ApplicationException(messageContext);
        }

        product.setStatus(ProductStatus.SOLD);
        UserOrderEntity orderEntity = new UserOrderEntity();
        orderEntity.setPaid(true);
        orderEntity.setUniqueOrderId(uniqueOrderIdForBank);
        orderEntity.setXmlResponseFromBank(/* bankResponse */"");

        // todo:take logic of addresses in separate method
        if (order.isSetAddressReceiver()) {
            UserAddressEntity address = null;
            if (order.getAddressReceiver().isSetId()) {
                address = this.userAddressRepository.findOne(order.getAddressReceiver().getId()).cloneEntity();
            } else {
                address = new UserAddressEntity();
                if (this.userAddressRepository.countUserAddresses(buyer.getId(), AddressAssignment.FOR_USER) < MAX_ADDRESSES_SIZE) {
                    address.setUser(buyer);
                    address.setAssignment(AddressAssignment.FOR_USER);
                    address = syncAddress(address, order.getAddressReceiver());
                    address = userAddressRepository.save(address).cloneEntity();
                }
            }
            address.setUser(null);
            address.setAssignment(AddressAssignment.FOR_ORDER);
            orderEntity.setAddressReceiver(address);
        }
        orderEntity.setBuyer(buyer);
        orderEntity.setSeller(seller);
        orderEntity.setProduct(product);
        DateTime date = DateTime.now();
        orderEntity.setOrderDate(date);
        UserOrderEntity userOrderEntity = this.orderRepository.save(orderEntity);
        favouriteProductRepository.removeProductFromFavorites(product.getId()); // delete

        final OrderSoldEmailSource source = OrderSoldEmailSource.builder().buyerName(buyer.getFullname())
                .buyerEmail(buyer.getUserCredentials().getEmail()).buyerMobile(buyer.getMobileNumber())
                .sellerName(seller.getFullname()).sellerEmail(seller.getUserCredentials().getEmail())
                .sellerMobile(seller.getMobileNumber()).productName(orderEntity.getProduct().getName())
                .price(orderEntity.getProduct().getPrice()).uniqueOrderId(userOrderEntity.getId().toString())
                .orderTime(date).currency(product.getCurrency()).build();

        this.applicationContext.publishEvent(new OrderSoldEmailEvent(source));

        /*
         * buySellProcessMailer.sendItemSoldEmail(new
         * String[]{seller.getUserCredentials().getEmail(), buyer.getUserCredentials().getEmail()},
         * userOrderEntity.getUniqueOrderId(), product.getName(),
         * buyer.getUserCredentials().getUsername(), date, product.getPrice(),
         * product.getCurrency());
         */
    }

    private ProductEntity postProduct(final ProductEntity product, final boolean isDraft) {
        product.setOwner(this.userSessionHolder.getUser());
        product.setStatus(isDraft ? ProductStatus.NEW : ProductStatus.FOR_SALE);
        product.setPublishedDate(isDraft ? null : DateTime.now());
        return this.productRepository.save(product);
    }

    // TODO: to be refactored
    @Transactional
    private Set<ProductTagEntity> productTags(final Product product) throws ValidationException {
        Set<ProductTagEntity> tagEntitySet = null;
        Set<String> tags = Sets.newHashSet(product.getTags());
        if (!CollectionUtils.isEmpty(tags)) {
            if (tags.size() > 10) {
                throw new ValidationException(this.i18n.getMessage("product.tags.more.10"));
            }
            tagEntitySet = new HashSet<ProductTagEntity>();
            List<ProductTagEntity> tagEntities = this.productTagRepository.findByTags(tags);
            List<ProductTagEntity> listForSave = new ArrayList<ProductTagEntity>();
            for (String tag : tags) {
                ProductTagEntity productTagEntity = new ProductTagEntity(tag);
                if (tagEntities.contains(productTagEntity)) {
                    tagEntitySet.add(tagEntities.get(tagEntities.indexOf(productTagEntity)));
                } else {
                    listForSave.add(productTagEntity);
                }
            }
            if (!CollectionUtils.isEmpty(listForSave)) {
                tagEntitySet.addAll(this.productTagRepository.save(listForSave));
            }
        }

        return tagEntitySet;
    }

    // todo: refactor changing productStatus - make one request to db
    @Transactional
    public void publishDraftProduct(final Long productId) throws ApplicationException {
        Long userId = this.userSessionHolder.getUserId();
        if (!this.productRepository.isProductDraftAndOwnedBy(userId, productId)) {
            throw new ProductNotExistsException(this.i18n.getMessage("product.not.exists"));
        }
        if (StringUtils.isEmpty(this.userSessionHolder.getUser().getIban())) {
            throw new ApplicationException("profile.iban.required");
        }
        this.productRepository.setProductStatus(productId, ProductStatus.FOR_SALE);
        this.productRepository.setProductPublishDate(productId, DateTime.now());
    }

    private Products publishedProducts(final Long userId, final Integer pageNum, final Integer pageSize) {
        final int offset = PagingUtils.offset(pageNum, pageSize);
        final int limit = PagingUtils.limit(pageSize, MAX_PRODUCTS_SIZE);
        Page<ProductEntity> productEntities = this.productRepository.getUserProducts(userId, ProductStatus.FOR_SALE,
                new PageRequest(offset, limit));
        Products products = getProducts(productEntities, this.productConverter);
        return addProductCountingInfo(products);
    }

    @Transactional
    public Product publishProduct(final Product product) throws ApplicationException {
        ProductEntity productEntity = commonSaveUpdateProduct(product);
        SellType sellType = com.digout.model.common.SellType.valueOf(product.getSellType().value());
        Product productTO = this.productConverter.createTO(postProduct(productEntity, false));
        if (sellType == SellType.DIGOUT) {
            if (StringUtils.isEmpty(this.userSessionHolder.getUser().getIban())) {
                productTO.setOwnerHasIban(false);
            } else {
                productTO.setOwnerHasIban(true);
            }
        }
        return productTO;
    }

    @Transactional
    public ProductEntity publishProduct(final ProductEntity product) {
        return postProduct(product, false);
    }

    @Transactional
    public void removeProduct(final Long productId) throws ProductNotExistsException {
        Long userId = this.userSessionHolder.getUserId();
        ProductEntity productEntity = this.productRepository.findOne(productId);
        boolean isProductOwnedByUser = this.productRepository.isProductOwnedBy(userId, productId);
        if (!isProductOwnedByUser) {
            throw new ProductNotExistsException(this.i18n.getMessage("product.not.exists"));
        }
        this.productRepository.delete(productId);
    }

    @Transactional
    public void removeProductFromFavourites(final Long productId) {
        this.favouriteProductRepository.removeProductFromFavourites(productId, this.userSessionHolder.getUserId());
    }

    @Transactional
    public void reportIssue(final Issue issue) throws ApplicationException {
        UserEntity userEntity = this.userSessionHolder.getUser();
        UserOrderEntity orderEntity = this.orderRepository.findOrderById(issue.getOrderId(), userEntity.getId());

        if (orderEntity == null) {
            throw new ApplicationException("order not found");
        }

        ProductEntity productEntity = orderEntity.getProduct();
        productEntity.setStatus(ProductStatus.ISSUED);
        orderRepository.save(orderEntity);

        IssueEntity issueEntity = new IssueEntity();
        issueEntity.setOrder(orderEntity);
        IssueType issueType = IssueType.getIssueType(issue.getIssueType());
        issueEntity.setIssueType(issueType);
        issueEntity.setUser(userEntity);
        String issueDetails = issue.getDetails();
        if (!Strings.isNullOrEmpty(issueDetails)) {
            issueEntity.setIssueDetails(issueDetails);
        }
        issueEntity.setReportDate(DateTime.now());

        issueRepository.save(issueEntity);

        final IssueEmailSource source = IssueEmailSource.builder()
                .email(orderEntity.getSeller().getUserCredentials().getEmail())
                .productName(orderEntity.getProduct().getName())
                .username(orderEntity.getBuyer().getUserCredentials().getUsername()).issueType(issueType.toString())
                .description(issueDetails).build();
        this.applicationContext.publishEvent(new IssueEmailEvent(source));
    }

    public Issue getIssueByOrder(final Long orderId) throws ApplicationException {
        List<IssueEntity> issueEntities = issueRepository.findIssuesByOrderId(orderId);
        if (CollectionUtils.isEmpty(issueEntities)) {
            throw new ApplicationException("order.has.no.issues");
        }
        final Issue issue = new Issue();
        final IssueEntity issue1 = issueEntities.get(0);
        issue.setIssueType(issue1.getIssueType().getOrder());
        issue.setDetails(issue1.getIssueDetails());
        issue.setOrderId(orderId);
        return issue;
    }

    @Transactional
    public ProductEntity saveDraftProduct(final Product product) throws ProductNotExistsException, ValidationException {
        ProductEntity productEntity = commonSaveUpdateProduct(product);
        return postProduct(productEntity, true);
    }

    @Transactional
    public ProductEntity saveDraftProduct(final ProductEntity product) {
        return postProduct(product, true);
    }

    private ProductImageInfoAccessor saveProductImageInfo(final ProductEntity product, final String imageFilePath,
            final ImageFormat imageFormat, final String group, final boolean asMain, final Integer sequence) {
        return asMain ? saveProductMainImage(product, imageFilePath, imageFormat, group) : saveProductSecondaryImage(
                product, imageFilePath, imageFormat, group, sequence);
    }

    private ProductImageInfoAccessor saveProductMainImage(final ProductEntity product, final String imageFilePath,
    // StorageEntity storage,
            final ImageFormat imageFormat, final String group) {
        ProductMainImageEntity prodImg = new ProductMainImageEntity();
        prodImg.setProduct(product);
        prodImg.setImagePath(imageFilePath);
        // prodImg.setStorage(storage);
        prodImg.setFormat(imageFormat);
        prodImg.setGroup(group);
        this.productMainImageRepository.save(prodImg);
        return prodImg;
    }

    private ProductImageInfoAccessor saveProductSecondaryImage(final ProductEntity product, final String imageFilePath,
            final ImageFormat imageFormat, final String group, final Integer sequence) {
        ProductSecondaryImageEntity prodImg = new ProductSecondaryImageEntity();
        prodImg.setProduct(product);
        prodImg.setImagePath(imageFilePath);
        prodImg.setFormat(imageFormat);
        prodImg.setGroup(group);
        prodImg.setSequence(sequence);
        this.productSecondaryImageRepository.save(prodImg);
        return prodImg;
    }

    @Transactional
    public void sellProduct(final Long productId) throws ApplicationException {
        ProductEntity productEntity = this.productRepository.findOne(productId);
        if (!productEntity.getOwner().getId().equals(this.userSessionHolder.getUserId())) {
            throw new ApplicationException(i18n.getMessage("user.has.no.permission.for.sale"));
        }
        if (productEntity.getStatus().equals(ProductStatus.FOR_SALE) || productEntity.getSellType() == SellType.F2F) {
            productEntity.setStatus(ProductStatus.NEW);
            productEntity.setSoldDate(new DateTime());
            productEntity.setPublishedDate(null);
            favouriteProductRepository.removeProductFromFavorites(productEntity.getId());
            this.productRepository.save(productEntity);
        } else {
            throw new ApplicationException(i18n.getMessage("can.not.sell.product"));
        }
    }

    // todo:use i18n
    @Transactional
    public void sendProduct(final Long orderId) throws ApplicationException {
        UserOrderEntity orderEntity = this.orderRepository.findOne(orderId);
        if (!orderEntity.getSeller().getId().equals(this.userSessionHolder.getUserId())) {
            throw new ApplicationException("user can send only its own product");
        }
        ProductEntity productEntity = orderEntity.getProduct();
        productEntity.setStatus(ProductStatus.SHIPPING);
        orderEntity.setStartShippingDate(new DateTime());
        this.orderRepository.save(orderEntity);
    }

    // todo: use i18n
    @Transactional
    public void sendProductBack(final Long orderId) throws ApplicationException {
        UserOrderEntity orderEntity = getBuyerOrSellerOrderById(orderId);
        if (!(orderEntity.getProduct().getStatus() == ProductStatus.SHIPPING)) {
            throw new ApplicationException("cant send back this product");
        }
        Long userId = this.userSessionHolder.getUserId();
        if (!orderEntity.getBuyer().getId().equals(userId)) {
            throw new ApplicationException("only buyer can send product back");
        }
        ProductEntity productEntity = orderEntity.getProduct();
        productEntity.setStatus(ProductStatus.CANCELED);
        DateTime now = DateTime.now();
        orderEntity.setCanceledDate(now);
        orderEntity.setStartShippingDate(now);
        this.orderRepository.save(orderEntity);
    }

    private UserOrderEntity getBuyerOrSellerOrderById(final Long orderId) throws ApplicationException {
        Long userId = this.userSessionHolder.getUserId();
        UserOrderEntity orderEntity = this.orderRepository.findOrderById(orderId, userId);
        if (orderEntity == null) {
            throw new ApplicationException("order.not.found");
        }
        return orderEntity;
    }

    private String sendRequestToBank(final String requestXml, final String uri) throws ApplicationException {
        Client client = Client.create(HttpsUtils.getSSLClientConfig());
        WebResource webResource = client.resource(uri);
        ClientResponse response = null;
        try {
            response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, requestXml);
        } catch (Exception ex) {
            LOGGER.error("Order payment failed", ex);
            throw new ApplicationException(this.i18n.getMessage("no.connection.with.bank"));
        }
        return response.getEntity(String.class);
    }

    private String sendRequestToBank(final String cardNumber, final String expDate, final String cvc,
            final String orderIdForBank, final String groupId, final String amount, final String currency,
            final String ipAddress, final String customerEmail) throws ApplicationException {
        BankInfoEntity bankInfoEntity = this.bankInfoRepository.findOne(1L);
        if (bankInfoEntity == null) {
            throw new ApplicationException(this.i18n.getMessage("no.bank.info.in.db"));
        }
        String hashData = BankUtils.generateHashData(bankInfoEntity.getPassword(), bankInfoEntity.getTerminalId(),
                orderIdForBank, cardNumber, amount);
        String requestDataXml = BankUtils.generateXML(bankInfoEntity.getMode(), bankInfoEntity.getVersion(),
                bankInfoEntity.getProvUserId(), hashData, bankInfoEntity.getUserId(), bankInfoEntity.getMerchantId(),
                bankInfoEntity.getTerminalId(), ipAddress, customerEmail, cardNumber, expDate, cvc, orderIdForBank,
                groupId, bankInfoEntity.getTransactionType(), bankInfoEntity.getInstallmentCnt(), amount, currency,
                bankInfoEntity.getCardHolderPresentCode(), bankInfoEntity.getMotoInd(),
                bankInfoEntity.getOriginalRetrefNum());
        Client client = Client.create();
        WebResource webResource = client.resource(bankInfoEntity.getUri());
        ClientResponse response = webResource.type(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class,
                requestDataXml);
        return response.getEntity(String.class);
    }

    private UserAddressEntity syncAddress(final UserAddressEntity userAddressEntity, final Address address) {
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
        return userAddressEntity;
    }

    private Products syncProductEntities(final Page<ProductEntity> productEntities) {
        Products products = new Products();
        for (ProductEntity p : productEntities) {
            Product product = new Product();
            product.setName(p.getName());
            product.setPrice(p.getPrice().toString());
            product.setCurrency(p.getCurrency().name());
            product.setOwnerFullName(p.getOwner().getFullname());
            products.getProducts().add(product);
        }
        return products;
    }

    // todo: refactor changing productStatus - make one request to db
    @Transactional
    public void unpublishProduct(final Long productId) throws ProductNotExistsException {
        Long userId = this.userSessionHolder.getUserId();
        if (!this.productRepository.isProductPublishedAndOwnedBy(userId, productId)) {
            throw new ProductNotExistsException(this.i18n.getMessage("product.not.exists"));
        }
        this.productRepository.setProductStatus(productId, ProductStatus.NEW);
        this.productRepository.setProductPublishDate(productId, null);
    }

    private ProductEntity updateProduct(final Product product) throws ProductNotExistsException {
        ProductEntity productEntity = this.productRepository.findOne(product.getProductId());
        if (productEntity == null) {
            throw new ProductNotExistsException(this.i18n.getMessage("product.update.error"));
        }
        this.productUpdateConverter.syncProduct(productEntity, product);
        return productEntity;
    }

    @Transactional
    public ProductEntity updateProduct(final ProductEntity entity) {
        entity.setOwner(this.userSessionHolder.getUser());
        return this.productRepository.save(entity);
    }

    private ProductImageInfoAccessor updateProductImageEntity(final ProductImageInfoAccessor productImageInfoAccessor,
            final String imageFilePath, final String group) {
        productImageInfoAccessor.setGroup(group);
        productImageInfoAccessor.setImagePath(imageFilePath);
        ProductImageInfoAccessor imageEntity = null;
        if (productImageInfoAccessor instanceof ProductMainImageEntity) {
            ProductMainImageEntity mainImageEntity = (ProductMainImageEntity) productImageInfoAccessor;
            imageEntity = this.productMainImageRepository.save(mainImageEntity);
        } else if (productImageInfoAccessor instanceof ProductSecondaryImageEntity) {
            ProductSecondaryImageEntity secondaryImageEntity = (ProductSecondaryImageEntity) productImageInfoAccessor;
            imageEntity = this.productSecondaryImageRepository.save(secondaryImageEntity);
        }
        return imageEntity;
    }

    @Transactional
    public ImagesGroup updateProductPhoto(final Long imageId, final InputStream inputStream)
            throws ApplicationException, IOException {
        List<ImageEntity> imageEntities = this.imageRepository.getImagesByImageId(imageId);
        if (CollectionUtils.isEmpty(imageEntities)) {
            throw new ApplicationException(this.i18n.getMessage("image.not.exists"));
        }

        Map<ImageFormat, ProductImageInfoAccessor> map = Maps.newEnumMap(ImageFormat.class);

        for (ImageEntity image : imageEntities) {
            if (image instanceof ProductImageInfoAccessor) {
                ProductImageInfoAccessor productImageInfoAccessor = (ProductImageInfoAccessor) image;

                if (!productImageInfoAccessor.getProduct().getOwner().getId()
                        .equals(this.userSessionHolder.getUserId())) {
                    throw new ApplicationException(this.i18n.getMessage("user.has.no.permission"));
                }

                if (((ProductImageInfoAccessor) image).getFormat() == ImageFormat.THUMB) {
                    map.put(ImageFormat.THUMB, productImageInfoAccessor);
                }

                if (((ProductImageInfoAccessor) image).getFormat() == ImageFormat.STANDARD) {
                    map.put(ImageFormat.STANDARD, productImageInfoAccessor);
                }

                if (((ProductImageInfoAccessor) image).getFormat() == ImageFormat.ORIGINAL) {
                    map.put(ImageFormat.ORIGINAL, productImageInfoAccessor);
                }
            }
        }

        map = uploadPhotoToFS(configurationProvider.getProductsImagesStorePath(), inputStream, map);
        return getImagesGroup(map);
    }

    public Map<ImageFormat, ProductImageInfoAccessor> uploadPhotoToFS(final String storagePath,
            final InputStream inputStream, final Map<ImageFormat, ProductImageInfoAccessor> productsImageEntity)
            throws IOException, ApplicationException {
        byte[] bytes = IOUtils.toByteArray(inputStream);
        try {
            String dateFolder = DateTime.now().toString("ddMMyyyy");
            String groupId = SecureUtils.generateSecureId();
            for (ImageFormat imageFormat : ImageFormat.values()) {
                String fileName = FileUtils.generateUniqueFileName("");
                String relativeDir = StringsHelper.appendAll(dateFolder, File.separator, imageFormat.name());
                String fileDir = StringsHelper.appendAll(storagePath, File.separator, relativeDir);
                OutputStream outputStream = ImageUtils.resizeTo(bytes, fileDir, fileName, imageFormat);
                outputStream.flush();
                IOUtils.closeQuietly(outputStream);
                ProductImageInfoAccessor productImageInfoAccessor = productsImageEntity.get(imageFormat);
                String pathForDeleteOldPhoto = StringsHelper.appendAll(storagePath, File.separator,
                        productImageInfoAccessor.getImagePath());
                FileUtils.deleteQuietly(pathForDeleteOldPhoto);
                productsImageEntity.put(
                        imageFormat,
                        updateProductImageEntity(productImageInfoAccessor,
                                StringsHelper.appendAll(relativeDir, File.separator, fileName), groupId));
            }
        } catch (Exception e) {
            throw new ApplicationException(this.i18n.getMessage("file.upload.failed"), e);
        } finally {
            bytes = null;
            IOUtils.closeQuietly(inputStream);
        }
        return productsImageEntity;
    }

    @Transactional
    public Map<ImageFormat, ImageInfoAccessor> uploadProductImage(final Long productId, final boolean isPrimaryImage,
            final InputStream inputStream, final Integer sequence) throws ApplicationException, IOException {
        ProductEntity product = this.productRepository.findOne(productId);
        if (product == null) {
            throw new ProductNotExistsException(this.i18n.getMessage("product.not.exists"));
        }

        if (isPrimaryImage && !CollectionUtils.isEmpty(product.getMainImages())) {
            throw new ApplicationException(this.i18n.getMessage("product.already.has.main.image"));
        }
        return getUploadedProductImagesIds(product, inputStream, isPrimaryImage, sequence);
    }

    public void reportInappropriateProduct(final Long productId) throws ProductNotExistsException {
        final ProductEntity product = this.productRepository.findOne(productId);
        if (product == null) {
            throw new ProductNotExistsException(this.i18n.getMessage("product.not.exists"));
        }

        final UserEntity reporter = this.userSessionHolder.getUser();

        final InappropriateProductEmailSource inappropriateProductEmailSource = InappropriateProductEmailSource
                .builder().forProduct(product).byReporter(reporter).withProductOwner(product.getOwner()).build();

        this.applicationContext.publishEvent(new InappropriateProductEmailEvent(inappropriateProductEmailSource));
    }

}
