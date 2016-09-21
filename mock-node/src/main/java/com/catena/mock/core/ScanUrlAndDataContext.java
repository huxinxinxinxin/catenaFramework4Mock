package com.catena.mock.core;

import com.catena.mock.MockRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.*;
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
    public static final String DATA_CONTENT_GET_KEY = "data.content.get.key";
    public static final String DATA_CONTENT_POST_KEY = "data.content.post.key";
    public static final String DATA_CONTENT_PUT_KEY = "data.content.put.key";
    public static final String DATA_CONTENT_DELETE_KEY = "data.content.delete.key";
    private List<Map.Entry<String, String>> allKeyUrl = new CopyOnWriteArrayList<>();
    private Map<String, String> resourceUrlMap = new HashMap<>();
    private Map<String, String> resourceDataGetMap = new HashMap<>();
    private Map<String, String> resourceDataPostMap = new HashMap<>();
    private Map<String, String> resourceDataPutMap = new HashMap<>();
    private Map<String, String> resourceDataDeleteMap = new HashMap<>();
    private Map<String, String> resourceContentGetMap = new HashMap<>();
    private Map<String, String> resourceContentPostMap = new HashMap<>();
    private Map<String, String> resourceContentPutMap = new HashMap<>();
    private Map<String, String> resourceContentDeleteMap = new HashMap<>();
    private Map<String, String> environmentMap = new HashMap<>();
    private Map<String, Long> fileLastModified = new HashMap<>();
    private boolean scanSwitch = false;
    private static Object object = new Object();

    public ScanUrlAndDataContext() throws IOException {
        loadEnvironment();
        load();
        scanLastModified();
    }

    private void scanLastModified() throws IOException {
        File rootDir = new File(FILE_PATH);
        for (File file : rootDir.listFiles()) {
            fileLastModified.put(file.getName(), file.lastModified());
        }
    }

    public static ScanUrlAndDataContext reset() {
        try {
            return new ScanUrlAndDataContext();
        } catch (IOException e) {
            throw new MockRuntimeException(501);
        }
    }

    private void loadEnvironment() throws IOException {
        File file = new File("projectEnvironment/environment.properties");
        if (!file.exists()) {
            LOGGER.error("projectEnvironment/environment.properties 文件不存在manw");
            throw new MockRuntimeException(404, "环境配置文件environment不存在请创建");
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
        scanFile(files, true);
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

    public void scanFile(List<File> files, boolean isFirstScan) throws IOException {
        for (File file : files) {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            if (!isFirstScan) {
                LOGGER.info("有文件被修改, 文件名 : {}", file.getName());
            }
            while (true) {
                String s = br.readLine();
                if (s != null) {
                    analyzeMockLineStr(new StringBuilder().append(s), isFirstScan);
                } else
                    break;
            }
            br.close();
        }
    }


    private void analyzeMockLineStr(StringBuilder lineStr, boolean isFirstScan) {
        String str = lineStr.toString().replace("\n", "");
        if (str.length() > 0 && !Objects.equals(str.substring(0, 1), "#")) {
            int indexPoint = str.indexOf('.');
            int indexEqual = str.indexOf('=') + 1;
            String beforePt = str.substring(indexPoint + 1, indexEqual - 1);
            String afterPt = str.substring(indexEqual);
            String apiKey = str.substring(0, indexPoint);
            buildUrlMap(apiKey, beforePt, afterPt, isFirstScan);
        }
    }

    private void buildUrlMap(String apiKey, String beforePt, String afterPt, boolean isFirstScan) {
        if (Objects.equals(apiKey, environmentMap.get(API_KEY))) {
            if (isFirstScan) {
                allKeyUrl.add(new AbstractMap.SimpleEntry<>(beforePt, afterPt));
            } else {
                if(allKeyUrl.stream().allMatch(entry -> !(entry.getKey().equals(beforePt) && entry.getValue().equals(afterPt)))){
                    allKeyUrl.add(new AbstractMap.SimpleEntry<>(beforePt, afterPt));
                }
            }
            resourceUrlMap.put(beforePt, afterPt);
        }
        buildDataMap(apiKey, beforePt, afterPt, isFirstScan);
        buildContentMap(apiKey, beforePt, afterPt);
    }

    private void buildDataMap(String apiKey, String beforePt, String afterPt, boolean isFirstScan) {
        String info = "有api重复 : {}";
        if (Objects.equals(apiKey, environmentMap.get(DATA_GET_KEY))) {
            if (Objects.nonNull(resourceDataGetMap.get(resourceUrlMap.get(beforePt))) && isFirstScan) {
                LOGGER.info(info, resourceUrlMap.get(beforePt));
            }
            resourceDataGetMap.put(resourceUrlMap.get(beforePt), afterPt);
        } else if (Objects.equals(apiKey, environmentMap.get(DATA_POST_KEY))) {
            if (Objects.nonNull(resourceDataPostMap.get(resourceUrlMap.get(beforePt))) && isFirstScan) {
                LOGGER.info(info, resourceUrlMap.get(beforePt));
            }
            resourceDataPostMap.put(resourceUrlMap.get(beforePt), afterPt);
        } else if (Objects.equals(apiKey, environmentMap.get(DATA_PUT_KEY))) {
            if (Objects.nonNull(resourceDataPutMap.get(resourceUrlMap.get(beforePt))) && isFirstScan) {
                LOGGER.info(info, resourceUrlMap.get(beforePt));
            }
            resourceDataPutMap.put(resourceUrlMap.get(beforePt), afterPt);
        } else if (Objects.equals(apiKey, environmentMap.get(DATA_DELETE_KEY))) {
            if (Objects.nonNull(resourceDataDeleteMap.get(resourceUrlMap.get(beforePt))) && isFirstScan) {
                LOGGER.info(info, resourceUrlMap.get(beforePt));
            }
            resourceDataDeleteMap.put(resourceUrlMap.get(beforePt), afterPt);
        }
    }

    private void buildContentMap(String apiKey, String beforePt, String afterPt) {
        if (Objects.equals(apiKey, environmentMap.get(DATA_CONTENT_GET_KEY))) {
            resourceContentGetMap.put(resourceUrlMap.get(beforePt), afterPt);
        } else if (Objects.equals(apiKey, environmentMap.get(DATA_CONTENT_POST_KEY))) {
            resourceContentPostMap.put(resourceUrlMap.get(beforePt), afterPt);
        } else if (Objects.equals(apiKey, environmentMap.get(DATA_CONTENT_PUT_KEY))) {
            resourceContentPutMap.put(resourceUrlMap.get(beforePt), afterPt);
        } else if (Objects.equals(apiKey, environmentMap.get(DATA_CONTENT_DELETE_KEY))) {
            resourceContentDeleteMap.put(resourceUrlMap.get(beforePt), afterPt);
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
            throw new MockRuntimeException(405, "httpMethod 错误");
        }
    }

    public String getContentWithApi(String requestURI, String method) {
        if (method.equalsIgnoreCase(HttpRequestMethod.GET.name())) {
            return getResourceContentGetMap().get(requestURI);
        } else if (method.equalsIgnoreCase(HttpRequestMethod.POST.name())) {
            return getResourceContentPostMap().get(requestURI);
        } else if (method.equalsIgnoreCase(HttpRequestMethod.PUT.name())) {
            return getResourceContentPutMap().get(requestURI);
        } else if (method.equalsIgnoreCase(HttpRequestMethod.DELETE.name())) {
            return getResourceContentDeleteMap().get(requestURI);
        } else {
            throw new MockRuntimeException(405, "httpMethod 错误");
        }
    }

    public Map<String, Long> getFileLastModified() {
        return fileLastModified;
    }

    public List<Map.Entry<String, String>> getAllKeyUrl() {
        return allKeyUrl;
    }

    public void checkScanMockData() throws IOException {
        File rootDir =new File(ScanUrlAndDataContext.FILE_PATH);
        synchronized (object) {
            List<File> files = new CopyOnWriteArrayList<>();
            for (File file : rootDir.listFiles()) {
                validateLastModified(files, file);
            }
            if (!CollectionUtils.isEmpty(files)) {
                this.scanFile(files, false);
            }
        }
    }

    public void validateLastModified(List<File> files, File file) {
        Map<String, Long> fileLastModified = this.getFileLastModified();
        if (Objects.isNull(fileLastModified.get(file.getName()))) {
            files.add(file);
        } else {
            if (!(fileLastModified.get(file.getName()) - file.lastModified() == 0)) {
                fileLastModified.put(file.getName(), file.lastModified());
                files.add(file);
            }
        }
    }

    public Map<String, String> getResourceContentGetMap() {
        return resourceContentGetMap;
    }

    public Map<String, String> getResourceContentPostMap() {
        return resourceContentPostMap;
    }

    public Map<String, String> getResourceContentPutMap() {
        return resourceContentPutMap;
    }

    public Map<String, String> getResourceContentDeleteMap() {
        return resourceContentDeleteMap;
    }
}
