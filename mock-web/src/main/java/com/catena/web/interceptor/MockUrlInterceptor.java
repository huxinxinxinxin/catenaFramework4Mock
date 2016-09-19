package com.catena.web.interceptor;

import com.catena.core.CatenaContext;
import com.catena.mock.MockRuntimeException;
import com.catena.mock.core.ScanUrlAndDataContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by hx-pc on 16-5-17.
 */
public class MockUrlInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockUrlInterceptor.class);

    @Autowired
    private ScanUrlAndDataContext scanUrlAndDataContext;

    @Autowired
    private CatenaContext catenaContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        checkScanMockData();
        StringBuilder apiKey = getApiKey(request);
        String data = scanUrlAndDataContext.getDataWithApi(apiKey.toString(), request.getMethod());
        if (!StringUtils.isEmpty(data)) {
            request.setAttribute("data", data);
            catenaContext.getNodeOperationRepository().get("returnData").startReturnDataWithString(request, response);
        }
        return false;
    }

    private StringBuilder getApiKey(HttpServletRequest request) {
        StringBuilder apiKey = getUrlAddress(request);
        LOGGER.info("请求 {}, 来源 {} ",apiKey, request.getHeader("Host"));
        return apiKey;
    }

    protected StringBuilder getUrlAddress(HttpServletRequest request) {
        StringBuilder apiKey = new StringBuilder(request.getRequestURI());
        if (request.getParameterNames() != null && request.getParameterMap().size() > 0) {
            apiKey.append("?");
            for (Map.Entry<String, String[]> e : (request.getParameterMap()).entrySet()) {
                apiKey.append(e.getKey()).append("=").append(e.getValue()[0]).append("&");
            }
            apiKey = new StringBuilder(apiKey.substring(0, apiKey.length() - 1));
        }
        return apiKey;
    }

    protected void checkScanMockData() throws IOException {
        List<File> files = new CopyOnWriteArrayList<>();
        File rootDir = new File(ScanUrlAndDataContext.FILE_PATH);
        for (File file : rootDir.listFiles()) {
            validateLastModified(files, file);
        }
        if (!CollectionUtils.isEmpty(files)) {
            scanUrlAndDataContext.scanFile(files, false);
        }
    }

    protected void validateLastModified(List<File> files, File file) {
        Map<String, Long> fileLastModified = scanUrlAndDataContext.getFileLastModified();
        if (Objects.isNull(fileLastModified.get(file.getName()))) {
            files.add(file);
        } else {
            if (!(fileLastModified.get(file.getName()) - file.lastModified() == 0)) {
                fileLastModified.put(file.getName(), file.lastModified());
                files.add(file);
            }
        }
    }
}
