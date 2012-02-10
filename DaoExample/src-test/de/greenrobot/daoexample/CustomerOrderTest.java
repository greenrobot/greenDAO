package de.greenrobot.daoexample;

import java.util.Date;
import java.util.List;

import android.app.Application;
import de.greenrobot.dao.test.AbstractDaoSessionTest;

public class CustomerOrderTest extends AbstractDaoSessionTest<Application, DaoMaster, DaoSession> {

    public CustomerOrderTest() {
        super(DaoMaster.class);
    }

    public void testCustomerToOrders() {
        Customer customer = new Customer(null, "greenrobot");
        daoSession.insert(customer);

        addOrderToCustomer(customer);
        addOrderToCustomer(customer);

        List<Order> orders = customer.getOrders();
        assertEquals(2, orders.size());
    }

    public void testOrderToCustomer() {
        Customer customer = new Customer(null, "greenrobot");
        daoSession.insert(customer);

        Order order = addOrderToCustomer(customer);
        Customer customer2 = order.getCustomer();
        
        assertSame(customer, customer2);
    }

    public void testUpdateBirectional() {
        Customer customer = new Customer(null, "greenrobot");
        daoSession.insert(customer);

        addOrderToCustomer(customer);
        List<Order> orders = customer.getOrders();
        
        Order newOrder = new Order();
        newOrder.setCustomer(customer);
        daoSession.insert(newOrder);
        orders.add(newOrder);
        assertEquals(2, orders.size());
        
        customer.resetOrders();
        List<Order> orders2 = customer.getOrders();
        assertEquals(orders.size(), orders2.size());
    }

    private Order addOrderToCustomer(Customer customer) {
        Date date = new Date(System.currentTimeMillis() - ((long) (Math.random() * 1000 * 60 * 60 * 24 * 365)));
        Order order = new Order(null, date, customer.getId());
        daoSession.insert(order);
        return order;
    }

}
