package org.openengsb.openengsbplugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.apache.maven.plugin.MojoExecutionException;
import org.openengsb.openengsbplugin.base.ConfiguredMojo;
import org.openengsb.openengsbplugin.base.LicenseMojo;
import org.openengsb.openengsbplugin.tools.MavenExecutor;
import org.w3c.dom.Document;

/**
 *  
 * @goal prePush
 * 
 * @inheritedByDefault false
 * 
 * @requiresProject true
 * 
 * @aggregator true
 * 
 */
public class PrePush extends ConfiguredMojo {
    
    public PrePush() {
        configs.add("license/licenseConfig.xml");
        configs.add("checkstyle/checkstyleConfig.xml");
        configs.add("integrationTest/integrationTestConfig.xml");
    }

    @Override
    protected void configureCoCMojo() throws MojoExecutionException {
        List<String> goals = new ArrayList<String>();
        goals.add("clean");
        goals.add("install");
        
        List<String> activeProfiles = new ArrayList<String>();
        activeProfiles.add(cocProfile);
        
        MavenExecutor prePushMojoExecutor = getNewMavenExecutor(this);
        prePushMojoExecutor.addGoals(goals);
        prePushMojoExecutor.setRecursive(true);
        prePushMojoExecutor.addActivatedProfiles(activeProfiles);
        
        addMavenExecutor(prePushMojoExecutor);
    }
    
    @Override
    protected void modifyMojoConfiguration(Document configuredPom) throws MojoExecutionException {
        try {
            File licenseHeaderFile = LicenseMojo.readHeaderStringAndwriteHeaderIntoTmpFile();
            FILES_TO_REMOVE_FINALLY.add(licenseHeaderFile);
            LicenseMojo.insertGoalAndSetHeaderPath(configuredPom, cocProfileXpath, "check",
                    licenseHeaderFile.getAbsolutePath());
            
            File checkstyleCheckerConfigTmp = Checkstyle.createCheckstyleCheckerConfiguration();
            FILES_TO_REMOVE_FINALLY.add(checkstyleCheckerConfigTmp);
            Checkstyle.insertCheckstyleConfigLocation(configuredPom, cocProfileXpath, checkstyleCheckerConfigTmp);
        } catch (XPathExpressionException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    @Override
    protected void validateIfExecutionIsAllowed() throws MojoExecutionException {
    }

}
