package org.openlca.app.projects;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.openlca.app.db.Database;
import org.openlca.app.util.Labels;
import org.openlca.core.database.Cache;
import org.openlca.core.model.descriptors.ProjectDescriptor;

/** The editor input of a project result. */
class ProjectResultInput implements IEditorInput {

	private long projectId;
	private String resultKey;

	ProjectResultInput(long projectId, String resultKey) {
		this.projectId = projectId;
		this.resultKey = resultKey;
	}

	public long getProjectId() {
		return projectId;
	}

	public String getResultKey() {
		return resultKey;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getName() {
		Cache cache = Database.getCache();
		if (cache == null)
			return "";
		ProjectDescriptor d = cache.getProjectDescriptor(projectId);
		return "Project result of " + Labels.getDisplayName(d);
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return getName();
	}

}