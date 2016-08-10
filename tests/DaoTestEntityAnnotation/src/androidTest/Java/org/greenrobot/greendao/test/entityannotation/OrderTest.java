package org.greenrobot.greendao.test.entityannotation;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

public class OrderTest extends AbstractDaoTestLongPk<OrderDao, Order> {

    public OrderTest() {
        super(OrderDao.class);
    }

    @Override
    protected Order createEntity(Long key) {
        Order entity = new Order();
        entity.setId(key);
        entity.setCustomerId(1);
        return entity;
    }

}
