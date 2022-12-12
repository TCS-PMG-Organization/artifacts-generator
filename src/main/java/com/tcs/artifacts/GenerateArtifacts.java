package com.tcs.artifacts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tcs.artifacts.domain.SourceApplication;
import com.tcs.artifacts.util.Converter;
import com.tcs.artifacts.util.ConverterImpl;

public class GenerateArtifacts {

	public static Logger LOGGER = LoggerFactory.getLogger(GenerateArtifacts.class);

	public static void main(String[] args) {

		SourceApplication sourceApplication = null;

		try {

			sourceApplication = getSourceApplication(args);
			String targetDeploymentType = sourceApplication.getTargetDeploymentType();
			String applicationName = sourceApplication.getApplicationName();
			String targetLocation = sourceApplication.getTargetLocation();

			if (null != targetDeploymentType && targetDeploymentType.length() > 0 && null != applicationName
					&& applicationName.length() > 0 && null != targetLocation && targetLocation.length() > 0) {

				Converter converter = new ConverterImpl(sourceApplication);
				converter.mapToKubernetes();
			} else {
				LOGGER.error("Error generating the artifacts= 1.targetDeploymentType- " + targetDeploymentType
						+ ", 2.applicationName- " + applicationName + ", 3.targetLocation- " + targetLocation);
			}
		} catch (Exception e) {
			LOGGER.error("Error during execution of application-" + e.getLocalizedMessage());
		}

	}

	private static SourceApplication getSourceApplication(String[] args) {

		String targetDeploymentType = null;
		String applicationName = null;
		String targetLocation = null;
		String namespaceName = null;
		String ingressDomain = null;

		if (args.length > 0) {
			targetDeploymentType = args[0];
		}

		if (args.length > 1) {
			applicationName = args[1];
		}

		if (args.length > 2) {
			targetLocation = args[2];
		}

		if (args.length > 3) {
			namespaceName = args[3];
		}

		if (args.length > 4) {
			ingressDomain = args[4];
		}

		SourceApplication sourceApplication = new SourceApplication();

		sourceApplication.setTargetDeploymentType(targetDeploymentType);
		sourceApplication.setApplicationName(applicationName);
		sourceApplication.setTargetLocation(targetLocation);
		sourceApplication.setNamespaceName(namespaceName == null ? "" : namespaceName);
		sourceApplication.setIngressDomain(ingressDomain == null ? "" : ingressDomain);

		return sourceApplication;
	}

}
