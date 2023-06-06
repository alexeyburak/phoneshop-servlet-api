package com.es.phoneshop.dao.impl;

import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.model.Order;

public class OrderDaoImpl extends AbstractGenericDao<Order> implements OrderDao {

    private OrderDaoImpl() {
    }

    private static final class SingletonHolder {
        private static final OrderDaoImpl INSTANCE = new OrderDaoImpl();
    }

    public static OrderDaoImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }

}
