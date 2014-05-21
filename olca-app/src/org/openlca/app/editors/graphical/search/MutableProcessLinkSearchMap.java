package org.openlca.app.editors.graphical.search;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TLongObjectHashMap;

import java.util.Collection;

import org.openlca.core.model.ProcessLink;

public class MutableProcessLinkSearchMap extends ProcessLinkSearchMap {

	public MutableProcessLinkSearchMap(Collection<ProcessLink> links) {
		super(links);
	}

	public void put(ProcessLink link) {
		int index = remove(link);
		if (index == -1)
			index = data.size();
		index(link.getProviderId(), index, providerIndex);
		index(link.getRecipientId(), index, recipientIndex);
	}

	public int remove(ProcessLink link) {
		int index = data.indexOf(link);
		if (index < 0)
			return -1;
		data.set(index, null);
		remove(link.getProviderId(), index, providerIndex);
		remove(link.getRecipientId(), index, recipientIndex);
		return index;
	}

	private void remove(long id, int index,
			TLongObjectHashMap<TIntArrayList> map) {
		TIntArrayList list = map.get(id);
		if (list == null)
			return;
		list.remove(index);
		if (list.size() == 0)
			map.remove(id);
	}

}