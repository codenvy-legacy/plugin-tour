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

package com.codenvy.plugin.tour.dto;

import com.codenvy.dto.shared.DTO;

import java.util.List;

/**
 * Defines the configuration of Guided Tour
 * @author Florent Benoit
 */
@DTO
public interface GuidedTourConfiguration {

    boolean getDebugMode();

    void setDebugMode(final boolean debugMode);

    GuidedTourConfiguration withDebugMode(final boolean debugMode);


    List<GuidedTourStep> getSteps();
    void setSteps(List<GuidedTourStep> steps);
    GuidedTourConfiguration withSteps(List<GuidedTourStep> steps);


}
