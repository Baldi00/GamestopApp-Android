package com.gamestop.android.gamestopapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

public class CacheManager {

    private static CacheManager instance;
    private static LruCache<String, Bitmap> memoryCache;

    private CacheManager() {

        // get system available memory
        int maxMemory = (int) (Runtime.getRuntime().maxMemory());

        // set a cache size : 30 MiB, about 20 images
        int cacheSize = 30*1024*1024;

        // if the cache is too big
        if ( cacheSize > maxMemory ){
            cacheSize = maxMemory;
        }

        // set cache dimension & override sizeOf()
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };

    }

    public static CacheManager getInstance () {
        if(instance == null) {
            instance = new CacheManager();
        }

        return instance;
    }

    public void addBitmapToMemCache(String key) {
        final Bitmap bitmap = getBitmapFromMemCache(key);

        if (bitmap == null) {
            addBitmapToMemCache(key,BitmapFactory.decodeFile(key));
        }
    }

    public void addBitmapToMemCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return memoryCache.get(key);
    }

}
