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

package org.openengsb.tooling.pluginsuite.openengsbplugin;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.openengsb.tooling.pluginsuite.openengsbplugin.tools.Tools;

public class PushVersionTest extends MojoPreparation {

    @Before
    public void buildInvocationCommand() throws Exception {
        prepareGoal("pushVersion");
    }

    @Test
    public void pushVersionOfExampleProject_ShouldPass() throws Exception {
        File pomFile = null;
        try {
            File pomBeforePush = new File("src/test/resources/pushVersion/beforePushVersion.xml");
            pomFile = new File("src/test/resources/pushVersion/pom.xml");
            FileUtils.copyFile(pomBeforePush, pomFile);
            int result = Tools.executeProcess(
                    Arrays.asList(new String[] { mvnCommand, "-e", invocation, "-DdevelopmentVersion=2.0-SNAPSHOT" }),
                    new File("src/test/resources/pushVersion"));
            assertEquals(0, result);
            File pomAfterPush = new File("src/test/resources/pushVersion/afterPushVersion.xml");
            assertEquals(FileUtils.readFileToString(pomAfterPush), FileUtils.readFileToString(pomFile));
        } finally {
            FileUtils.deleteQuietly(pomFile);
        }
    }

}
