/*******************************************************************************
 * Copyright (c) 2007 - 2010 GreenDeltaTC. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Mozilla
 * Public License v1.1 which accompanies this distribution, and is available at
 * http://www.openlca.org/uploads/media/MPL-1.1.html
 * 
 * Contributors: GreenDeltaTC - initial API and implementation
 * www.greendeltatc.com tel.: +49 30 4849 6030 mail: gdtc@greendeltatc.com
 ******************************************************************************/
package org.openlca.core.application.views.navigator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;
import org.openlca.core.application.navigation.CategoryNavigationElement;
import org.openlca.core.application.navigation.INavigationElement;
import org.openlca.core.application.navigation.ModelNavigationElement;
import org.openlca.core.application.plugin.Activator;

/**
 * Extension of the {@link CommonDropAdapterAssistant} to support drop
 * assistance for the common viewer of the applications navigator
 * 
 * @author Sebastian Greve
 * 
 */

public class NavigationDropAssistant extends CommonDropAdapterAssistant {

	/**
	 * The type of operation
	 */
	private int operation;

	/**
	 * Default constructor
	 */
	public NavigationDropAssistant() {
		// nothing to initialize
	}

	@Override
	public IStatus handleDrop(final CommonDropAdapter aDropAdapter,
			final DropTargetEvent aDropTargetEvent, final Object aTarget) {
		// get navigator
		final Navigator navigator = (Navigator) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage()
				.findView("org.openlca.core.application.navigator"); //$NON-NLS-1$
		// drop target
		final DropTarget target = (DropTarget) aDropTargetEvent.getSource();
		// target category
		final CategoryNavigationElement targetElement = (CategoryNavigationElement) aTarget;

		// if target control is the navigator tree
		if (target.getControl() == navigator.getCommonViewer().getTree()) {

			final List<INavigationElement> elements = new ArrayList<>();
			final IStructuredSelection selection = (IStructuredSelection) aDropTargetEvent.data;

			// for each selected object
			for (final Object o : selection.toArray()) {
				// if model component or category element
				if (o instanceof ModelNavigationElement
						|| o instanceof CategoryNavigationElement) {
					elements.add((INavigationElement) o);
				}
			}
			if (operation == DND.DROP_COPY) {
				// copy
				CopyPasteManager.getInstance()
						.copy(elements.toArray(new INavigationElement[elements
								.size()]));
			} else {
				// cut
				CopyPasteManager.getInstance()
						.cut(elements.toArray(new INavigationElement[elements
								.size()]));
			}
			// paste
			CopyPasteManager.getInstance().paste(targetElement);
		}
		return null;
	}

	@Override
	public boolean isSupportedType(final TransferData aTransferType) {
		return true;
	}

	@Override
	public IStatus validateDrop(final Object target, final int operation,
			final TransferData transferType) {
		this.operation = operation;
		IStatus status = null;
		if (target instanceof CategoryNavigationElement) {
			status = new Status(IStatus.OK, Activator.PLUGIN_ID, ""); //$NON-NLS-1$
		}
		return status;
	}

}
