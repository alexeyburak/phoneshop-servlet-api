package com.es.phoneshop.dao.implementation;

import com.es.phoneshop.dao.ProductDao;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ArrayListProductDaoImplTest
{
    private ProductDao productDao;

    @Before
    public void setup() {
        productDao = new ArrayListProductDaoImpl();
    }

    @Test
    public void testFindProductsNoResults() {
        assertTrue(productDao.findProducts().isEmpty());
    }
}
