package com.tencent.tbds.api.util.props;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLDecoder;
import java.util.Properties;

/**
 * @author liulv
 * @date 2018/10/24
 *
 * properties类别配置文件读取工具类
 */
public class PropertiesUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtils.class.getSimpleName());

    /**
     * Classpath
     */
    public static String CLASS_PATH = Thread.currentThread().getContextClassLoader().getResource("").getPath();

    /**
     * APP PATH
     */
    public static final String APP_PATH = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "/";

    /**
     * CONFIG_PATH 配置文件目录
     */
    public static String CONFIG_PATH = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "/";

    /**
     * 当前执行命令目录的同级目录config
     */
     public static String RUN_CONFIG_PATH = System.getProperty("user.dir").substring(0,
            System.getProperty("user.dir").lastIndexOf(File.separator)) + File.separator + "config" + File.separator;

    private static Properties loadProperties(String configFile) {
        Properties properties = new Properties();
        InputStream in = null;
        try {
            String configFilePath = CONFIG_PATH + configFile;
            configFilePath = URLDecoder.decode(configFilePath, "UTF-8");
            in = new BufferedInputStream(new FileInputStream(new File(configFilePath)));
            //properties.load(is);//直接这么写，如果properties文件中有汉子，则汉字会乱码。因为未设置编码格式。
            properties.load(new InputStreamReader(in, "utf-8"));
        } catch (Exception e) {
            LOGGER.error("加载配置文件{}异常", configFile, e);
            throw new RuntimeException("加载配置文件 {" + configFile + "} 异常", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return properties;
    }

    public static Properties getProperties(String configFile) {
        try {
            configFile = URLDecoder.decode(configFile, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return loadProperties(configFile);
    }

    public static Properties getProperties() {
        return getProperties("application.properties");
    }

}
