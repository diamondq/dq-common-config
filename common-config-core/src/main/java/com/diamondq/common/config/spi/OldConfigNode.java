package com.diamondq.common.config.spi;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class OldConfigNode {

	private final String						mName;

	private Optional<ConfigProp>				mValue;

	private ConcurrentMap<String, OldConfigNode>	mChildren;

	private ConcurrentMap<String, ConfigProp>	mMetaData;

	private NodeType							mType;

	public OldConfigNode(String pName) {
		mName = pName;
	}

	public OldConfigNode(OldConfigNode pOrig) {
		mName = pOrig.mName;
		mValue = pOrig.mValue;
		mType = pOrig.mType;
		if (pOrig.mChildren != null) {
			mChildren = new ConcurrentHashMap<>();
			for (Map.Entry<String, OldConfigNode> c : pOrig.mChildren.entrySet())
				mChildren.put(c.getKey(), new OldConfigNode(c.getValue()));
		}
		if (pOrig.mMetaData != null)
			mMetaData = new ConcurrentHashMap<>(pOrig.mMetaData);
	}

	/**
	 * The name of the node. Must be a simple name (no 'period' / . character)
	 * 
	 * @return the name
	 */
	public String getName() {
		return mName;
	}

	/**
	 * Returns the type of the node. Must be a fully qualified Java name (ie. java.lang.String). If the type is not
	 * known, it will default to java.lang.String. A node that is just a 'parent', will have a type of
	 * 'com.diamondq.common.config.model.Node'
	 * 
	 * @return the type
	 */
	public NodeType getType() {
		return mType;
	}

	/**
	 * Sets the type of node.
	 * 
	 * @param pType the type
	 */
	public void setType(NodeType pType) {
		mType = pType;
	}

	/**
	 * Returns the value of the node
	 * 
	 * @return the optional value
	 */
	public Optional<ConfigProp> getValue() {
		return mValue;
	}

	/**
	 * Sets the value of the node
	 * 
	 * @param pValue the value
	 */
	public void setValue(ConfigProp pValue) {
		mValue = Optional.of(pValue);
	}

	/**
	 * Sets the value of the node
	 * 
	 * @param pValue the value
	 */
	public void setValue(Optional<ConfigProp> pValue) {
		mValue = pValue;
	}

	/**
	 * Returns the children
	 * 
	 * @param pKey
	 * @return the children map
	 */
	public Optional<OldConfigNode> getChildByKey(String pKey) {
		synchronized (this) {
			if (mChildren == null)
				return Optional.empty();
			return Optional.ofNullable(mChildren.get(pKey));
		}
	}

	public void putChildByKey(String pKey, OldConfigNode pChild) {
		synchronized (this) {
			if (mChildren == null)
				mChildren = new ConcurrentHashMap<>();
			mChildren.put(pKey, new OldConfigNode(pChild));
		}
	}

	/**
	 * Returns the set of meta data
	 * 
	 * @return NOTE: May be null
	 */
	public Map<String, ConfigProp> getMetaData() {
		return mMetaData;
	}

	public void putMetaData(String pKey, ConfigProp pValue) {
		synchronized (this) {
			if (mMetaData == null)
				mMetaData = new ConcurrentHashMap<>();
			mMetaData.put(pKey, pValue);
		}
	}

	public Optional<ConfigProp> getMetaDataByKey(String pKey) {
		synchronized (this) {
			if (mMetaData == null)
				return Optional.empty();
			return Optional.ofNullable(mMetaData.get(pKey));
		}
	}

	/**
	 * Returns the set of children
	 * 
	 * @return NOTE: May be null
	 */
	public Map<String, OldConfigNode> getChildren() {
		synchronized (this) {
			return mChildren;
		}
	}

	public void clearChildren() {
		synchronized (this) {
			mChildren = null;
		}
	}

}
