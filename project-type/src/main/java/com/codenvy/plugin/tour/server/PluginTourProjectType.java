/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.plugin.tour.server;

import com.codenvy.api.project.server.type.ProjectType;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.codenvy.plugin.tour.shared.PluginTourProjectTypeConstants.PLUGIN_TOUR_PROJECT_TYPE_DISPLAY_NAME;
import static com.codenvy.plugin.tour.shared.PluginTourProjectTypeConstants.PLUGIN_TOUR_PROJECT_TYPE_ID;
import static com.codenvy.plugin.tour.shared.PluginTourProjectTypeConstants.PLUGIN_TOUR_ATTRIBUTE_NAME;


/**
 * The Plugin Tour project type definition.
 *
 * @author vetal
 */
@Singleton
public class PluginTourProjectType extends ProjectType {
    @Inject
    public PluginTourProjectType() {
        super(PLUGIN_TOUR_PROJECT_TYPE_ID, PLUGIN_TOUR_PROJECT_TYPE_DISPLAY_NAME, false, true);
        addVariableDefinition(PLUGIN_TOUR_ATTRIBUTE_NAME, "Codenvy GuidedTour marker", false);
    }
}
