package com.catena.web.controller;

import com.catena.core.CatenaOperationConstant;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by hx-pc on 16-3-21.
 */
@Controller
@RequestMapping(value = "/api")
public class CatenaConfigController {

    @RequestMapping(value = "/{target}", method = RequestMethod.GET)
    public String singleGet(@PathVariable String target, HttpServletRequest servletRequest, Model model) throws IOException {
        model.addAttribute(CatenaOperationConstant.START_RETURN_VALUE, servletRequest.getAttribute(CatenaOperationConstant.START_RETURN_VALUE));
        return getTargetIndex(target.split("\\."));
    }

    @RequestMapping(value = "/{target}", method = RequestMethod.POST)
    public String singlePost(@PathVariable String target, HttpServletRequest servletRequest, Model model) throws IOException {
        model.addAttribute(CatenaOperationConstant.START_RETURN_VALUE, servletRequest.getAttribute(CatenaOperationConstant.START_RETURN_VALUE));
        return getTargetIndex(target.split("\\."));
    }

    @RequestMapping(value = "/{target}", method = RequestMethod.PUT)
    public String singlePut(@PathVariable String target, HttpServletRequest servletRequest, Model model) throws IOException {
        model.addAttribute(CatenaOperationConstant.START_RETURN_VALUE, servletRequest.getAttribute(CatenaOperationConstant.START_RETURN_VALUE));
        return getTargetIndex(target.split("\\."));
    }

    @RequestMapping(value = "/{target}", method = RequestMethod.DELETE)
    public String singleDelete(@PathVariable String target, HttpServletRequest servletRequest, Model model) throws IOException {
        model.addAttribute(CatenaOperationConstant.START_RETURN_VALUE, servletRequest.getAttribute(CatenaOperationConstant.START_RETURN_VALUE));
        return getTargetIndex(target.split("\\."));
    }

    private String getTargetIndex(String... target) {
        return target[target.length - 1];
    }

}
