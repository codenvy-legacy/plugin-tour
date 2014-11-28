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

package com.codenvy.plugin.tour.client.action.impl;

import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.Presentation;
import com.codenvy.plugin.tour.client.action.ExternalAction;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * External actions for action manager
 * @author Florent Benoit
 */
public class ActionManagerExternalAction implements ExternalAction {

    @Inject
    private ActionManager actionManager;


    @Override
    public boolean accept(String category) {
        return "action".equals(category);
    }

    @Override
    public void execute(@Nonnull String actionId) {
        Action action = actionManager.getAction(actionId);
        if (action != null) {
            ActionEvent e = new ActionEvent("", new Presentation(), actionManager, 0);
            action.actionPerformed(e);
        }
    }
}
