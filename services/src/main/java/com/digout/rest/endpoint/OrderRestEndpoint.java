package com.digout.rest.endpoint;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.digout.artifact.Issue;
import com.digout.artifact.OrderDetail;
import com.digout.artifact.OrderShipmentInfo;
import com.digout.exception.ApplicationException;
import com.digout.manager.OrderManager;
import com.digout.manager.ProductManager;
import com.digout.model.UserRole;
import com.digout.model.meta.Authenticated;

@Path("/order")
public class OrderRestEndpoint {

    @Autowired
    private OrderManager orderManager;
    @Autowired
    private ProductManager productManager;

    @POST
    @Path("/ship")
    @Produces("application/json")
    @Consumes("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public OrderDetail shipOrder(final OrderShipmentInfo shipmentInfo) throws ApplicationException {
        return orderManager.shipOrder(shipmentInfo);
    }

    @GET
    @Path("/{orderId}/shippingInfo")
    @Produces("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public OrderShipmentInfo getOrderShippingInfo(@PathParam("orderId") final Long orderId) throws ApplicationException {
        return orderManager.getOrderShipmentInfo(orderId);
    }

    @Path("/{orderId}/issue")
    @GET
    @Produces("application/json")
    @Authenticated(UserRole.USER)
    @Transactional
    public Issue getIssue(@PathParam("orderId") final Long orderId) throws ApplicationException {
        return productManager.getIssueByOrder(orderId);
    }
    
    public static void main(String[] args) {
        Boolean flag = null;
        System.out.println(flag ? "+" : "-");
    }
}
