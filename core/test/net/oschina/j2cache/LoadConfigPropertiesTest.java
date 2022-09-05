package net.oschina.j2cache;

import org.junit.Assert;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.IOException;

/**
 * issue https://gitee.com/ld/J2Cache/issues/I5OOTA 问题1测试
 * 1.兼容旧的指定相对路径的方式读取配置文件j2cache.properies
 * 2.支持优先读取全路径的方式读取配置文件j2cache.properies
 *
 * **/
public class LoadConfigPropertiesTest {


    private final static String CONFIG_FILE_RELATIVE_PATH = "/j2cache.properties";

    private final static String FULL_FILE_RELATIVE_PATH = LoadConfigPropertiesTest.class.getResource("/").getPath()+"j2cache-test.properties";

    //switch path to test load properites
    private final static boolean FULL_PATH_SWITCH = true;

    private static J2CacheConfig config;


    private final static String CAFFEINE_FULL_PATH=LoadConfigPropertiesTest.class.getResource("/").getPath()+"caffeine-test.properties";



    static {
        try {

            if(FULL_PATH_SWITCH)
            {
                //init caffeine path
                System.out.println("j2cache current full path ="+FULL_FILE_RELATIVE_PATH);
                config = J2CacheConfig.initFromConfig(FULL_FILE_RELATIVE_PATH);
            }else{
                System.out.println("current  path ="+CONFIG_FILE_RELATIVE_PATH);
                config = J2CacheConfig.initFromConfig(CONFIG_FILE_RELATIVE_PATH);
            }

        } catch (IOException e) {
            throw new CacheException("Failed to load j2cache configuration " + CONFIG_FILE_RELATIVE_PATH, e);
        }
    }

    public static void main(String[] args) {

        System.out.println("------test load j2cache----");
        testLoadJ2Cache();
        System.out.println("------test load caffine----");
        testLoadCaffine();
    }


    private static void testLoadJ2Cache() {
        //test read j2cache
        Assert.assertTrue(config!=null);
        if(FULL_PATH_SWITCH){
            Assert.assertEquals("${caffeine.path.wait.replace}",config.getProperties().getProperty("caffeine.properties"));
        }else{
            Assert.assertEquals("/caffeine.properties",config.getProperties().getProperty("caffeine.properties"));
        }
    }

    private static void testLoadCaffine() {

            if(FULL_PATH_SWITCH){
                config.getL1CacheProperties().setProperty("properties",CAFFEINE_FULL_PATH);
                CacheProviderHolder holder = CacheProviderHolder.init(config,null);
                System.out.println(holder.getL1Provider().name());
            }else{
                CacheProviderHolder holder = CacheProviderHolder.init(config,null);
                System.out.println(holder.getL1Provider().name());
            }

    }
}
