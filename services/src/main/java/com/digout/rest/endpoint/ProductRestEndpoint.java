package com.digout.rest.endpoint;

import com.digout.artifact.*;
import com.digout.converter.ProductCommentConverter;
import com.digout.converter.ProductConverter;
import com.digout.exception.ApplicationException;
import com.digout.exception.ProductNotExistsException;
import com.digout.exception.ValidationException;
import com.digout.manager.ProductManager;
import com.digout.manager.RequestSessionHolder;
import com.digout.model.UserRole;
import com.digout.model.common.ImageFormat;
import com.digout.model.entity.common.ImageInfoAccessor;
import com.digout.model.meta.Authenticated;
import com.digout.validation.CommentValidator;
import com.digout.validation.CreditCardValidator;
import com.digout.validation.ProductValidator;
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
import java.util.Map;

@Path("/product")
public class ProductRestEndpoint {

    @Autowired
    private ProductConverter productConverter;
    @Autowired
    private ProductManager productManager;
    @Autowired
    private RequestSessionHolder requestSessionHolder;
    @Autowired
    private ProductValidator productValidator;
    @Autowired
    private CommentValidator commentValidator;
    @Autowired
    private CreditCardValidator creditCardValidator;
    @Autowired
    private ProductCommentConverter productCommentConverter;

    @Path("favorites/{productId}/add")
    @GET
    @Authenticated(UserRole.USER)
    @Transactional
    public Response addProductToFavorites(@PathParam("productId") final Long productId) throws ApplicationException {
        this.productManager.addProduct2Favourites(productId);
        return Response.ok().build();
    }

    @Path("/{orderId}/approvePurchase")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public Response approvePurchase(@PathParam("orderId") final Long orderId) throws ApplicationException {
        this.productManager.approvePurchase(orderId);
        return Response.ok().build();
    }

    @Path("comment/{commentId}/delete")
    @DELETE
    @Authenticated(UserRole.USER)
    @Transactional
    public Response deleteComment(@PathParam("commentId") final Long commentId) throws ApplicationException {
        this.productManager.deleteComment(commentId);
        return Response.ok().build();
    }

    @Path("/image/{imageId}/delete")
    @DELETE
    @Authenticated(UserRole.USER)
    @Transactional
    public Response deleteProductImage(@PathParam("imageId") final Long imageId) throws ApplicationException {
        this.productManager.deleteProductImageByImageId(imageId);
        return Response.ok().build();
    }

    @Path("/draftProducts")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated(UserRole.USER)
    @Transactional
    public Products draftProducts(@QueryParam("pageNum") final Integer pageNum,
            @QueryParam("pageSize") final Integer pageSize) {
        return this.productManager.getUserDraftProducts(pageNum, pageSize);
    }

    private Image fillImage(final Long imageId, final String productImageUrl, final String group) {
        Image image = new Image();
        image.setId(imageId);
        image.setUrl(productImageUrl + imageId);
        return image;
    }

    @Path("/find")
    @GET
    @Authenticated(UserRole.USER)
    @Transactional
    public Products findByName(@QueryParam("productName") final String productName,
            @QueryParam("pageNum") final Integer pageNum, @QueryParam("pageSize") final Integer pageSize,
            @QueryParam("tagSearch") final Boolean tagSearch, @QueryParam("sortType") final String sortType) {
        return this.productManager.findProductsByName(productName, pageNum, pageSize, tagSearch, sortType);
    }

    @GET
    @Path("comment/{productId}/comments")
    @Authenticated(UserRole.USER)
    @Transactional
    public Comments getCommentsByProductId(@PathParam("productId") final Long productId,
            @QueryParam("pageNum") final Integer pageNum, @QueryParam("pageSize") final Integer pageSize)
            throws ApplicationException {
        return this.productManager.getCommentsByProductId(productId, pageNum, pageSize);
    }

    @Path("favorites/getAll")
    @GET
    @Authenticated(UserRole.USER)
    @Produces("application/json")
    @Transactional
    public Products getFavoriteProducts(@QueryParam("pageNum") final Integer pageNum,
            @QueryParam("pageSize") final Integer pageSize) throws ApplicationException {
        return this.productManager.getFavoriteProducts(pageNum, pageSize);
    }

    @Path("/{orderId}/orderDetails")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public OrderDetail getOrder(@PathParam("orderId") final Long orderId) throws ApplicationException {
        return this.productManager.getOrder(orderId);
    }

    @Path("/myOrders")
    @GET
    @Produces("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public Orders getOrders(@QueryParam("pageNum") final Integer pageNum, @QueryParam("pageSize") final Integer pageSize) {
        return this.productManager.getOrders(pageNum, pageSize);
    }

    @Path("/published")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public Products getPublished(@QueryParam("pageNum") final Integer pageNum,
            @QueryParam("pageSize") final Integer pageSize) {
        return this.productManager.getPublishedProducts(pageNum, pageSize);
    }

    @Path("/{userId}/published")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public Products getPublishedByUserId(@PathParam("userId") final Long userId,
            @QueryParam("pageNum") final Integer pageNum, @QueryParam("pageSize") final Integer pageSize) {
        return this.productManager.getPublishedProducts(userId, pageNum, pageSize);
    }

    @Path("/sellings")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public Orders getSellings(@QueryParam("pageNum") final Integer pageNum,
            @QueryParam("pageSize") final Integer pageSize) {
        return this.productManager.getSellings(pageNum, pageSize);
    }

    @Path("/{userId}/sold")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public Products getSoldById(@PathParam("userId") final Long userId, @QueryParam("pageNum") final Integer pageNum,
            @QueryParam("pageSize") final Integer pageSize) {
        return this.productManager.getSoldById(userId, pageNum, pageSize);
    }

    @Path("/makeOrder")
    @POST
    @Authenticated(UserRole.USER)
    @Transactional
    public Response makeOrder(final Order order) throws ApplicationException {
        this.creditCardValidator.validateAndRaise(order);
        this.productManager.makeOrder(order);
        return Response.ok().build();
    }

    @Path("/mainProducts")
    @GET
    @Produces({ "application/json" })
    @Authenticated({ UserRole.USER, UserRole.GUEST })
    @Transactional
    public Products marketProducts(@QueryParam("pageNum") final Integer pageNum,
            @QueryParam("pageSize") final Integer pageSize) {
        return this.productManager.getMarketProducts(pageNum, pageSize);
    }

    @Path("/{productId}/details")
    @GET
    @Produces({ "application/json" })
    @Authenticated({ UserRole.USER, UserRole.GUEST })
    @Transactional
    public Product product(@PathParam("productId") final Long productId) throws ProductNotExistsException {
        return this.productManager.getMarketProduct(productId);
    }

    @Path("/{productId}/countingInfo")
    @GET
    @Authenticated(UserRole.USER)
    @Transactional
    public ProductCountingInformation productCountingInformation(@PathParam("productId") final Long productId) {
        return this.productManager.getProductCountingInfo(productId);
    }

    @Path("image/{productImageId}")
    @GET
    @Produces("*/*")
    @Consumes("*/*")
    @Transactional(readOnly = true)
    public Response productImage(@PathParam("productImageId") final Long productImageId) {
        try {
            File imageFile = this.productManager.getProductImage(productImageId);
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

    @Path("/publish4sale")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated(UserRole.USER)
    @Transactional
    public Product publish4sale(final Product product) throws ApplicationException {
        this.productValidator.validateAndRaise(product);
        return this.productManager.publishProduct(product);
    }

    @Path("/{draftProductId}/publishDraft4sale")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated(UserRole.USER)
    @Transactional
    public Response publishDraft4sale(@PathParam("draftProductId") final Long productId) throws ApplicationException {
        this.productManager.publishDraftProduct(productId);
        return Response.status(Response.Status.OK).build();
    }

    @Path("/{productId}/remove")
    @DELETE
    @Produces({ "application/json" })
    @Authenticated({ UserRole.USER })
    @Transactional
    public void remove(@PathParam("productId") final Long productId) throws ProductNotExistsException {
        this.productManager.removeProduct(productId);
    }

    @Path("favorites/{productId}/remove")
    @DELETE
    @Authenticated(UserRole.USER)
    @Transactional
    public void removeProductFromFavorites(@PathParam("productId") final Long productId) {
        this.productManager.removeProductFromFavourites(productId);
    }

    @Path("/reportIssue")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public Response reportIssue(final Issue issue) throws ApplicationException {
        this.productManager.reportIssue(issue);
        return Response.ok().build();
    }

    @Path("/saveDraft")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated(UserRole.USER)
    @Transactional
    public Product saveDraftProduct(final Product product) throws ApplicationException {
        this.productValidator.validateAndRaise(product);
        return this.productConverter.createTO(this.productManager.saveDraftProduct(product));
    }

    @Path("/{productId}/sell")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public Response sellProduct(@PathParam("productId") final Long productId) throws ApplicationException {
        this.productManager.sellProduct(productId);
        return Response.ok().build();
    }

    @Path("/{orderId}/sendProduct")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public Response sendProduct(@PathParam("orderId") final Long orderId) throws ApplicationException {
        this.productManager.sendProduct(orderId);
        return Response.ok().build();
    }

    /*
     * @Path("test/query")
     * 
     * @GET
     * 
     * @Authenticated(UserRole.USER)
     * 
     * @Transactional public Products getProductsByFollowed(@QueryParam("pageNum") Integer pageNum,
     * 
     * @QueryParam("pageSize") Integer pageSize){ return
     * productManager.getProductsByFollowedUsers(pageNum,pageSize); }
     */

    @Path("/{orderId}/sendProductBack")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public Response sendProductBack(@PathParam("orderId") final Long orderId) throws ApplicationException {
        this.productManager.sendProductBack(orderId);
        return Response.ok().build();
    }

    @Path("comment/add")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public Response setComment(final Comment comment) throws ValidationException {
        this.commentValidator.validateAndRaise(comment);
        this.productManager.addComment(comment);
        return Response.ok().build();
    }

    @Path("/{productId}/unpublish4sale")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated(UserRole.USER)
    @Transactional
    public Response unpublish4sale(@PathParam("productId") final Long productId) throws ProductNotExistsException {
        this.productManager.unpublishProduct(productId);
        return Response.status(Response.Status.OK).build();
    }

    @Path("image/{imageId}/update")
    @POST
    @Produces({ "application/json" })
    @Consumes("multipart/form-data")
    @Authenticated(UserRole.USER)
    @Transactional
    public ImagesGroup updateProductPhoto(@PathParam("imageId") final Long imageId,
            @FormDataParam("file") final InputStream inputstream) throws IOException, ApplicationException {
        return this.productManager.updateProductPhoto(imageId, inputstream);
    }

    private ImagesGroup uploadProductImage(final Long productId, final boolean isPrimaryPhoto,
            final InputStream inputStream, final Integer sequence) throws IOException, ApplicationException {
        final Map<ImageFormat, ImageInfoAccessor> images = this.productManager.uploadProductImage(productId,
                isPrimaryPhoto, inputStream, sequence);

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

    @Path("/{productId}/uploadMainPhoto")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({ "application/json" })
    @Authenticated(UserRole.USER)
    @Transactional
    public ImagesGroup uploadProductMainPhoto(@PathParam("productId") final Long productId,
            @FormDataParam("file") final InputStream inputstream) throws ApplicationException, IOException {
        return uploadProductImage(productId, true, inputstream, null);
    }

    @Path("/{productId}/uploadPhoto")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({ "application/json" })
    @Authenticated(UserRole.USER)
    @Transactional
    public ImagesGroup uploadProductPhoto(@PathParam("productId") final Long productId,
            @QueryParam("seq") Integer sequence, @FormDataParam("file") final InputStream inputstream)
            throws ApplicationException, IOException {
        return uploadProductImage(productId, false, inputstream, sequence);
    }

    @Path("/{productId}/reportInappropriate")
    @POST
    @Produces("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public Response reportInappropriate(@PathParam("productId") final Long productId) throws ApplicationException {
        this.productManager.reportInappropriateProduct(productId);
        return Response.ok().build();
    }
}
