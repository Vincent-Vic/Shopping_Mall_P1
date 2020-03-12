package cn.iflags.Mall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 描述:答案token时长
 * @author Vincent Vic
 * @create 2020-02-14 21:49
 */


public class TokenChache {
    //token前缀
    private static final String TOKEN_FPREFIX ="token_";
    //日志
    private static Logger  logger =  LoggerFactory.getLogger(TokenChache.class);
    //内存块,时长控制
    private static LoadingCache<String,String> loadingCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(1, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                //默认加载实现，如果key没有对应值的话，加载这个方法
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });
    public static void setKey(String key,String value){
        loadingCache.put(key,value);
    }

    public static String getKey(String key){
        String value = null;
        try {
            value = loadingCache.get(key);
            if ("null".equals(value)){
                return null;
            }
            return value;
        } catch (ExecutionException e) {
            //e.printStackTrace();
            logger.error("localCache get error",e);
        }
        return null;
    }

    public static String getTokenFprefix() {
        return TOKEN_FPREFIX;
    }
}