package com.catena.web.controller;

import com.catena.core.CatenaControllerBase;
import com.catena.entity.ParkEntity;
import com.catena.mock.MockRuntimeException;
import com.catena.mock.core.HttpRequestMethod;
import com.catena.mock.core.ScanUrlAndDataContext;
import com.catena.mock.param.MockBaseParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hx-pc on 16-5-18.
 */
@RestController
@RequestMapping ("/api")
public class MockDataManageController extends CatenaControllerBase {

    @Autowired
    private ScanUrlAndDataContext scanUrlAndDataContext;

    @RequestMapping (value = "/init", method = RequestMethod.POST)
    public String init() {
        return "init";
    }

    @RequestMapping (value = "/mock", method = RequestMethod.POST)
    public void addMock(MockBaseParam mockBaseParam) {
        if (!mockBaseParam.getApiValue().substring(0,1).equals("/")) {
            throw new MockRuntimeException(401,"请填写完整的url路径");
        }
        getOperation("addMock").start(mockBaseParam);
    }

    @RequestMapping (value = "/mock", method = RequestMethod.GET)
    public void getMock(@RequestParam(required = true) HttpRequestMethod httpRequestMethod, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        scanUrlAndDataContext.checkScanMockData();
        if (httpRequestMethod == null) {
            servletRequest.setAttribute("httpRequestMethod", "get");
        } else {
            servletRequest.setAttribute("httpRequestMethod", httpRequestMethod);
        }
        getOperation("getMock").startReturnDataWithObject(servletRequest, servletResponse);
    }
}
