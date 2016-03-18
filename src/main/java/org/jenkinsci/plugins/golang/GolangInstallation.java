package org.jenkinsci.plugins.golang;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.EnvironmentSpecific;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.slaves.NodeSpecific;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolInstaller;
import hudson.tools.ToolProperty;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildWrapper;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class GolangInstallation extends ToolInstallation implements EnvironmentSpecific<GolangInstallation>,
        NodeSpecific<GolangInstallation> {

    @DataBoundConstructor
    public GolangInstallation(String name, String home, List<? extends ToolProperty<?>> properties) {
        super(name, home, properties);
    }

    public void buildEnvVars(final SimpleBuildWrapper.Context context) {
        String root = getHome();
        if (root != null) {
            context.env("GOROOT", root);
            context.env("PATH+GOROOT_BIN", new File(root, "bin").toString());
        }
    }

    public GolangInstallation forEnvironment(EnvVars environment) {
        return new GolangInstallation(getName(), environment.expand(getHome()), getProperties().toList());
    }

    public GolangInstallation forNode(Node node, TaskListener log) throws IOException, InterruptedException {
        return new GolangInstallation(getName(), translateFor(node, log), getProperties().toList());
    }

    @Extension
    public static class DescriptorImpl extends ToolDescriptor<GolangInstallation> {

        @Override
        public String getDisplayName() {
            return "Go";
        }

        @Override
        public List<? extends ToolInstaller> getDefaultInstallers() {
            return Collections.singletonList(new GolangInstaller(null));
        }

        @Override
        public GolangInstallation[] getInstallations() {
            return Jenkins.getInstance()
                    .getDescriptorByType(GolangBuildWrapper.DescriptorImpl.class)
                    .getInstallations();
        }

        @Override
        public void setInstallations(GolangInstallation... installations) {
            Jenkins.getInstance()
                    .getDescriptorByType(GolangBuildWrapper.DescriptorImpl.class)
                    .setInstallations(installations);
        }

    }

}