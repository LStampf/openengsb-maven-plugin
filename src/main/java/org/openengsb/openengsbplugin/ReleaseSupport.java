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

package org.openengsb.openengsbplugin;

import org.openengsb.openengsbplugin.base.ReleaseMojo;

/**
 * Mojo to perform support releases. This mojo activates the "support" profile in the project, where you can put your
 * configuration for support releases.
 *
 * @goal releaseSupport
 *
 * @inheritedByDefault false
 *
 * @requiresProject true
 *
 * @aggregator true
 */
public class ReleaseSupport extends ReleaseMojo {

    @Override
    protected String getReleaseProfile() {
        return "support";
    }

}
