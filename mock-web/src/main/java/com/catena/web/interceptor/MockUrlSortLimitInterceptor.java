package com.catena.web.interceptor;

import com.catena.core.CatenaContext;
import com.catena.mock.core.ScanUrlAndDataContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hx-pc on 16-9-6.
 */
public class MockUrlSortLimitInterceptor extends MockUrlInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockUrlSortLimitInterceptor.class);

    @Autowired
    private ScanUrlAndDataContext scanUrlAndDataContext;

    @Autowired
    private CatenaContext catenaContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        checkScanMockData();

        return super.preHandle(request, response, handler);
    }
}
