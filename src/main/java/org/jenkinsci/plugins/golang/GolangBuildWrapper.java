package org.jenkinsci.plugins.golang;


import hudson.*;
import hudson.model.*;
import hudson.tasks.BuildWrapperDescriptor;
import jenkins.tasks.SimpleBuildWrapper;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

public class GolangBuildWrapper extends SimpleBuildWrapper {

    private final String goVersion;

    @DataBoundConstructor
    public GolangBuildWrapper(String goVersion) {
        this.goVersion = goVersion;
    }

    public void setUp(final Context context, final Run<?, ?> run, final FilePath workspace,
                      final Launcher launcher, final TaskListener listener, final EnvVars env)
            throws IOException, InterruptedException {


        GolangInstallation installation = getGoInstallation();
        if(installation != null) {
            // Get the Go version for this node, installing it if necessary
            installation = installation.forNode(workspace.toComputer().getNode(), listener).forEnvironment(env);

            // Apply the GOROOT and go binaries to PATH
            final GolangInstallation install = installation;
            install.buildEnvVars(context);
        }
    }

    private GolangInstallation getGoInstallation() {
        for (GolangInstallation i : ((DescriptorImpl) getDescriptor()).getInstallations()) {
            if (i.getName().equals(goVersion)) {
                return i;
            }
        }
        return null;
    }

    @Extension
    public static class DescriptorImpl extends BuildWrapperDescriptor {

        @CopyOnWrite
        private volatile GolangInstallation[] installations = new GolangInstallation[0];

        public DescriptorImpl() {
            load();
        }

        @Override
        public String getDisplayName() {
            return Messages.SetUpGoTools();
        }

        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        public GolangInstallation[] getInstallations() {
            return installations;
        }

        public void setInstallations(GolangInstallation... installations) {
            this.installations = installations;
            save();
        }

    }

}

