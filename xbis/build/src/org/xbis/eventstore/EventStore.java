/*
Copyright (c) 2004, Dennis M. Sosnoski
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.
 * Neither the name of XBIS nor the names of its contributors may be used
   to endorse or promote products derived from this software without specific
   prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.xbis.eventstore;

import java.util.HashMap;

import org.xbis.*;

/**
 * XML parse event store. This builds an in-memory record of the events
 * generated by parsing a document, allowing the events to be replayed
 * (possibly more than once) later.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */

public class EventStore
{
    //
    // Event types recorded in table

    public static final short START_DOCUMENT = 0;   // no data
    public static final short END_DOCUMENT = 1;     // no data
    public static final short START_TAG = 2;        // followed by element data
    public static final short END_TAG = 3;          // no data
    public static final short TEXT = 4;             // text length
    public static final short CDSECT = 5;           // text length
    public static final short ENTITY_REF = 6;       // entity name length
    public static final short IGNORABLE_WHITESPACE = 7; // whitespace length
    public static final short PROCESSING_INSTRUCTION = 8; // no data
    public static final short COMMENT = 9;          // comment length
    public static final short DOCDECL = 10;         // text length
    public static final short START_NAMESPACE = 11; // handle
    
    //
    // Default initial sizes
    public static final int INITIAL_EVENT_COUNT = 2000;
    public static final int INITIAL_NAMESPACE_COUNT = 20;
    public static final int INITIAL_QNAME_COUNT = 100;
    public static final int INITIAL_CHAR_COUNT = 10000;
    public static final int INITIAL_STRING_COUNT = 2000;
    
    /** Number of parse events and data values stored. */
    private int m_eventCount;
    
    /** Parse event codes in order (with associated data, in some cases). */
    private short[] m_events;
    
    /** Number of namespaces defined. */
    private int m_nsCount;
    
    /** Namespaces. */
    private BasicNamespace[] m_nss;
    
    /** Number of qualified names defined. */
    private int m_qnameCount;
    
    /** Qualified names for elements and attributes. */
    private NameImpl[] m_qnames;
    
    /** Map of defined names for elements and attributes. */
    private HashMap m_qnameMap;
    
    /** Number of characters of data stored. */
    private int m_charCount;
    
    /** Character data and other document data. */
    private char[] m_chars;
    
    /** Number of string values stored. */
    private int m_stringCount;
    
    /** String values saved directly (used for attribute values). */
    private String[] m_strings;

	/**
	 * Default constructor.
	 */

	public EventStore() {
        m_events = new short[INITIAL_EVENT_COUNT];
        m_nss = new BasicNamespace[INITIAL_NAMESPACE_COUNT];
        m_qnames = new NameImpl[INITIAL_QNAME_COUNT];
        m_chars = new char[INITIAL_CHAR_COUNT];
        m_strings = new String[INITIAL_STRING_COUNT];
        m_qnameMap = new HashMap();
	}

    /**
     * Reset state information used during the serialization process. This
     * reinitializes the serialization state so that an instance of this class
     * can be reused to serialize multiple independent documents.
     */

    public void reset() {
        for (int i = 0; i < m_nsCount; i++) {
            m_nss[i] = null;
        }
        for (int i = 0; i < m_qnameCount; i++) {
            m_qnames[i] = null;
        }
        for (int i = 0; i < m_stringCount; i++) {
            m_strings[i] = null;
        }
        m_eventCount = 0;
        m_nsCount = 0;
        m_qnameCount = 0;
        m_charCount = 0;
        m_stringCount = 0;
        m_qnameMap.clear();
    }

    /**
     * Append value to event data store.
     * 
     * @param value event data value
     */

    public void appendEvent(short value) {
        if (m_events.length <= m_eventCount) {
            short[] copy = new short[m_events.length * 2];
            System.arraycopy(m_events, 0, copy, 0, m_events.length);
            m_events = copy;
        }
        m_events[m_eventCount++] = value;
    }

    /**
     * Append characters to store.
     * 
	 * @param buff array supplying character data
	 * @param offset starting offset in array
	 * @param length number of characters
     */

    public void appendChars(char[] buff, int offset, int length) {
        int end = m_charCount + length;
        if (m_chars.length <= end) {
            char[] copy = new char[end * 2];
            System.arraycopy(m_chars, 0, copy, 0, m_chars.length);
            m_chars = copy;
        }
        System.arraycopy(buff, offset, m_chars, m_charCount, length);
        m_charCount = end;
    }

    /**
     * Append string to store.
     * 
     * @param text string to be appended
     * @return index number of namespace
     */

    public int appendString(String text) {
        if (m_strings.length <= m_stringCount) {
            String[] copy = new String[m_strings.length * 2];
            System.arraycopy(m_strings, 0, copy, 0, m_strings.length);
            m_strings = copy;
        }
        m_strings[m_stringCount] = text;
        return m_stringCount++;
    }

    /**
     * Create qname, adding it to store.
     * 
     * @param ns namespace for name
     * @param name local name
     */

    public NameImpl addQName(BasicNamespace ns, String name) {
        String qname = (ns.getPrefix().length() == 0) ?
            name : ns.getPrefix() + ':' + name;
        NameImpl inst = new NameImpl(ns, name, qname, m_qnameCount);
        if (m_qnames.length <= m_qnameCount) {
            NameImpl[] copy = new NameImpl[m_qnames.length * 2];
            System.arraycopy(m_qnames, 0, copy, 0, m_qnames.length);
            m_qnames = copy;
        }
        m_qnames[m_qnameCount++] = inst;
        return inst;
    }

    /**
     * Append namespace to store.
     * 
     * @param ns namespace to be appended
     */

    public int appendNs(BasicNamespace ns) {
        if (m_nss.length <= m_nsCount) {
            BasicNamespace[] copy = new BasicNamespace[m_nss.length * 2];
            System.arraycopy(m_nss, 0, copy, 0, m_nss.length);
            m_nss = copy;
        }
        m_nss[m_nsCount] = ns;
        return m_nsCount++;
    }

    /**
     * Get name information. Checks if the supplied name and namespace match an
     * existing instance in the table, and if so returns that instance.
     * Otherwise creates a new instance and assigns the next handle value to
     * that instance.
     *
     * @param ns namespace for name
     * @param name local name
     */

    private NameImpl getNameImpl(BasicNamespace ns, String name) {
        Object value = m_qnameMap.get(name);
        if (value == null) {
            NameImpl inst = addQName(ns, name);
            m_qnameMap.put(name, inst);
            return inst;
        } else {
            if (value instanceof NameImpl) {
                NameImpl inst = (NameImpl)value;
                if (inst.getNamespace().equals(ns)) {
                    return inst;
                } else {
                    HashMap submap = new HashMap();
                    submap.put(inst.getNamespace(), inst);
                    m_qnameMap.put(name, submap);
                    value = submap;
                }
            }
            HashMap submap = (HashMap)value;
            value = submap.get(ns);
            if (value == null) {
                NameImpl inst = addQName(ns, name);
                submap.put(ns, inst);
                return inst;
            } else {
                return (NameImpl)value;
            }
        }
    }

    /**
     * Append element start tag event to store. Appends the event type followed
     * by the actual element name handle information, adding the element to the
     * names table if necessary.
     *
     * @param ns element namespace information
     * @param name local name for element
     * @param acnt number of attributes on element
     */

    public void appendElementStart(BasicNamespace ns, String name, int acnt) {
        NameImpl nimpl = getNameImpl(ns, name);
        appendEvent(START_TAG);
        appendEvent((short)nimpl.getHandle());
        appendEvent((short)acnt);
    }

    /**
     * Append attribute data to store. Appends the attribute name handle
     * information followed by the length of the data to the event store, then
     * appends the attribute value to the string store.
     *
     * @param ns element namespace information
     * @param name local name for element
     * @param value text of attribute value
     */

    public void appendAttribute(BasicNamespace ns, String name, String value) {
        NameImpl nimpl = getNameImpl(ns, name);
        appendEvent((short)nimpl.getHandle());
        appendString(value);
    }

    /**
     * Create reader.
     *
     * @return reader for event store data
     */

    public StoreReader createReader() {
        return new StoreReader();
    }

    /**
     * Event store reader used to retrieve information from store. This just
     * wraps the current position information, operating as a kind of
     * multi-iterator.
     */
    
    public class StoreReader
    {
        private int m_eventIndex;
        private int m_stringIndex;
        private int m_charIndex;
        
        public StoreReader() {}
        
        public int readEvent() {
            return m_events[m_eventIndex++];
        }
        
        public NameImpl readQName() {
            return m_qnames[m_events[m_eventIndex++]];
        }
        
        public String readString() {
            return m_strings[m_stringIndex++];
        }
        
        public char[] getChars() {
            return m_chars;
        }
        
        public int getCharOffset() {
            return m_charIndex;
        }
        
        public int readChars() {
            int length = m_events[m_eventIndex++];
            m_charIndex += length;
            return length;
        }
        
        public BasicNamespace getNamespace(int index) {
            return m_nss[index];
        }
        
        public void reset() {
            m_eventIndex = 0;
            m_stringIndex = 0;
            m_charIndex = 0;
        }
    }
} 