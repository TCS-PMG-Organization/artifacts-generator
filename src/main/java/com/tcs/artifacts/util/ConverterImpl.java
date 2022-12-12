package com.tcs.artifacts.util;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.tcs.artifacts.domain.KubernetesDeploymentValues;
import com.tcs.artifacts.domain.SourceApplication;
import com.tcs.artifacts.exception.PipelineArtifactFileCreationException;

public class ConverterImpl implements Converter {

	public static Logger LOGGER = LoggerFactory.getLogger(ConverterImpl.class);

	private Path outputDirectory;

	private KubernetesDeploymentValues kubernetesDeploymentValues;

	private String targetDeploymentType;

	public ConverterImpl(SourceApplication sourceApplication) {

		targetDeploymentType = sourceApplication.getTargetDeploymentType();

		outputDirectory = Paths.get(sourceApplication.getTargetLocation())
				.resolve(sourceApplication.getApplicationName());

		kubernetesDeploymentValues = buildDeploymentArtifactValues(sourceApplication);

	}

	private KubernetesDeploymentValues buildDeploymentArtifactValues(SourceApplication data) {
		KubernetesDeploymentValues kubernetesDeploymentValues = new KubernetesDeploymentValues();

		kubernetesDeploymentValues.setApplicationNameAsReceived(data.getApplicationName());
		kubernetesDeploymentValues.setApplicationName(data.getApplicationName().toLowerCase());
		kubernetesDeploymentValues.setNamespace_Name(data.getNamespaceName());
		kubernetesDeploymentValues.setIngress_Domain(data.getIngressDomain());

		return kubernetesDeploymentValues;
	}

	public boolean mapToKubernetes() {

		generateDockerFile();
		if ("helm".equalsIgnoreCase(targetDeploymentType)) {
			generateDeploymentArtifactsHelm();
		} else {
			generateDeploymentArtifacts();
		}

		return true;
	}

	private boolean generateDeploymentArtifactsHelm() {

		LOGGER.debug("Generating helm artifacts");

		String chart = resolveVelocityTemplate("templates/helm/Chart.yaml.template", kubernetesDeploymentValues);
		LOGGER.debug("Generated Chart yaml :: {}", chart);
		
		String _helpers = resolveVelocityTemplate("templates/helm/_helpers.tpl.template", kubernetesDeploymentValues);
		LOGGER.debug("Generated helpers tpl :: {}", _helpers);

		String deployment = resolveVelocityTemplate("templates/helm/deployment.yaml.template",
				kubernetesDeploymentValues);
		LOGGER.debug("Generated deployment yaml :: {}", deployment);

		String hpa = resolveVelocityTemplate("templates/helm/hpa.yaml.template", kubernetesDeploymentValues);
		LOGGER.debug("Generated hpa yaml :: {}", hpa);

		String ingress = resolveVelocityTemplate("templates/helm/ingress.yaml.template", kubernetesDeploymentValues);
		LOGGER.debug("Generated ingress yaml:: {}", ingress);
		
		String notes = resolveVelocityTemplate("templates/helm/NOTES.txt.template", kubernetesDeploymentValues);
		LOGGER.debug("Generated NOTES txt:: {}", notes);

		String service = resolveVelocityTemplate("templates/helm/service.yaml.template", kubernetesDeploymentValues);
		LOGGER.debug("Generated service yaml:: {}", service);

		String serviceAccount = resolveVelocityTemplate("templates/helm/serviceaccount.yaml.template",
				kubernetesDeploymentValues);
		LOGGER.debug("Generated serviceaccount yaml:: {}", serviceAccount);

		String testConnection = resolveVelocityTemplate("templates/helm/test-connection.yaml.template",
				kubernetesDeploymentValues);
		LOGGER.debug("Generated test-connection yaml:: {}", testConnection);

		String values = resolveVelocityTemplate("templates/helm/values.yaml.template", kubernetesDeploymentValues);
		LOGGER.debug("Generated values yaml:: {}", values);
		
		String pipeline = resolveVelocityTemplate("templates/helm/pipeline-docker-build-and-deploy-pr-working.yaml.template", kubernetesDeploymentValues);
		LOGGER.debug("Generated pipeline yaml:: {}", pipeline);

		Path templatesPath = outputDirectory.resolve("templates");
		Path templatesTestPath = templatesPath.resolve("tests");
		
		try {

			CommonJavaUtil.writeToFile(outputDirectory, "Chart.yaml", chart);
			CommonJavaUtil.writeToFile(templatesPath, "_helpers.tpl", _helpers);
			CommonJavaUtil.writeToFile(templatesPath, "deployment.yaml", deployment);
			CommonJavaUtil.writeToFile(templatesPath, "hpa.yaml", hpa);
			CommonJavaUtil.writeToFile(templatesPath, "ingress.yaml", ingress);
			CommonJavaUtil.writeToFile(templatesPath, "NOTES.txt", notes);
			CommonJavaUtil.writeToFile(templatesPath, "service.yaml", service);
			CommonJavaUtil.writeToFile(templatesPath, "serviceaccount.yaml", serviceAccount);
			CommonJavaUtil.writeToFile(templatesTestPath, "test-connection.yaml", testConnection);
			CommonJavaUtil.writeToFile(outputDirectory, "values.yaml", values);
			CommonJavaUtil.writeToFile(outputDirectory, "pipeline-docker-build-and-deploy-pr-working.yaml", pipeline);

			return true;
		} catch (PipelineArtifactFileCreationException e) {
			LOGGER.error("Error during generation of helm artifacts-", e);
			return false;
		}
	}

	private boolean generateDeploymentArtifacts() {

		LOGGER.debug("Generating artifacts");

		String deployment = resolveVelocityTemplate("templates/deployment-configs/deployment.yaml.template",
				kubernetesDeploymentValues);
		LOGGER.debug("Generated deployment service :: {}", deployment);

		String service = resolveVelocityTemplate("templates/deployment-configs/service.yaml.template",
				kubernetesDeploymentValues);
		LOGGER.debug("Generated service yaml:: {}", service);

		String pipeline = resolveVelocityTemplate("templates/deployment-configs/.rancher-pipeline.yml.template",
				kubernetesDeploymentValues);
		LOGGER.debug("Generated pipeline yaml:: {}", pipeline);

		String route = resolveVelocityTemplate("templates/deployment-configs/route.yaml.template", kubernetesDeploymentValues);
		LOGGER.debug("Generated route yaml:: {}", route);

		try {
			CommonJavaUtil.writeToFile(outputDirectory, "deployment.yaml", deployment);
			CommonJavaUtil.writeToFile(outputDirectory, "service.yaml", service);
			CommonJavaUtil.writeToFile(outputDirectory, ".rancher-pipeline.yml", pipeline);
			CommonJavaUtil.writeToFile(outputDirectory, "route.yaml", route);
			return true;
		} catch (PipelineArtifactFileCreationException e) {
			LOGGER.error("Error during generation of artifacts-", e);
			return false;
		}
	}

	private boolean generateDockerFile() {

		System.out.println("Generating docker");

		String template = "templates/deployment-configs/Dockerfile.template";

		String dockerFile = resolveVelocityTemplate(template, kubernetesDeploymentValues);
		LOGGER.debug("Generated Dockerfile :: {}", dockerFile);
		try {
			CommonJavaUtil.writeToFile(outputDirectory, "Dockerfile", dockerFile);
			return true;
		} catch (PipelineArtifactFileCreationException e) {
			LOGGER.error("Error during generation of dockerfile-", e);
			return false;
		}
	}

	/**
	 * Resolves Mustache template with passed values.
	 * 
	 * @param dockerValues
	 * @return Returns the resolved template.
	 */
	public String resolveTemplate(String templatePath, Object dockerValues) {
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache mustache = mf.compile(templatePath);
		StringWriter writer = new StringWriter();
		try {
			mustache.execute(writer, dockerValues).flush();
		} catch (IOException e) {
			LOGGER.error("Error during resolveTemplate-" + templatePath, e);
		}

		return writer.toString();
	}

	/**
	 * Resolves Velocity template with passed values.
	 * 
	 * @param dockerValues
	 * @return Returns the resolved template.
	 */
	public String resolveVelocityTemplate(String templatePath, Object dockerValues) {
		Template template = null;
		StringWriter writer = new StringWriter();

		try {

			VelocityEngine velocityEngine = new VelocityEngine();
			velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

			velocityEngine.init();

			VelocityContext context = new VelocityContext();

			context.put("dockerValues", dockerValues);
			template = velocityEngine.getTemplate(templatePath);
			template.merge(context, writer);
		} catch (Exception e) {
			LOGGER.error("Exception during resolveVelocityTemplate:" + e.getMessage());
		}

		return writer.toString();

	}

}
