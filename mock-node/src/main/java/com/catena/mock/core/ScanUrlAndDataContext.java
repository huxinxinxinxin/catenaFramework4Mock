package com.catena.mock.core;

import com.catena.mock.MockRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by hx-pc on 16-5-17.
 */
public class ScanUrlAndDataContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScanUrlAndDataContext.class);
    public static final String FILE_NAME = "mockConfig";
    public static final String PROJECT_VERSION = "project.version";
    public static String FILE_PATH;
    public static final String API_KEY = "api.key";
    public static final String DATA_GET_KEY = "data.get.key";
    public static final String DATA_POST_KEY = "data.post.key";
    public static final String DATA_PUT_KEY = "data.put.key";
    public static final String DATA_DELETE_KEY = "data.delete.key";
    private Map<String, String> resourceUrlMap = new HashMap<>();
    private Map<String, String> resourceDataGetMap = new HashMap<>();
    private Map<String, String> resourceDataPostMap = new HashMap<>();
    private Map<String, String> resourceDataPutMap = new HashMap<>();
    private Map<String, String> resourceDataDeleteMap = new HashMap<>();
    private Map<String, String> environmentMap = new HashMap<>();
    private Map<String, Long> fileLastModified = new HashMap<>();
    private boolean scanSwitch = false;

    public ScanUrlAndDataContext() throws IOException {
        loadEnvironment();
        load();
        scanLastModified();
    }

    private void scanLastModified() throws IOException {
        List<File> files = new CopyOnWriteArrayList<>();
        File rootDir = new File(FILE_PATH);
        for (File file : rootDir.listFiles()) {
            files.add(file);
        }
        if (!CollectionUtils.isEmpty(files)) {
            this.scanFile(files);
        }
    }

    public static ScanUrlAndDataContext reset() {
        try {
            return new ScanUrlAndDataContext();
        } catch (IOException e) {
            throw new MockRuntimeException(e, "500001");
        }
    }

    private void loadEnvironment() throws IOException {
        File file = new File("projectEnvironment/environment.properties");
        if (!file.exists()) {
            LOGGER.error("projectEnvironment/environment.properties 文件不存在");
            throw new MockRuntimeException("环境配置文件environment不存在请创建");
        }
        FileInputStream fis = new FileInputStream(file);
        StringBuilder lineStr = new StringBuilder();
        byte b = 10;
        while (b > 0) {
            if (b == 10) {
                analyzeEnvLineStr(lineStr);
                lineStr.delete(0, lineStr.length());
            }
            lineStr.append((char) b);
            b = (byte) fis.read();
        }
        analyzeEnvLineStr(lineStr);
        fis.close();
    }

    private void load() throws IOException {
        FILE_PATH = "mockConfig" + environmentMap.get(PROJECT_VERSION) + "/";
        File rootDir = new File(FILE_PATH);
        if (!rootDir.exists()) {
            rootDir.mkdirs();
            LOGGER.info("创建目录mockConfig成功");
        }
        List<File> files = getFiles(rootDir);
        scanFile(files);
    }

    private List<File> getFiles(File rootDir) {
        List<File> list = new CopyOnWriteArrayList<>();
        for (String fileName : rootDir.list()) {
            if (fileName.endsWith("properties")) {
                list.add(new File(rootDir.getAbsolutePath() + "/" + fileName));
            }
        }
        return list;
    }

    public void scanFile(List<File> files) throws IOException {
        for (File file : files) {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            while (true) {
                StringBuilder lineStr = new StringBuilder();
                String s = br.readLine();
                if (s != null) {
                    lineStr = lineStr.append(s);
                    analyzeMockLineStr(lineStr);
                } else
                    break;
            }
            br.close();
        }
    }

    private void analyzeMockLineStr(StringBuilder lineStr) {
        String str = lineStr.toString().replace("\n", "");
        if (str.length() > 0 && !Objects.equals(str.substring(0, 1), "#")) {
            int indexPoint = str.indexOf('.');
            int indexEqual = str.indexOf('=') + 1;
            String beforePt = str.substring(indexPoint + 1, indexEqual - 1);
            String afterPt = str.substring(indexEqual);
            if (Objects.equals(str.substring(0, indexPoint), environmentMap.get(API_KEY))) {
                resourceUrlMap.put(beforePt, afterPt);
            }
            if (Objects.equals(str.substring(0, indexPoint), environmentMap.get(DATA_GET_KEY))) {
                if (Objects.nonNull(resourceDataGetMap.get(resourceUrlMap.get(beforePt)))) {
                    LOGGER.info("有api重复 : {}", resourceUrlMap.get(beforePt));
                }
                resourceDataGetMap.put(resourceUrlMap.get(beforePt), afterPt);
            } else if (Objects.equals(str.substring(0, indexPoint), environmentMap.get(DATA_POST_KEY))) {
                if (Objects.nonNull(resourceDataPostMap.get(resourceUrlMap.get(beforePt)))) {
                    LOGGER.info("有api重复 : {}", resourceUrlMap.get(beforePt));
                }
                resourceDataPostMap.put(resourceUrlMap.get(beforePt), afterPt);
            } else if (Objects.equals(str.substring(0, indexPoint), environmentMap.get(DATA_PUT_KEY))) {
                if (Objects.nonNull(resourceDataPutMap.get(resourceUrlMap.get(beforePt)))) {
                    LOGGER.info("有api重复 : {}", resourceUrlMap.get(beforePt));
                }
                resourceDataPutMap.put(resourceUrlMap.get(beforePt), afterPt);
            } else if (Objects.equals(str.substring(0, indexPoint), environmentMap.get(DATA_DELETE_KEY))) {
                if (Objects.nonNull(resourceDataDeleteMap.get(resourceUrlMap.get(beforePt)))) {
                    LOGGER.info("有api重复 : {}", resourceUrlMap.get(beforePt));
                }
                resourceDataDeleteMap.put(resourceUrlMap.get(beforePt), afterPt);
            }
        }
    }

    private void analyzeEnvLineStr(StringBuilder lineStr) {
        String str = lineStr.toString().replace("\n", "");
        if (str.length() > 0 && !Objects.equals(str.substring(0, 1), "#")) {
            int indexEqual = str.lastIndexOf('=') + 1;
            String beforePt = str.substring(0, indexEqual - 1);
            String afterPt = str.substring(indexEqual);
            environmentMap.put(beforePt, afterPt);
        }
    }

    public Map<String, String> getResourceApiMap() {
        if (scanSwitch) {
            reset();
        }
        return resourceUrlMap;
    }

    public Map<String, String> getResourceDataGetMap() {
        if (scanSwitch) {
            reset();
        }
        return resourceDataGetMap;
    }

    public Map<String, String> getResourceDataPostMap() {
        if (scanSwitch) {
            reset();
        }
        return resourceDataPostMap;
    }

    public Map<String, String> getResourceDataPutMap() {
        if (scanSwitch) {
            reset();
        }
        return resourceDataPutMap;
    }

    public Map<String, String> getResourceDataDeleteMap() {
        if (scanSwitch) {
            reset();
        }
        return resourceDataDeleteMap;
    }

    public Map<String, String> getEnvironmentMap() {
        if (scanSwitch) {
            reset();
        }
        return environmentMap;
    }

    public void setScanSwitch(boolean scanSwitch) {
        this.scanSwitch = scanSwitch;
    }

    public String getDataWithApi(String requestURI, String method) {
        if (method.equalsIgnoreCase(HttpRequestMethod.GET.name())) {
            return getResourceDataGetMap().get(requestURI);
        } else if (method.equalsIgnoreCase(HttpRequestMethod.POST.name())) {
            return getResourceDataPostMap().get(requestURI);
        } else if (method.equalsIgnoreCase(HttpRequestMethod.PUT.name())) {
            return getResourceDataPutMap().get(requestURI);
        } else if (method.equalsIgnoreCase(HttpRequestMethod.DELETE.name())) {
            return getResourceDataDeleteMap().get(requestURI);
        } else {
            throw new MockRuntimeException("httpMethod 错误");
        }
    }

    public Map<String, Long> getFileLastModified() {
        return fileLastModified;
    }
}
