package com.cloud.deploy;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;

import com.cloud.capacity.CapacityManager;
import com.cloud.dc.ClusterVO;
import com.cloud.dc.DataCenter;
import com.cloud.dc.Pod;
import com.cloud.dc.dao.ClusterDao;
import com.cloud.dc.dao.DataCenterDao;
import com.cloud.dc.dao.HostPodDao;
import com.cloud.exception.InsufficientServerCapacityException;
import com.cloud.host.Host;
import com.cloud.host.HostVO;
import com.cloud.host.Status;
import com.cloud.host.dao.HostDao;
import com.cloud.hypervisor.Hypervisor.HypervisorType;
import com.cloud.offering.ServiceOffering;
import com.cloud.org.Cluster;
import com.cloud.utils.component.Inject;
import com.cloud.utils.exception.CloudRuntimeException;
import com.cloud.vm.VirtualMachine;
import com.cloud.vm.VirtualMachineProfile;
import com.cloud.vm.VirtualMachineProfileImpl;

@Local(value=DeploymentPlanner.class)
public class BareMetalPlanner implements DeploymentPlanner {
	private static final Logger s_logger = Logger.getLogger(BareMetalPlanner.class);
	@Inject protected DataCenterDao _dcDao;
	@Inject protected HostPodDao _podDao;
	@Inject protected ClusterDao _clusterDao;
	@Inject protected HostDao _hostDao;
	@Inject protected CapacityManager _capacityMgr;
	String _name;
	
	@Override
	public DeployDestination plan(VirtualMachineProfile vmProfile, DeploymentPlan plan, ExcludeList avoid) throws InsufficientServerCapacityException {
		VirtualMachine vm = vmProfile.getVirtualMachine();
		ServiceOffering offering = vmProfile.getServiceOffering();	
		String hostTag = null;
		
		if (offering.getTags() != null) {
			String[] tags = offering.getTags().split(",");
			if (tags.length > 0) {
				hostTag = tags[0];
			}
		}
		
		List<ClusterVO> clusters = _clusterDao.listByDcHyType(vm.getDataCenterId(), HypervisorType.BareMetal.toString());
		if (clusters.size() != 1) {
			throw new CloudRuntimeException("Invaild baremetal cluster number " + clusters.size());
		}
		Cluster cluster = clusters.get(0);
		
		int cpu_requested;
		long ram_requested;
		HostVO target = null;
		List<HostVO> hosts = _hostDao.listByCluster(cluster.getId());
		if (hostTag != null) {
			for (HostVO h : hosts) {
				_hostDao.loadDetails(h);
				if (h.getDetail("hostTag") != null && h.getDetail("hostTag").equalsIgnoreCase(hostTag)) {
					target = h;
					break;
				}
			}
		}

		if (target == null) {
			s_logger.warn("Cannot find host with tag " + hostTag + " use capacity from service offering");
			cpu_requested = offering.getCpu() * offering.getSpeed();
			ram_requested = offering.getRamSize() * 1024 * 1024;
		} else {
			cpu_requested = target.getCpus() * target.getSpeed().intValue();
			ram_requested = target.getTotalMemory();
		}
		
		for (HostVO h : hosts) {
			if (h.getStatus() == Status.Up) {
				if(_capacityMgr.checkIfHostHasCapacity(h.getId(), cpu_requested, ram_requested, false)){
					s_logger.debug("Find host " + h.getId() + " has enough capacity");
					DataCenter dc = _dcDao.findById(h.getDataCenterId());
					Pod pod = _podDao.findById(h.getPodId());
					return new DeployDestination(dc, pod, cluster, h);
				}
			}
		}
		
		s_logger.warn(String.format("Cannot find enough capacity(requested cpu=%1$s memory=%2$s)", cpu_requested, ram_requested));
		return null;
	}

	@Override
	public boolean canHandle(VirtualMachineProfile<? extends VirtualMachine> vm, DeploymentPlan plan, ExcludeList avoid) {
		return vm.getHypervisorType() == HypervisorType.BareMetal;
	}

	@Override
	public boolean configure(String name, Map<String, Object> params) throws ConfigurationException {
		_name = name;
		return true;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public boolean start() {
		return true;
	}

	@Override
	public boolean stop() {
		return true;
	}

	@Override
	public boolean check(VirtualMachineProfile<? extends VirtualMachine> vm, DeploymentPlan plan, DeployDestination dest, ExcludeList exclude) {
		// TODO Auto-generated method stub
		return false;
	}
}
