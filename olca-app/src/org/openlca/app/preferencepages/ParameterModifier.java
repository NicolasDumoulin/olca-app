package org.openlca.app.preferencepages;

import java.util.List;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Item;
import org.openlca.app.Messages;
import org.openlca.app.util.Dialog;
import org.openlca.core.model.Parameter;
import org.openlca.expressions.Interpreter;
import org.openlca.expressions.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The cell modifier of the parameter table. */
class ParameterModifier implements ICellModifier {

	private final String FORMULA = Messages.Formula;
	private final String NAME = Messages.Name;
	private final String DESCRIPTION = Messages.Description;
	private final String NUMERIC_VALUE = Messages.NumericValue;
	private List<Parameter> parameters;

	private Logger log = LoggerFactory.getLogger(getClass());
	private TableViewer viewer;

	public ParameterModifier(TableViewer viewer, List<Parameter> parameters) {
		this.viewer = viewer;
		this.parameters = parameters;
	}

	@Override
	public boolean canModify(Object element, String property) {
		if (property.equals(NUMERIC_VALUE))
			return false;
		return true;
	}

	@Override
	public Object getValue(Object element, String property) {
		if (!(element instanceof Parameter) || property == null)
			return null;
		Parameter parameter = (Parameter) element;
		if (property.equals(NAME))
			return parameter.getName();
		else if (property.equals(FORMULA))
			return parameter.getExpression().getFormula();
		else if (property.equals(DESCRIPTION))
			return parameter.getDescription();
		else
			return null;
	}

	@Override
	public void modify(Object element, String property, Object value) {
		if (element instanceof Item) {
			element = ((Item) element).getData();
		}
		if (!(element instanceof Parameter))
			return;
		Parameter parameter = (Parameter) element;
		log.trace("modify parameter {}", parameter);
		log.trace("modify property {} with value {}", property, value);
		setValue(property, (String) value, parameter);
		viewer.refresh();
	}

	private void setValue(String property, String value, Parameter parameter) {
		if (property.equals(NAME)) {
			setParameterName(parameter, value);
		} else if (property.equals(FORMULA)) {
			log.trace("set formula to {}", value);
			parameter.getExpression().setFormula(value);
			eval();
		} else if (property.equals(DESCRIPTION)) {
			log.trace("set description to {}", value);
			parameter.setDescription(value);
		}
	}

	private void setParameterName(Parameter parameter, String value) {
		log.trace("set name to {}", value);
		if (Parameter.checkName(value)) {
			parameter.setName(value);
			eval();
		} else {
			log.warn("invalid parameter name {}", value);
			Dialog.showError(viewer.getControl().getShell(),
					"Invalid parameter name: " + value);
		}
	}

	private void eval() {
		log.trace("evaluate database parameters");
		Interpreter interpreter = new Interpreter();
		for (Parameter parameter : parameters) {
			Variable variable = new Variable(parameter.getName(), parameter
					.getExpression().getFormula());
			interpreter.bind(variable);
		}
		try {
			for (Parameter p : parameters) {
				double val = interpreter.eval(p.getName());
				p.getExpression().setValue(val);
			}
		} catch (Exception e) {
			log.error("Failed to evaluate parameters", e);
			Dialog.showError(viewer.getControl().getShell(),
					"Parameter evaluation failed: " + e.getMessage());
		}
	}
}