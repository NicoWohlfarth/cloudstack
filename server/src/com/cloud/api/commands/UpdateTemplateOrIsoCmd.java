/**
 *  Copyright (C) 2010 Cloud.com, Inc.  All rights reserved.
 * 
 * This software is licensed under the GNU General Public License v3 or later.
 * 
 * It is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package com.cloud.api.commands;

import org.apache.log4j.Logger;

import com.cloud.api.BaseCmd;
import com.cloud.api.BaseCmd.Manager;
import com.cloud.api.Implementation;
import com.cloud.api.Parameter;

@Implementation(method="updateTemplate", manager=Manager.ManagementServer)
public abstract class UpdateTemplateOrIsoCmd extends BaseCmd {
    public static final Logger s_logger = Logger.getLogger(UpdateIsoCmd.class.getName());

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @Parameter(name="bootable", type=CommandType.BOOLEAN, description="true if image is bootable, false otherwise")
    private Boolean bootable;

    @Parameter(name="displaytext", type=CommandType.STRING, description="the display text of the image")
    private String displayText;

    @Parameter(name="id", type=CommandType.LONG, required=true, description="the ID of the image file")
    private Long id;

    @Parameter(name="name", type=CommandType.STRING, description="the name of the image file")
    private String isoName;

    @Parameter(name="ostypeid", type=CommandType.LONG, description="the ID of the OS type that best represents the OS of this image.")
    private Long osTypeId;
    
    @Parameter(name="format", type=CommandType.STRING, description="the format for the image")
    private String format;
    
    @Parameter(name="passwordenabled", type=CommandType.BOOLEAN, description="true if the image supports the password reset feature; default is false")
    private Boolean passwordEnabled;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public Boolean isBootable() {
        return bootable;
    }

    public String getDisplayText() {
        return displayText;
    }

    public Long getId() {
        return id;
    }

    public String isoName() {
        return isoName;
    }

    public Long getOsTypeId() {
        return osTypeId;
    }
    
    public Boolean isPasswordEnabled() {
        return passwordEnabled;
    }
    
    public String getFormat() {
        return format;
    }

}
