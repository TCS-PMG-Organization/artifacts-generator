package com.tcs.artifacts.domain;

public class SourceApplication {

	private String targetDeploymentType;
	private String applicationName;
	private String targetLocation;
	private String namespaceName;
	private String ingressDomain;

	public String getTargetDeploymentType() {
		return targetDeploymentType;
	}

	public void setTargetDeploymentType(String targetDeploymentType) {
		this.targetDeploymentType = targetDeploymentType;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getTargetLocation() {
		return targetLocation;
	}

	public void setTargetLocation(String targetLocation) {
		this.targetLocation = targetLocation;
	}

	public String getNamespaceName() {
		return namespaceName;
	}

	public void setNamespaceName(String namespaceName) {
		this.namespaceName = namespaceName;
	}

	public String getIngressDomain() {
		return ingressDomain;
	}

	public void setIngressDomain(String ingressDomain) {
		this.ingressDomain = ingressDomain;
	}

}
