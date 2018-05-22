package com.oxchains.message.common;


import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author luoxuri
 * @create 2018-03-01 17:23
 **/
@Service
@Data
public class MapCacheManager {

    /**
     * 所有未读消息缓存map
     * int[]索引
     * 0：所有未读消息
     * 1：公告未读消息
     * 2：系统未读消息
     * 3：私信未读
     * 4：是否有新消息，0：无，1：有
     */
    private Map<Long, int[]> countMap = new HashMap<>();

}
