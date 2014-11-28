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
 * Defines a representation of a Step tour in Codenvy
 * @author Florent Benoit
 */
@DTO
public interface GuidedTourStep {

    String getTitle();
    void setTitle(String title);
    GuidedTourStep withTitle(String title);

    String getContent();
    void setContent(String content);
    GuidedTourStep withContent(String content);

    String getElement();
    void setElement(String element);
    GuidedTourStep withElement(String element);

    String getPlacement();
    void setPlacement(String placement);
    GuidedTourStep withPlacement(String placement);

    String getXOffset();
    void setXOffset(String xOffset);
    GuidedTourStep withXOffset(String xOffset);

    List<GuidedTourAction> getActions();
    void setActions(List<GuidedTourAction> actions);
    GuidedTourStep withActions(List<GuidedTourAction> actions);
}
