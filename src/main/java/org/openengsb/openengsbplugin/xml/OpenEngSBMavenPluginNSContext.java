/**
 * Licensed to the Austrian Association for Software Tool Integration (AASTI)
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. The AASTI licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openengsb.openengsbplugin.xml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

public class OpenEngSBMavenPluginNSContext implements NamespaceContext {

    private HashMap<String, String> prefixToURI = new HashMap<String, String>();
    private HashMap<String, String> uriToPrefix = new HashMap<String, String>();

    public OpenEngSBMavenPluginNSContext() {
        buildNamingContext();
    }

    private void buildNamingContext() {
        String[][] data = new String[][]{
            { "c", "http://www.openengsb.org/openengsb-maven-plugin/CoCProfile" },
            { "pom", "http://maven.apache.org/POM/4.0.0" } };

        for (String[] strArr : data) {
            prefixToURI.put(strArr[0], strArr[1]);
            uriToPrefix.put(strArr[1], strArr[0]);
        }
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return prefixToURI.get(prefix);
    }

    @Override
    public String getPrefix(String uri) {
        return uriToPrefix.get(uri);
    }

    @Override
    public Iterator<String> getPrefixes(String arg0) {
        HashSet<String> h = new HashSet<String>();
        h.addAll(prefixToURI.keySet());
        return h.iterator();
    }

}
