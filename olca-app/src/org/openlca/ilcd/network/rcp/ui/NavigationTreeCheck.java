package org.openlca.ilcd.network.rcp.ui;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.openlca.core.application.navigation.INavigationElement;
import org.openlca.core.application.navigation.ModelNavigationElement;

public class NavigationTreeCheck implements ICheckStateListener {

	private CheckboxTreeViewer viewer;

	public NavigationTreeCheck(CheckboxTreeViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void checkStateChanged(CheckStateChangedEvent event) {
		viewer.getControl().setRedraw(false);
		Object element = event.getElement();
		viewer.setGrayed(element, false);
		if (element instanceof INavigationElement) {
			INavigationElement naviElement = (INavigationElement) element;
			updateChildren(naviElement, event.getChecked());
			updateParent(naviElement);
		}
		viewer.getControl().setRedraw(true);
	}

	private void updateChildren(INavigationElement naviElement, boolean state) {
		for (final INavigationElement child : naviElement.getChildren(false)) {
			viewer.setGrayed(child, false);
			viewer.setChecked(child, state);
			if (!(child instanceof ModelNavigationElement))
				updateChildren(child, state);
		}
	}

	private void updateParent(INavigationElement naviElement) {
		INavigationElement parent = naviElement.getParent();
		if (parent == null)
			return;

		boolean hasCheckedChilds = false;
		boolean allChildsChecked = true;
		for (INavigationElement child : parent.getChildren(false)) {
			boolean isChecked = viewer.getChecked(child);
			boolean isGrayed = viewer.getGrayed(child);
			if (isChecked || isGrayed) {
				hasCheckedChilds = true;
			}
			if (!isChecked || isGrayed) {
				allChildsChecked = false;
			}
		}

		viewer.setGrayed(parent, !allChildsChecked && hasCheckedChilds);
		viewer.setChecked(parent, hasCheckedChilds);
		updateParent(parent);
	}

}
