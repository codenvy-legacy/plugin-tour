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

import javax.inject.Singleton;

/**
 * Defines the Guided Tour Project Type
 */
@Singleton
public class GuidedTourProjectType extends ProjectType {

    /**
     * Options for guided tour
     */
    public GuidedTourProjectType() {
        super("GuidedTour", "Guided tour type", false, true);
        addVariableDefinition("codenvyGuidedTour", "Guided Tour attribute", false);
    }
}