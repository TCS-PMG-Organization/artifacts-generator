package com.tcs.artifacts.domain;

import java.util.Map.Entry;
import java.util.Set;

public class KubernetesDeploymentValues {

	private String applicationName;
	private String applicationNameAsReceived;
	private String namespace_Name;
	private String ingress_Domain;

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getApplicationNameAsReceived() {
		return applicationNameAsReceived;
	}

	public void setApplicationNameAsReceived(String applicationNameAsReceived) {
		this.applicationNameAsReceived = applicationNameAsReceived;
	}

	public String getNamespace_Name() {
		return namespace_Name;
	}

	public void setNamespace_Name(String namespace_Name) {
		this.namespace_Name = namespace_Name;
	}

	public String getIngress_Domain() {
		return ingress_Domain;
	}

	public void setIngress_Domain(String ingress_Domain) {
		this.ingress_Domain = ingress_Domain;
	}

}
