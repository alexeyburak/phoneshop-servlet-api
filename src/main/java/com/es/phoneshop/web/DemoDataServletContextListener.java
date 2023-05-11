package com.es.phoneshop.web;


import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.dao.impl.ProductDaoImpl;
import com.es.phoneshop.model.PriceChange;
import com.es.phoneshop.model.Product;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class DemoDataServletContextListener implements ServletContextListener {
    private final ProductDao productDao;

    public DemoDataServletContextListener() {
        this.productDao = ProductDaoImpl.getInstance();
    }

    public DemoDataServletContextListener(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        boolean isInsertDemoDataTrue = Boolean.parseBoolean(servletContextEvent.getServletContext().getInitParameter("insertDemoData"));

        if (isInsertDemoDataTrue) {
            saveSampleProducts();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    private void saveSampleProducts() {
        Currency usd = Currency.getInstance("USD");
        List<PriceChange> priceHistory = getPriceHistoryList();
        productDao.save(new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", priceHistory));
        productDao.save(new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg", priceHistory));
        productDao.save(new Product("sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg", priceHistory));
        productDao.save(new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg", priceHistory));
        productDao.save(new Product("iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg", priceHistory));
        productDao.save(new Product("sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg", priceHistory));
        productDao.save(new Product("xperiaxz", "Sony Xperia XZ", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg", priceHistory));
        productDao.save(new Product("nokia3310", "Nokia 3310", new BigDecimal(70), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Nokia/Nokia%203310.jpg", priceHistory));
        productDao.save(new Product("palmp", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg", priceHistory));
        productDao.save(new Product("simc56", "Siemens C56", new BigDecimal(70), usd, 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg", priceHistory));
        productDao.save(new Product("simc61", "Siemens C61", new BigDecimal(80), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg", priceHistory));
        productDao.save(new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg", priceHistory));
    }

    private List<PriceChange> getPriceHistoryList() {
        return new ArrayList<>(
                List.of(
                        new PriceChange(LocalDate.of(2020, 5, 2), new BigDecimal(100)),
                        new PriceChange(LocalDate.of(2018, 3, 23), new BigDecimal(110)),
                        new PriceChange(LocalDate.of(2017, 8, 15), new BigDecimal(150))
                )
        );
    }

}
