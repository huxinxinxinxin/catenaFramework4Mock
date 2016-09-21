/*
 * www.7yangche.com Inc.
 * Copyright (c) 2014 All Rights Reserved.
 */
package com.catena.mock.inject;

import com.catena.core.inject.InjectionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author cyzhang
 */
public class InjectionLoading implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        InjectionContext.loadApplicationContext(applicationContext);
    }

}
