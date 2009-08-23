/*
Copyright (c) 2003-2004, Dennis M. Sosnoski
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

package org.xbis;

import java.io.*;
import java.util.HashMap;

/**
 * Parse event wrapper for XML Binary Information Set output. This provides methods for
 * writing the XBIS representation of the document from a parse event stream.
 *
 * @author Dennis M. Sosnoski
 * @version 0.8
 */

public class XBISEventWriter extends XBISWriter
{
    /** Number of namespace declarations allowed for in default allocation. */
    public static final int INITIAL_NAMESPACE_COUNT = 8;
    
    /** Number of elements allowed for in default allocation. */
    public static final int INITIAL_ELEMENT_COUNT = 12;
    
	/** Map of element name instances. */
	private HashMap m_elementMap;

	/** Map of attribute name instances. */
	private HashMap m_attributeMap;
    
    /** Pending namespace count. */
    private int m_pendingCount;
    
    /** Pending namespace table. */
    private OutputNamespace[] m_pendingNamespaces;
    
    /** Number of elements open. */
    private int m_elementCount;
    
    /** Number of namespaces defined for each open elemnt. */
    private int[] m_namespaceCounts;
	
	/** Flag for start of element last reported. */
	private boolean m_isStart;

	/**
	 * Constructor with buffer size specified.
	 *
	 * @param size output buffer size
	 */

	public XBISEventWriter(int size) {
		super(size);
		m_elementMap = new HashMap();
		m_attributeMap = new HashMap();
        m_pendingNamespaces = new OutputNamespace[INITIAL_NAMESPACE_COUNT];
        m_namespaceCounts = new int[INITIAL_ELEMENT_COUNT];
		setSharedContent(6);
		setSharedAttributes(6);
		initState();
	}

	/**
	 * Default constructor.
	 */

	public XBISEventWriter() {
		this(DEFAULT_BUFFER_SIZE);
	}

	/**
	 * Reset state information used during the serialization process. This
	 * reinitializes the serialization state so that an instance of this class
	 * can be reused to serialize multiple independent documents.
	 */

	public void reset() {
		if (!isReset()) {
			super.reset();
			m_elementMap.clear();
			m_attributeMap.clear();
            m_pendingCount = 0;
            m_elementCount = 0;
            for (int i = 0; i < m_pendingNamespaces.length; i++) {
                m_pendingNamespaces[i] = null;
            }
            m_isStart = false;
		}
	}

	/**
	 * Initialize state information used during the serialization process. This
	 * implementation of the abstract base class method is specific to the parse
	 * event stream interface.
	 */

	protected void initState() {}

	/**
	 * Get handle for attribute. This implementation of the abstract base class
	 * method is specific to the parse event stream interface.
	 *
	 * @param obj attribute object for which handle is to be found (must be a
	 * {@link org.xbis.XBISEventWriter.NameImpl} instance)
	 * @return handle value if defined (always strictly positive), negative
	 * value if not defined
	 */

	protected int getAttributeHandle(Object obj) {
		return ((NameImpl)obj).getHandle();
	}

	/**
	 * Add attribute definition. This implementation of the abstract base class
	 * method is specific to the parse event stream interface.
	 *
	 * @param obj attribute object to be defined (must be a
	 * {@link org.xbis.XBISEventWriter.NameImpl} instance)
	 * @throws IOException on error writing to stream
	 */

	protected void defineAttribute(Object obj) throws IOException {
		NameImpl name = (NameImpl)obj;
		writeNameDef(name.getName(), name.getNamespace());
		name.setHandle(m_attributeMap.size());
	}

	/**
	 * Set or clear flag for content present in element. This modifies the lead
	 * byte for the element definition to reflect the content status, clearing
	 * the marked output position.
	 *
	 * @param present flag for content present in element
	 * @throws IOException on error writing to stream
	 */

	private void setHasContent(boolean present) throws IOException {
		if (m_isStart) {
			if (present) {
				writeMarked(readMarked() | ELEMENT_HASCHILDREN_FLAG);
			}
			clearMark();
			m_isStart = false;
		}
	}

	/**
	 * Write document start to stream.
	 *
	 * @throws IOException on error writing to stream
	 */

	public void writeDocumentStart() throws IOException {
		clearReset();
		writeByte(NODE_TYPE_DOCUMENT);
	}

	/**
	 * Write document end to stream.
	 *
	 * @throws IOException on error writing to stream
	 */

	public void writeDocumentEnd() throws IOException {
		writeByte(0);
		flush();
	}

	/**
	 * Write character data to stream.
	 *
	 * @param buff array containing characters to be written
     * @param offset starting offset in buffer
     * @param length number of characters in string
	 * @throws IOException on error writing to stream
	 */

	public void writeCharData(char[] buff, int offset, int length)
        throws IOException {
        if (length > 0) {
            setHasContent(true);
            writeText(buff, offset, length);
        }
	}

	/**
	 * Write CDATA section to stream.
	 *
	 * @param buff array containing characters to be written
     * @param offset starting offset in buffer
     * @param length number of characters in string
	 * @throws IOException on error writing to stream
	 */

	public void writeCDATA(char[] buff, int offset, int length)
        throws IOException {
		setHasContent(true);
		writeByte(NODE_TYPE_CDATA);
		writeStringChars(buff, offset, length);
	}

	/**
	 * Write comment to stream.
	 *
	 * @param buff array containing characters to be written
     * @param offset starting offset in buffer
     * @param length number of characters in string
	 * @throws IOException on error writing to stream
	 */

	public void writeComment(char[] buff, int offset, int length)
        throws IOException {
		setHasContent(true);
		writeByte(NODE_TYPE_COMMENT);
		writeStringChars(buff, offset, length);
	}

	/**
	 * Write processing instruction to stream.
	 *
	 * @param name target name
	 * @param text value text
	 * @throws IOException on error writing to stream
	 */

	public void writeProcessingInstruction(String name, String text)
		throws IOException {
		setHasContent(true);
		writeByte(NODE_TYPE_PI);
		writeString(name);
		writeString(text);
	}

	/**
	 * Write element start tag to stream. Writes the element node information to
	 * the stream.
	 *
     * @param prefix element namespace prefix
	 * @param uri element namespace URI
	 * @param name local name for element
	 * @param hasa has attribute flag
	 * @throws IOException on error writing to stream
     * @throws XBISException on error in event stream
	 */

	public void writeElementStart(String prefix, String uri, String name,
		boolean hasa) throws IOException, XBISException {
		
		// force content set for containing element and mark this position
		setHasContent(true);
		m_isStart = true;
        
		// generate flags for lead node byte
		int lead = NODE_ELEMENT_FLAG;
		if (hasa) {
			lead |= ELEMENT_HASATTRIBUTES_FLAG;
		}
        
        // declare all namespaces except for new element namespace
        OutputNamespace ns = getNamespace(prefix, uri);
        for (int i = 0; i < m_pendingCount; i++) {
            OutputNamespace add = m_pendingNamespaces[i];
            if (add != ns || ns.getDefinitionHandle() >= 0) {
                writeNamespaceDecl(add);
            }
        }

		// check if element name already defined
        setMark();
		NameImpl nimpl = getElementImpl(ns, name);
		int handle = nimpl.getHandle();
		if (handle > 0) {

			// write existing handle for name
			writeQuickValue(handle, lead, ELEMENT_HANDLE_MASK);

		} else {

			// define new handle for name
			writeByte(lead+1);
			writeNameDef(name, ns);
			nimpl.setHandle(m_elementMap.size());

		}
        
        // track namespace declaration count at level
        if (m_elementCount >= m_namespaceCounts.length) {
            int[] grows = new int[m_elementCount*2];
            System.arraycopy(m_namespaceCounts, 0, grows, 0, m_elementCount);
            m_namespaceCounts = grows;
        }
        m_namespaceCounts[m_elementCount++] = m_pendingCount;
        m_pendingCount = 0;
	}

	/**
	 * Write attribute to current element start tag.
	 *
	 * @param prefix element namespace prefix
     * @param uri element namespace URI
     * @param name local name for element
	 * @param value attribute value
	 * @throws IOException on error writing to stream
     * @throws XBISException on error in event stream
	 */

	public void writeElementAttribute(String prefix, String uri, String name,
		String value) throws IOException, XBISException {
		NameImpl aimpl = getAttributeImpl(getNamespace(prefix, uri), name);
		writeAttribute(aimpl, value);
	}

	/**
	 * Write end of attributes for current element start tag.
	 *
	 * @throws IOException on error writing to stream
	 */

	public void writeEndAttribute() throws IOException {
		writeByte(0);
	}

	/**
	 * Write end of element to stream. Just clears the content present flag for
	 * the current element if first thing seen in element.
	 *
	 * @throws IOException on error writing to stream
	 */

	public void writeElementEnd() throws IOException {
		setHasContent(false);
		writeByte(0);
        closeNamespaces(m_namespaceCounts[--m_elementCount]);
	}

    /**
     * Begin namespace mapping. This accumulates namespace mappings until {@link
     * #writeElementStart} is called.
     *
     * @param prefix element namespace prefix
     * @param uri element namespace URI
     * @throws IOException on error writing to stream
     * @throws XBISException on namespace error
     */

    public void beginNamespaceMapping(String prefix, String uri)
        throws XBISException {
        if (m_pendingCount >= m_pendingNamespaces.length) {
            OutputNamespace[] grows = new OutputNamespace[m_pendingCount*2];
            System.arraycopy(m_pendingNamespaces, 0, grows, 0, m_pendingCount);
            m_pendingNamespaces = grows;
        }
        m_pendingNamespaces[m_pendingCount++] = getNamespace(prefix, uri);
    }

    /**
     * Write document type information to stream.
     *
     * @param name element name
     * @param pubid public id
     * @param sysid system id
     * @throws IOException on error writing to stream
     */

    public void writeDocumentType(String name, String pubid, String sysid)
        throws IOException {
        writeByte(NODE_TYPE_DOCTYPE);
        writeString(name);
        writeString(pubid);
        writeString(sysid);
    }

    /**
     * Write notation information to stream.
     *
     * @param name notation name
     * @param pubid public id
     * @param sysid system id
     * @throws IOException on error writing to stream
     */

    public void writeNotation(String name, String pubid, String sysid)
        throws IOException {
        writeByte(NODE_TYPE_NOTATION);
        writeString(name);
        writeString(pubid);
        writeString(sysid);
    }

    /**
     * Write unparsed entity information to stream.
     *
     * @param name entity name
     * @param pubid public id
     * @param sysid system id
     * @param notation notation name
     * @throws IOException on error writing to stream
     */

    public void writeUnparsedEntity(String name, String pubid, String sysid,
        String notation) throws IOException {
        writeByte(NODE_TYPE_UNPARSEDENTITY);
        writeString(name);
        writeString(pubid);
        writeString(sysid);
        writeString(notation);
    }

    /**
     * Write skipped entity information to stream.
     *
     * @param name entity name
     * @throws IOException on error writing to stream
     */

    public void writeSkippedEntity(String name) throws IOException {
        writeByte(NODE_TYPE_SKIPPEDENTITY);
        writeString(name);
    }

    /**
     * Write element declaration information to stream.
     *
     * @param name element name
     * @param model content model description
     * @throws IOException on error writing to stream
     */

    public void writeElementDecl(String name, String model) throws IOException {
        writeByte(NODE_TYPE_ELEMENTDECL);
        writeString(name);
        writeString(model);
    }

    /**
     * Write attribute declaration information to stream.
     *
     * @param ename owning element name
     * @param aname attribute name
     * @param type attribute type
     * @param deftype defaulting type
     * @param dflt default value
     * @throws IOException on error writing to stream
     */

    public void writeAttributeDecl(String ename, String aname, String type,
        String deftype, String dflt) throws IOException {
        writeByte(NODE_TYPE_ATTRIBUTEDECL);
        writeString(ename);
        writeString(aname);
        writeString(type);
        writeString(deftype);
        writeString(dflt);
    }

    /**
     * Write external entity information to stream.
     *
     * @param name entity name
     * @param pubid public id
     * @param sysid system id
     * @throws IOException on error writing to stream
     */

    public void writeExternalEntityDecl(String name, String pubid, String sysid)
        throws IOException {
        writeByte(NODE_TYPE_EXTERNALENTITYDECL);
        writeString(name);
        writeString(pubid);
        writeString(sysid);
    }

	/**
	 * Set output stream.
	 *
	 * @param os serialization output stream
	 * @throws IOException on error writing to stream
	 */

	public void setStream(OutputStream os) throws IOException {
		setStream(os, SAX2_SOURCE_ID);
	}

	/**
	 * Get name information. Checks if the supplied name and namespace match an
	 * existing instance in the table, and if so returns that instance.
	 * Otherwise creates a new instance and assigns the next handle value to
	 * that instance.
	 *
	 * @param ns namespace for name
	 * @param name local name
	 * @param map name map of appropriate type
	 * @throws IOException on error writing to stream
	 */

	private static NameImpl getNameImpl(OutputNamespace ns, String name,
		HashMap map) {
		Object value = map.get(name);
		if (value == null) {
			NameImpl inst = new NameImpl(ns, name);
			map.put(name, inst);
			return inst;
		} else {
			if (value instanceof NameImpl) {
				NameImpl inst = (NameImpl)value;
                OutputNamespace ins = inst.getNamespace();
                if (ins == ns) {
                    return inst;
                } else if (ins.getOwner() == ns.getOwner()) {
                    inst.setNewPrefix(ns);
					return inst;
				} else {
					HashMap submap = new HashMap();
					submap.put(inst.getNamespace(), inst);
					map.put(name, submap);
					value = submap;
				}
			}
			HashMap submap = (HashMap)value;
			value = submap.get(ns);
			if (value == null) {
				NameImpl inst = new NameImpl(ns, name);
                submap.put(ns, inst);
				return inst;
			} else {
				return (NameImpl)value;
			}
		}
	}

	/**
	 * Get name information for an attribute. Checks if the attribute name and
	 * namespace match an existing instance, and if so returns that instance.
	 * Otherwise creates a new instance and assigns the next handle value to
	 * that instance.
	 *
	 * @param ns namespace for attribute name
	 * @param name local name for attribute
	 * @throws IOException on error writing to stream
	 */

	public NameImpl getAttributeImpl(OutputNamespace ns, String name) {
		return getNameImpl(ns, name, m_attributeMap);
	}

	/**
	 * Get name information for an element. Checks if the element name and
	 * namespace match an existing instance, and if so returns that instance.
	 * Otherwise creates a new instance and assigns the next handle value to
	 * that instance.
	 *
	 * @param ns namespace for element name
	 * @param name local name for element
	 * @throws IOException on error writing to stream
	 */

	public NameImpl getElementImpl(OutputNamespace ns, String name) {
		return getNameImpl(ns, name, m_elementMap);
	}

	/**
	 * Element and attribute name implementation class used for parse event
     * stream processing. This just wraps the namespace information and local
     * name values.
	 */
	
	private static class NameImpl
	{
        private final String m_name;
		private OutputNamespace m_namespace;
        private boolean m_isNewPrefix;
		private int m_handle;
		
		private NameImpl(OutputNamespace ns, String name) {
			m_namespace = ns;
			m_name = name;
			m_handle = -1;
		}
        
        public void setNewPrefix(OutputNamespace ns) {
            m_namespace = ns;
            m_isNewPrefix = true;
        }
        
        public boolean isNewPrefix() {
            return m_isNewPrefix;
        }
        
        public void clearNewPrefix() {
            m_isNewPrefix = false;
        }
		
		public OutputNamespace getNamespace() {
			return m_namespace;
		}
		
		public String getName() {
			return m_name;
		}
		
		public void setHandle(int handle) {
			m_handle = handle;
		}
		
		public int getHandle() {
			return m_handle;
		}
	}
}