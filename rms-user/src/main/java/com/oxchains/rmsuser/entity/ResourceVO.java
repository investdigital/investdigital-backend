package com.oxchains.rmsuser.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * @author ccl
 * @time 2018-02-02 15:08
 * @name Resource
 * @desc:
 */
@Data
public class ResourceVO extends Resource{
    private String presourceName;
    private List<Resource> childResource;

    public ResourceVO() {}
    public ResourceVO(Resource resource) {
        setId(resource.getId());
        setPid(resource.getPid());
        setResourceName(resource.getResourceName());
        setResourceSign(resource.getResourceSign());
        setResourceUrl(resource.getResourceUrl());
        setResourceType(resource.getResourceType());
        setResourceLevel(resource.getResourceLevel());
        setEnabled(resource.getEnabled());
        setDescription(resource.getDescription());
    }
    public Resource vo2Resource() {
        Resource resource = new Resource();

        resource.setId(this.getId());
        resource.setPid(this.getPid());
        resource.setResourceName(this.getResourceName());
        resource.setResourceSign(this.getResourceSign());
        resource.setResourceUrl(this.getResourceUrl());
        resource.setResourceType(this.getResourceType());
        resource.setResourceLevel(this.getResourceLevel());
        resource.setEnabled(this.getEnabled());
        resource.setDescription(this.getDescription());

        return resource;
    }


}
