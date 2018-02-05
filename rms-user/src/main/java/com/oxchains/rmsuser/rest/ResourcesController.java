package com.oxchains.rmsuser.rest;

import com.oxchains.rmsuser.common.RestResp;
import com.oxchains.rmsuser.entity.ResourceVO;
import com.oxchains.rmsuser.service.ResourceService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author ccl
 * @time 2018-02-02 15:08
 * @name ResourcesController
 * @desc:
 */
@RestController
@RequestMapping("/resource")
public class ResourcesController {
    @Resource
    private ResourceService resourceService;

    @GetMapping(value = "/list/{userId}")
    public RestResp list(@PathVariable Long userId,Integer pageNo, Integer pageSize){
        return resourceService.list(userId,pageNo,pageSize);
    }

    @PostMapping(value = "/add")
    public RestResp add(@RequestBody ResourceVO vo){
        return resourceService.add(vo);
    }

    @PostMapping(value = "/update")
    public RestResp update (@RequestBody ResourceVO vo){
        return resourceService.update(vo);
    }

    @DeleteMapping(value = "/delete/{resourceId}")
    public RestResp delete (@PathVariable Long resourceId){
        return resourceService.delete(resourceId);
    }

}
