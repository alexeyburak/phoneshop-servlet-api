package com.es.phoneshop.model.product;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.dao.implementation.ArrayListProductDao;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ArrayListProductDaoTest
{
    private ProductDao productDao;

    @Before
    public void setup() {
        productDao = new ArrayListProductDao();
    }

    @Test
    public void testFindProductsNoResults() {
        assertTrue(productDao.findProducts().isEmpty());
    }
}
