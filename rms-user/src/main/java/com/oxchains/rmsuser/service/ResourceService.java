package com.oxchains.rmsuser.service;

import com.oxchains.rmsuser.common.RestResp;
import com.oxchains.rmsuser.common.RestRespPage;
import com.oxchains.rmsuser.dao.ResourceRepo;
import com.oxchains.rmsuser.entity.ResourceVO;
import com.sun.org.apache.regexp.internal.RE;
import jdk.management.resource.ResourceAccuracy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ccl
 * @time 2018-02-02 15:18
 * @name ResourceService
 * @desc:
 */
@Slf4j
@Service
public class ResourceService {
    @Resource
    private ResourceRepo resourceRepo;

    public RestResp list(){
        try{
            Iterable<com.oxchains.rmsuser.entity.Resource> list = resourceRepo.findAll();
            return RestResp.success(list);
        }catch (Exception e){
            return RestResp.fail("查询失败");
        }

    }

    public RestResp list(Integer pageNo,Integer pageSize){
        pageNo = pageNo == null?1:pageNo;
        pageSize = pageSize == null ?10 :pageSize;
        Pageable pager = new PageRequest((pageNo-1)*pageSize, pageSize);
        try{
            Page<com.oxchains.rmsuser.entity.Resource> page = resourceRepo.findAll(pager);
            return RestRespPage.success(page.getContent(),page.getTotalElements());
        }catch (Exception e){
            return RestResp.fail("查询失败");
        }
    }

    public RestResp list(Long userId, Integer pageNo,Integer pageSize){
        pageNo = pageNo == null?1:pageNo;
        pageSize = pageSize == null ?10 :pageSize;
        Pageable pager = new PageRequest((pageNo-1)*pageSize, pageSize);
        try{
            Page<com.oxchains.rmsuser.entity.Resource> page = resourceRepo.findAll(pager);
            return RestRespPage.success(transferVOList(page.getContent()),page.getTotalElements());
        }catch (Exception e){
            return RestResp.fail("查询失败");
        }
    }

    public RestResp add(ResourceVO vo){
        if(vo == null || null == vo.getPresourceName() || "".equals(vo.getPresourceName().trim()) ||
                null == vo.getResourceSign() || "".equals(vo.getResourceSign().trim())){
            return RestResp.fail();
        }
        com.oxchains.rmsuser.entity.Resource resource = getResource(vo);
        if(null!=resource){
            return RestResp.fail("资源存在，请勿重复添加");
        }
        resource = resourceRepo.save(vo.vo2Resource());
        return RestResp.success("操作成功",new ResourceVO(resource));
    }

    public RestResp update(ResourceVO vo){
        if(null == vo){
            return RestResp.fail("请正确填写信息");
        }
        try{
            com.oxchains.rmsuser.entity.Resource resource = resourceRepo.save(vo.vo2Resource());
            return RestResp.success("操作成功",new ResourceVO(resource));
        }catch (Exception e){
            log.error("操作失败",e);
            return RestResp.fail("操作失败");
        }
    }
    public RestResp delete(Long resourceId){
        if(resourceId == null ){
            return RestResp.fail();
        }
        try{
            resourceRepo.delete(resourceId);
            return RestResp.success("操作成功");
        }catch (Exception e){
            log.error("操作失败",e);
            return RestResp.fail("操作失败");
        }

    }
    public com.oxchains.rmsuser.entity.Resource getResource(ResourceVO vo){
        if(null != vo.getId()){
            return resourceRepo.findOne(vo.getId());
        }
        if(null != vo.getResourceSign() && !"".equals(vo.getResourceSign().trim())){
            return  resourceRepo.findByResourceSign(vo.getResourceSign());
        }
        return null;
    }

    public List<ResourceVO> transferVOList(List<com.oxchains.rmsuser.entity.Resource> list){
        if(null == list || list.size()<=0){
            return null;
        }
        List<ResourceVO> resourceVOS = new ArrayList<>(list.size());
        for(com.oxchains.rmsuser.entity.Resource resource : list){
            resourceVOS.add(new ResourceVO(resource));
        }
        return resourceVOS;
    }
}
