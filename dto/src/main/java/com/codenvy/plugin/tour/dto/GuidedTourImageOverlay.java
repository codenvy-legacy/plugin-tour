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

/**
 * Defines an image overlay
 * @author Florent Benoit
 */
@DTO
public interface GuidedTourImageOverlay {


    /**
     * @return HTML element on which the overlay will be applied
     */
    String getElement();

    /**
     * Defines the HTML element on which the overlay will be applied
     * @param element the step
     */
    void setElement(String element);

    /**
     * Defines the HTML element on which the overlay will be applied
     * @param element the step
     * @return this
     */
    GuidedTourImageOverlay withElement(String element);


    /**
     * @return URL of the image to be displayed on the overlay
     */
    String getUrl();

    /**
     * Defines the URL of the image to be displayed on the overlay
     * @param url the image URL
     */
    void setUrl(String url);

    /**
     * Defines the URL of the image to be displayed on the overlay
     * @param url the image URL
     * @return this
     */
    GuidedTourImageOverlay withUrl(String url);

    /**
     * @return offset to apply on X axis
     */
    String getXOffset();

    /**
     * Defines shift on X Axis of the overlay
     * @param xOffset the shift on X axis
     */
    void setXOffset(String xOffset);

    /**
     * Defines shift on X Axis of the overlay
     * @param xOffset the shift on X axis
     * @return this
     */
    GuidedTourImageOverlay withXOffset(String xOffset);

    /**
     * @return offset to apply on Y axis
     */
    String getYOffset();

    /**
     * Defines shift on Y Axis of the overlay
     * @param yOffset the shift on Y axis
     */
    void setYOffset(String yOffset);

    /**
     * Defines shift on Y Axis of the overlay
     * @param yOffset the shift on Y axis
     * @return this
     */
    GuidedTourImageOverlay withYOffset(String yOffset);
}
