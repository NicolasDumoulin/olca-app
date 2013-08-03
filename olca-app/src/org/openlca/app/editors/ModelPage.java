package org.openlca.app.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.openlca.app.App;
import org.openlca.app.components.TextDropComponent;
import org.openlca.app.editors.DataBinding.BindingType;
import org.openlca.app.util.Bean;
import org.openlca.app.util.UI;
import org.openlca.app.util.UIFactory;
import org.openlca.core.model.CategorizedEntity;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.descriptors.BaseDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ModelPage<T extends CategorizedEntity> extends FormPage {

	private Logger log = LoggerFactory.getLogger(getClass());
	private DataBinding binding;

	public ModelPage(ModelEditor<T> editor, String id, String title) {
		super(editor, id, title);
		this.binding = new DataBinding(editor);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ModelEditor<T> getEditor() {
		return (ModelEditor<T>) super.getEditor();
	}

	protected T getModel() {
		return getEditor().getModel();
	}

	public DataBinding getBinding() {
		return binding;
	}

	protected void createLink(String label, String property, Composite parent) {
		try {
			Object value = Bean.getValue(getModel(), property);
			if (!(value instanceof CategorizedEntity))
				throw new IllegalArgumentException(
						"Property for link must be CategorizedEntity");

			CategorizedEntity entity = (CategorizedEntity) value;
			new Label(parent, SWT.NONE).setText(label);
			Link link = new Link(parent, SWT.NONE);
			link.setText("<a>" + entity.getName() + "</a>");
			link.addSelectionListener(new ModelLinkClickedListener(entity));
		} catch (Exception e) {
			log.error("Could not get value of bean", e);
		}
	}

	protected void createReadOnly(String label, String property,
			Composite parent) {
		Text text = UI.formText(parent, getManagedForm().getToolkit(), label);
		text.setEnabled(false);
		binding.readOnly(getModel(), property, BindingType.STRING, text);
	}

	protected void createText(String label, String property, Composite parent) {
		Text text = UI.formText(parent, getManagedForm().getToolkit(), label);
		binding.on(getModel(), property, BindingType.STRING, text);
	}

	protected void createText(String label, String property, BindingType type,
			Composite parent) {
		Text text = UI.formText(parent, getManagedForm().getToolkit(), label);
		binding.on(getModel(), property, type, text);
	}

	protected void createDropComponent(String label, String property,
			ModelType modelType, Composite parent) {
		TextDropComponent text = UIFactory.createDropComponent(parent, label,
				getManagedForm().getToolkit(), modelType);
		binding.on(getModel(), property, text);
	}

	public class ModelLinkClickedListener implements SelectionListener {

		private Object model;

		public ModelLinkClickedListener(CategorizedEntity entity) {
			this.model = entity;
		}

		public ModelLinkClickedListener(BaseDescriptor descriptor) {
			this.model = descriptor;
		}

		private void selected() {
			if (model instanceof CategorizedEntity)
				App.openEditor((CategorizedEntity) model);
			else if (model instanceof BaseDescriptor)
				App.openEditor((BaseDescriptor) model);
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			selected();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			selected();
		}

	}

}