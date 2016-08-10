package org.greenrobot.greendao.test.entityannotation;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

import java.util.concurrent.atomic.AtomicLong;

public class CustomerTest extends AbstractDaoTestLongPk<CustomerDao, Customer> {
    private AtomicLong nameNumber = new AtomicLong();

    public CustomerTest() {
        super(CustomerDao.class);
    }

    @Override
    protected Customer createEntity(Long key) {
        Customer entity = new Customer();
        entity.setId(key);
        entity.setName("Ho " + nameNumber.incrementAndGet());
        return entity;
    }

}
