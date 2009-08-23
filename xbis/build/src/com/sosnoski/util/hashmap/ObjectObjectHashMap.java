/*
 * Copyright (c) 2000-2001 Sosnoski Software Solutions, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package com.sosnoski.util.hashmap;

import java.util.Iterator;

import com.sosnoski.util.SparseArrayIterator;

/**
 * Hash map using <code>Object</code> values as keys mapped to
 * <code>Object</code> values. This implementation is unsynchronized
 * in order to provide the best possible performance for typical usage
 * scenarios, so explicit synchronization must be implemented by a wrapper
 * class or directly by the application in cases where instances are modified
 * in a multithreaded environment. See the base classes for other details of
 * the implementation.
 *
 * @author Dennis M. Sosnoski
 * @version 1.1
 */

public class ObjectObjectHashMap extends ObjectKeyBase
{
	/** Array of key table slots. */
	protected Object[] m_keyTable;

	/** Array of value table slots. */
	protected Object[] m_valueTable;

	/**
	 * Constructor with full specification.
	 *
	 * @param count number of values to assume in initial sizing of table
	 * @param fill fraction full allowed for table before growing
	 * @param tech hash technique specifier (one of STANDARD_HASH,
	 * IDENTITY_COMP, or IDENTITY_HASH, inherited from
	 * {@link com.sosnoski.util.ObjectHashBase})
	 */

	public ObjectObjectHashMap(int count, double fill, Object tech) {
		super(count, fill, Object.class, Object.class, tech);
	}

	/**
	 * Constructor with size and fill fraction specified. Uses default hash
	 * technique.
	 *
	 * @param count number of values to assume in initial sizing of table
	 * @param fill fraction full allowed for table before growing
	 */

	public ObjectObjectHashMap(int count, double fill) {
		this(count, fill, STANDARD_HASH);
	}

	/**
	 * Constructor with only size and hash technique supplied. Uses default
	 * value for fill fraction.
	 *
	 * @param count number of values to assume in initial sizing of table
	 * @param tech hash technique specifier (one of STANDARD_HASH,
	 * IDENTITY_COMP, or IDENTITY_HASH, inherited from
	 * {@link com.sosnoski.util.ObjectHashBase})
	 */

	public ObjectObjectHashMap(int count, Object tech) {
		this(count, DEFAULT_FILL, tech);
	}

	/**
	 * Constructor with only size supplied. Uses default hash technique and
	 * value for fill fraction.
	 *
	 * @param count number of values to assume in initial sizing of table
	 */

	public ObjectObjectHashMap(int count) {
		this(count, DEFAULT_FILL);
	}

	/**
	 * Constructor with technique specified. Uses standard default values
	 * for size and fill fraction.
	 *
	 * @param tech hash technique specifier (one of STANDARD_HASH,
	 * IDENTITY_COMP, or IDENTITY_HASH, inherited from
	 * {@link com.sosnoski.util.ObjectHashBase})
	 */

	public ObjectObjectHashMap(Object tech) {
		this(0, DEFAULT_FILL, tech);
	}

	/**
	 * Default constructor.
	 */

	public ObjectObjectHashMap() {
		this(0, DEFAULT_FILL);
	}

	/**
	 * Copy (clone) constructor.
	 *
	 * @param base instance being copied
	 */

	public ObjectObjectHashMap(ObjectObjectHashMap base) {
		super(base);
	}

	/**
	 * Get the backing array of keys. This implementation of an abstract
	 * method is used by the type-agnostic base class code to access the
	 * array used for type-specific storage by the child class.
	 *
	 * @return backing key array object
	 */

	protected final Object[] getKeyArray() {
		return m_keyTable;
	}

	/**
	 * Set the backing array of keys. This implementation of an abstract
	 * method is used by the type-agnostic base class code to set the
	 * array used for type-specific storage by the child class.
	 *
	 * @param array backing key array object
	 */

	protected final void setKeyArray(Object array) {
		m_keyTable = (Object[])array;
	}

	/**
	 * Get the backing array of values. This implementation of an abstract
	 * method is used by the type-agnostic base class code to access the
	 * array used for type-specific storage by the child class.
	 *
	 * @return backing key array object
	 */

	protected final Object getValueArray() {
		return m_valueTable;
	}

	/**
	 * Set the backing array of values. This implementation of an abstract
	 * method is used by the type-agnostic base class code to set the
	 * array used for type-specific storage by the child class.
	 *
	 * @param array backing value array object
	 */

	protected final void setValueArray(Object array) {
		m_valueTable = (Object[])array;
	}

	/**
	 * Reinsert an entry into the hash map. This implementation of an abstract
	 * method is used when the table is being directly modified by
	 * the base class, and does not adjust the count present or check the table
	 * capacity.
	 *
	 * @param slot position of entry to be reinserted into hash map
	 * @return <code>true</code> if the slot number used by the entry has
	 * has changed, <code>false</code> if not
	 */

	protected final boolean reinsert(int slot) {
		Object key = m_keyTable[slot];
		m_keyTable[slot] = null;
		return assignSlot(key, m_valueTable[slot]) != slot;
	}

	/**
	 * Restructure the table. This implementation of an abstract method is
	 * used when the table is increasing or decreasing in size,
	 * and works directly with the old table representation arrays. It
	 * inserts pairs from the old arrays directly into the table without
	 * adjusting the count present or checking the table size.
	 *
	 * @param karray array of keys
	 * @param varray array of values
	 */

	protected void restructure(Object karray, Object varray) {
		Object[] keys = (Object[])karray;
		Object[] values = (Object[])varray;
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] != null) {
				assignSlot(keys[i], values[i]);
			}
		}
	}

	/**
	 * Assign slot for entry. Starts at the slot found by the hashed key
	 * value. If this slot is already occupied, it steps the slot number and
	 * checks the resulting slot, repeating until an unused slot is found. This
	 * method does not check for duplicate keys, so it should only be used for
	 * internal reordering of the tables.
	 *
	 * @param key to be added to table
	 * @param value associated value for key
	 * @return slot at which entry was added
	 */

	protected int assignSlot(Object key, Object value) {
		int offset = freeSlot(standardSlot(key));
		m_keyTable[offset] = key;
		m_valueTable[offset] = value;
		return offset;
	}

	/**
	 * Add an entry to the table. If the key is already present in the table,
	 * this replaces the existing value associated with the key.
	 *
	 * @param key key to be added to table (non-<code>null</code>)
	 * @param value associated value for key (non-<code>null</code>)
	 * @return value previously associated with key, or <code>null</code>
	 * if key not previously present in table
	 */

	public Object add(Object key, Object value) {

		// first validate the parameters
		if (key == null) {
			throw new IllegalArgumentException("null key not supported");
		} else if (value == null) {
			throw new IllegalArgumentException("null value not supported");
		} else {

			// check space and duplicate key
			ensureCapacity(m_entryCount+1);
			int offset = standardFind(key);
			if (offset >= 0) {

				// replace existing value for key
				Object prior = m_valueTable[offset];
				m_valueTable[offset] = value;
				return prior;

			} else {

				// add new pair to table
				m_entryCount++;
				offset = -offset - 1;
				m_keyTable[offset] = key;
				m_valueTable[offset] = value;
				return null;

			}
		}
	}

	/**
	 * Check if an entry is present in the table. This method is supplied to
	 * support the use of values matching the reserved not found value.
	 *
	 * @param key key for entry to be found
	 * @return <code>true</code> if key found in table, <code>false</code>
	 * if not
	 */

	public final boolean containsKey(Object key) {
		return standardFind(key) >= 0;
	}

	/**
	 * Find an entry in the table.
	 *
	 * @param key key for entry to be returned
	 * @return value for key, or <code>null</code> if key not found
	 */

	public final Object get(Object key) {
		int slot = standardFind(key);
		if (slot >= 0) {
			return m_valueTable[slot];
		} else {
			return null;
		}
	}

	/**
	 * Remove an entry from the table. If multiple entries are present with
	 * the same key value, only the first one found will be removed.
	 *
	 * @param key key to be removed from table
	 * @return value associated with removed key, or <code>null</code>
	 * if key not found in table
	 */

	public Object remove(Object key) {
		int slot = standardFind(key);
		if (slot >= 0) {
			Object value = m_valueTable[slot];
			internalRemove(slot);
			return value;
		} else {
			return null;
		}
	}

	/**
	 * Return an iterator for the value <code>Object</code>s in this map. The
	 * iterator returns all values in arbitrary order, but is not "live". Any
	 * changes to the map while the iteration is in progress will give
	 * indeterminant results.
	 *
	 * @return iterator for values in array
	 */

	public final Iterator valueIterator() {
		return SparseArrayIterator.buildIterator(m_valueTable);
	}

	/**
	 * Construct a copy of the table.
	 *
	 * @return shallow copy of table
	 */

	public Object clone() {
		return new ObjectObjectHashMap(this);
	}
}
