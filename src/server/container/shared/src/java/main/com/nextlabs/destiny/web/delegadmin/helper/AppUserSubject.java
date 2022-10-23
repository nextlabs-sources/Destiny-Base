package com.nextlabs.destiny.web.delegadmin.helper;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.bluejungle.framework.expressions.IEvalValue;
import com.bluejungle.framework.utils.DynamicAttributes;
import com.bluejungle.pf.domain.destiny.subject.IMSubject;
import com.bluejungle.pf.domain.destiny.subject.Subject;
import com.bluejungle.pf.domain.epicenter.subject.ISubjectType;

/**
 *
 * Application User as a subject object for Delegation Rules
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class AppUserSubject extends Subject {

	private static final long serialVersionUID = -7718591007623026079L;
	private final DynamicAttributes attributes;

	/**
	 * Constructor
	 * 
	 * @param uid
	 *            uid
	 * @param uniqueName
	 *            username
	 * @param name
	 *            name
	 * @param id
	 *            id
	 * @param subjectType
	 *            subjectType
	 */
	public AppUserSubject(String uid, String uniqueName, String name, Long id, ISubjectType subjectType,
			DynamicAttributes attributes) {
		super(uid, uniqueName, name, id, subjectType);
		if (attributes != null) {
			this.attributes = attributes;
		} else {
			this.attributes = DynamicAttributes.EMPTY;
		}

	}

	/**
	 * @see Subject#getAttribute(String)
	 */
	@Override
	public IEvalValue getAttribute(String name) {
		return attributes.get(name);
	}

	/**
	 * @see Subject#getEntrySet()
	 */
	@Override
	public Set<Map.Entry<String, IEvalValue>> getEntrySet() {
		return Collections.unmodifiableSet(attributes.entrySet());
	}

	/**
	 * @see IMSubject#setAttribute(String, IEvalValue)
	 */
	public void setAttribute(String name, IEvalValue value) {
		attributes.put(name, value);
	}

	/**
	 * Subjects with non-empty dynamic attributes are considered non-cacheable.
	 *
	 * @see Subject#isCacheable()
	 */
	@Override
	public boolean isCacheable() {
		return attributes.isEmpty();
	}

}
