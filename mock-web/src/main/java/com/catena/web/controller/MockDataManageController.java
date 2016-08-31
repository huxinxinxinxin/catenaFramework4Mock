package com.catena.web.controller;

import com.catena.core.CatenaControllerBase;
import com.catena.entity.ParkEntity;
import com.catena.mock.param.MockBaseParam;
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

    @RequestMapping (value = "/mock", method = RequestMethod.POST)
    public void addMock(MockBaseParam mockBaseParam) {
        getOperation("addMock").start(mockBaseParam);
    }

    @RequestMapping (value = "/mock", method = RequestMethod.GET)
    public void getMock(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        getOperation("getMock").startReturnDataWithObject(servletRequest, servletResponse);
    }

    @RequestMapping (value = "/park", method = RequestMethod.GET)
    public ResponseEntity<List<LinkedHashMap>> getPark(@RequestParam (required = false) String industry,
                                                       @RequestParam (required = false)Long capitalUp,
                                                       @RequestParam (required = false)Long capitalDown,
                                                       @RequestParam (required = false)Integer personUp,
                                                       @RequestParam (required = false)Integer personDown) throws IOException {
        List<LinkedHashMap> results = ParkEntity.buildListParkEntity();
        results = results.stream().filter(linkedHashMap -> {
            if (capitalUp != null) {
                if (linkedHashMap.get("regcap") != null) {
                    return Double.valueOf(linkedHashMap.get("regcap") + "") < capitalUp;
                }
                return false;
            }
            if (capitalDown != null) {
                if (linkedHashMap.get("regcap") != null) {
                    return Double.valueOf(linkedHashMap.get("regcap") +"") > capitalDown;
                }
                return false;
            }
            if (personUp != null) {
                return Integer.valueOf(linkedHashMap.get("person") +"") < personUp;
            }
            if (personDown != null) {
                return Integer.valueOf(linkedHashMap.get("person") + "") > personDown;
            }
            if (industry != null) {
                if (industry.equals("科技创新") && linkedHashMap.get("industry").equals("科技创新")) {
                    return true;
                } else if (industry.equals("文化创意") && linkedHashMap.get("industry").equals("文化创意")) {
                    return true;
                } else if (industry.equals("电子商务") && linkedHashMap.get("industry").equals("电子商务")) {
                    return true;
                } else if (industry.equals("其他")) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        return new ResponseEntity<>(results.stream().collect(Collectors.toList()), HttpStatus.OK);
    }

}
