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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cloud.api.BaseListCmd;
import com.cloud.api.Implementation;
import com.cloud.api.Parameter;
import com.cloud.api.ResponseObject;
import com.cloud.api.response.AsyncJobResponse;
import com.cloud.api.response.ListResponse;
import com.cloud.async.AsyncJobVO;
import com.cloud.serializer.SerializerHelper;

@Implementation(method="searchForAsyncJobs", description="Lists all pending asynchronous jobs for the account.")
public class ListAsyncJobsCmd extends BaseListCmd {
    private static final String s_name = "listasyncjobsresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @Parameter(name="account", type=CommandType.STRING, description="the account associated with the async job. Must be used with the domainId parameter.")
    private String accountName;

    @Parameter(name="domainid", type=CommandType.LONG, description="the domain ID associated with the async job.  If used with the account parameter, returns async jobs for the account in the specified domain.")
    private Long domainId;

    @Parameter(name="startdate", type=CommandType.TZDATE, description="the start date of the async job")
    private Date startDate;


    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public String getAccountName() {
        return accountName;
    }

    public Long getDomainId() {
        return domainId;
    }

    public Date getStartDate() {
        return startDate;
    }

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////

    @Override
    public String getName() {
        return s_name;
    }

    @Override @SuppressWarnings("unchecked")
    public ListResponse<AsyncJobResponse> getResponse() {
        List<AsyncJobVO> jobs = (List<AsyncJobVO>)getResponseObject();

        ListResponse<AsyncJobResponse> response = new ListResponse<AsyncJobResponse>();
        List<AsyncJobResponse> jobResponses = new ArrayList<AsyncJobResponse>();
        for (AsyncJobVO job : jobs) {
            AsyncJobResponse jobResponse = new AsyncJobResponse();
            jobResponse.setAccountId(job.getAccountId());
            jobResponse.setCmd(job.getCmd());
            jobResponse.setCreated(job.getCreated());
            jobResponse.setId(job.getId());
            jobResponse.setJobInstanceId(job.getInstanceId());
            jobResponse.setJobInstanceType(job.getInstanceType());
            jobResponse.setJobProcStatus(job.getProcessStatus());
            jobResponse.setJobResult((ResponseObject)SerializerHelper.fromSerializedString(job.getResult()));
            jobResponse.setJobResultCode(job.getResultCode());
            jobResponse.setJobStatus(job.getStatus());
            jobResponse.setUserId(job.getUserId());

            jobResponse.setResponseName("asyncjobs");
            jobResponses.add(jobResponse);
        }

        response.setResponses(jobResponses);
        response.setResponseName(getName());
        return response;
    }
}
