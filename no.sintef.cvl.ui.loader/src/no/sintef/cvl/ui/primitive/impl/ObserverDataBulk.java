package no.sintef.cvl.ui.primitive.impl;

import java.util.HashMap;

import no.sintef.cvl.ui.primitive.Keywords;

public class ObserverDataBulk implements Keywords {

	private HashMap<String, Object> data = new HashMap<String, Object>();
	
	@Override
	public Object getDataField(String name) {
		return data.get(name);
	}

	@Override
	public void setDataField(String name, Object object) {
		data.put(name, object);
	}

}
