/*************************************************************************
 * Copyright 2009-2013 Eucalyptus Systems, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Please contact Eucalyptus Systems, Inc., 6755 Hollister Ave., Goleta
 * CA 93117, USA or visit http://www.eucalyptus.com/licenses/ if you need
 * additional information or have any questions.
 ************************************************************************/
package com.eucalyptus.loadbalancing.activities;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Entity;

import com.eucalyptus.entities.AbstractPersistent;
import com.eucalyptus.loadbalancing.LoadBalancer;
import com.eucalyptus.loadbalancing.LoadBalancerDnsRecord;
import com.eucalyptus.loadbalancing.LoadBalancerSecurityGroup;
import com.eucalyptus.loadbalancing.LoadBalancerZone;

/**
 * @author Sang-Min Park (spark@eucalyptus.com)
 *
 */
@Entity @javax.persistence.Entity
@PersistenceContext( name = "eucalyptus_loadbalancing" )
@Table( name = "metadata_servo_instance" )
@Cache( usage = CacheConcurrencyStrategy.TRANSACTIONAL )
public class LoadBalancerServoInstance extends AbstractPersistent {
	private static Logger    LOG     = Logger.getLogger( LoadBalancerServoInstance.class );
	@Transient
	private static final long serialVersionUID = 1L;
	
	enum STATE {
		Pending, InService, Error, OutOfService, Retired
	}
	
    @ManyToOne
    @JoinColumn( name = "metadata_zone_fk", nullable=true)
    private LoadBalancerZone zone = null;
    
    @ManyToOne
    @JoinColumn( name = "metadata_group_fk", nullable=true)
    private LoadBalancerSecurityGroup security_group = null;
    
    @ManyToOne
    @JoinColumn( name = "metadata_dns_fk", nullable=true)
    private LoadBalancerDnsRecord dns = null; 
    
    @ManyToOne
    @JoinColumn( name = "metadata_asg_fk", nullable=true)
    private LoadBalancerAutoScalingGroup autoscaling_group = null; 
       
    @Transient
    private LoadBalancer loadbalancer = null;
    
    @Column(name="metadata_instance_id", nullable=false, unique=true)
    private String instanceId = null;
    
    @Column(name="metadata_state", nullable=false)
    private String state = null;
    
    @Column(name="metadata_address", nullable=true)
    private String address = null;

    @Column(name="metadata_private_ip", nullable=true)
    private String privateIp = null;
    
    private LoadBalancerServoInstance(){
    }
    private LoadBalancerServoInstance(final LoadBalancerZone lbzone){
    	this.state = STATE.Pending.name();
    	this.zone = lbzone;
    	this.loadbalancer = zone.getLoadbalancer();
    	this.dns = this.loadbalancer.getDns();
    }
    private LoadBalancerServoInstance(final LoadBalancerZone lbzone, final LoadBalancerSecurityGroup group){
    	this.state = STATE.Pending.name();
    	this.zone = lbzone;
    	this.loadbalancer = zone.getLoadbalancer();
    	this.dns= this.loadbalancer.getDns();
    	this.security_group = group;
    }
    private LoadBalancerServoInstance(final LoadBalancerZone lbzone, final LoadBalancerSecurityGroup group, final LoadBalancerDnsRecord dns){
    	this.state = STATE.Pending.name();
    	this.zone = lbzone;
    	this.loadbalancer = zone.getLoadbalancer();
    	this.security_group = group;
    	this.dns = dns;
    }
    
    public static LoadBalancerServoInstance newInstance(final LoadBalancerZone lbzone, final LoadBalancerSecurityGroup group, final LoadBalancerDnsRecord dns, final LoadBalancerAutoScalingGroup as_group, String instanceId)
    {
    	final LoadBalancerServoInstance instance = new LoadBalancerServoInstance(lbzone, group, dns);
    	instance.setInstanceId(instanceId);
    	instance.setAutoScalingGroup(as_group);
    	return instance;
    }
    
    public static LoadBalancerServoInstance named(final LoadBalancerZone lbzone){
    	final LoadBalancerServoInstance sample = new LoadBalancerServoInstance(lbzone);
    	return sample;
    }
    
    public static LoadBalancerServoInstance named(String instanceId){
    	final LoadBalancerServoInstance sample = new LoadBalancerServoInstance();
    	sample.instanceId = instanceId;
    	return sample;
    }
    
    public static LoadBalancerServoInstance named(){
    	return new LoadBalancerServoInstance();
    }
    public static LoadBalancerServoInstance withState(String state){
    	final LoadBalancerServoInstance sample = new LoadBalancerServoInstance();
    	sample.state = state;
    	return sample;
    }
    
    public LoadBalancerZone getAvailabilityZone(){
    	return this.zone;
    }
    
    public void leaveZone(){
    	this.zone = null;
    }
    
    public void unmapDns(){
    	this.dns = null;
    }
    
    public void setInstanceId(String id){
    	this.instanceId = id;
    }
    
    public String getInstanceId(){
    	return this.instanceId;
    }
    
    public void setState(STATE update){
    	this.state = update.name();
    }
    
    public STATE getState(){
    	return Enum.valueOf(STATE.class, this.state);
    }
    
    public void setAddress(String address){
    	this.address=address;
    }
    
    public String getAddress(){
    	return this.address; 
    }
    
    public void setSecurityGroup(LoadBalancerSecurityGroup group){
    	this.security_group=group;
    }
    
    public void setDns(final LoadBalancerDnsRecord dns){
    	this.dns = dns;
    }

    public LoadBalancerDnsRecord getDns(){
    	return this.dns;
    }
   
    public void setAutoScalingGroup(LoadBalancerAutoScalingGroup group){
    	this.autoscaling_group = group;
    }
    
    public LoadBalancerAutoScalingGroup getAutoScalingGroup(){
    	return this.autoscaling_group;
    }
    
    public void setAvailabilityZone(LoadBalancerZone zone){
    	this.zone = zone;
    }
    
    public String getPrivateIp(){
    	return this.privateIp;
    }
    
    public void setPrivateIp(final String ipAddr){
    	this.privateIp = ipAddr;
    }
    
	@Override
	public String toString(){
		String id = this.instanceId==null? "unassigned" : this.instanceId;
		return String.format("Servo-instance (%s) for loadbalancer %s in %s", id, this.loadbalancer.getDisplayName(), this.zone.getName());
	}
}
