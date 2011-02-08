/**
 * Copyright 2010 OpenEngSB Division, Vienna University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openengsb.openengsbplugin.base;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.openengsb.openengsbplugin.tools.Tools;
import org.openengsb.openengsbplugin.xml.OpenEngSBMavenPluginNSContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class ConfiguredMojo extends MavenExecutorMojo {

    private static final Logger LOG = Logger.getLogger(ConfiguredMojo.class);

    // #################################
    // set these in subclass constructor
    // #################################

    protected String configProfileXpath;
    protected String configPath;

    // #################################

    protected String cocProfile;

    private static final OpenEngSBMavenPluginNSContext NS_CONTEXT = new OpenEngSBMavenPluginNSContext();
    private static final String POM_PROFILE_XPATH = "/pom:project/pom:profiles";

    protected static final List<File> FILES_TO_REMOVE_FINALLY = new ArrayList<File>();
    
    private File backupOriginalPom;

    /**
     * If set to "true" prints the temporary pom to the console.
     * 
     * @parameter expression="${debugMode}" default-value="false"
     */
    private boolean debugMode;

    @Override
    protected final void configure() throws MojoExecutionException {
        LOG.trace("-> configure");
        cocProfile = UUID.randomUUID().toString();
        configureTmpPom(cocProfile);
        configureCoCMojo();
    }

    @Override
    protected void postExecFinally() {
        restoreOriginalPom();
        cleanUp();
    }
    
    private void restoreOriginalPom() {
        try {
            FileUtils.copyFile(backupOriginalPom, getSession().getRequest().getPom());
        } catch (Exception e) {
            // do nothing
        }
    }

    protected abstract void configureCoCMojo() throws MojoExecutionException;

    private void configureTmpPom(String profileName) throws MojoExecutionException {
        try {
            
            backupOriginalPom = backupOriginalPom(getSession().getRequest().getPom());
            FILES_TO_REMOVE_FINALLY.add(backupOriginalPom);
            
            Document pomDocumentToConfigure = parseProjectPom();
            Document configDocument = parseDefaultConfiguration();

            insertConfigProfileIntoOrigPom(pomDocumentToConfigure, configDocument, profileName);
            
            serializeIntoPom(pomDocumentToConfigure);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            throw new MojoExecutionException("Couldn't configure temporary pom for this execution!", e);
        }
    }

    private Document parseProjectPom() throws Exception {
        return Tools.parseXMLFromString(FileUtils.readFileToString(getSession().getRequest().getPom()));
    }

    private Document parseDefaultConfiguration() throws Exception {
        return Tools.parseXMLFromString(IOUtils.toString(getClass().getClassLoader().getResourceAsStream(configPath)));
    }

    private void insertConfigProfileIntoOrigPom(Document originalPom, Document mojoConfiguration, String profileName)
        throws XPathExpressionException {
        Node licenseCheckMojoProfileNode = Tools.evaluateXPath(configProfileXpath, mojoConfiguration, NS_CONTEXT,
                XPathConstants.NODE, Node.class);

        Node idNode = mojoConfiguration.createElement("id");
        idNode.setTextContent(profileName);
        licenseCheckMojoProfileNode.insertBefore(idNode, licenseCheckMojoProfileNode.getFirstChild());

        Node importedLicenseCheckProfileNode = originalPom.importNode(licenseCheckMojoProfileNode, true);

        Tools.insertDomNode(originalPom, importedLicenseCheckProfileNode, POM_PROFILE_XPATH, NS_CONTEXT);
    }

    private void serializeIntoPom(Document pomDocument) throws IOException, URISyntaxException {
        String serializedXml = Tools.serializeXML(pomDocument);

        if (debugMode) {
            System.out.print(serializedXml);
        }

        FileUtils.writeStringToFile(getSession().getRequest().getPom(), serializedXml + "\n");
    }

    private void cleanUp() {
        LOG.trace("-> cleanup()");
        for (File f : FILES_TO_REMOVE_FINALLY) {
            FileUtils.deleteQuietly(f);
        }
    }
    
    private File backupOriginalPom(File originalPom) throws IOException {
        return Tools.generateTmpFile(FileUtils.readFileToString(originalPom), ".xml");
    }

}
