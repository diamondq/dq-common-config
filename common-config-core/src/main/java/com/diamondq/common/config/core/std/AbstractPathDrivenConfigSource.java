package com.diamondq.common.config.core.std;

import com.diamondq.common.config.spi.ConfigDataTuple;
import com.diamondq.common.config.spi.ConfigProp;
import com.diamondq.common.config.spi.ConfigSource;
import com.diamondq.common.config.spi.NodeType;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPathDrivenConfigSource implements ConfigSource {

	private static final Logger	sLogger	= LoggerFactory.getLogger(AbstractPathDrivenConfigSource.class);

	protected final Path		mPath;

	public AbstractPathDrivenConfigSource(Path pPath) {
		mPath = pPath;
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigReconstructable#getReconstructionNodeType()
	 */
	@Override
	public NodeType getReconstructionNodeType() {
		return NodeType.builder().isExplicitType(true)
			.type(ConfigProp.builder().configSource("").value(getClass().getName()).build()).build();
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigSource#getConfiguration(java.lang.String, java.util.List)
	 */
	@Override
	public List<ConfigDataTuple> getConfiguration(String pEnvironment, List<String> pProfiles) {
		sLogger.trace("getConfiguration({}, {})", pEnvironment, pProfiles);
		try {

			Path path = mPath;
			boolean hasParent = path.getParent() != null;
			if ((pEnvironment != null) && (pEnvironment.isEmpty() == false))
				path = (hasParent ? path.getParent().resolve(pEnvironment).resolve(path.getFileName())
					: Paths.get(pEnvironment, path.toString()));

			List<String> profiles = new ArrayList<>(pProfiles);
			profiles.add(0, "");

			List<ConfigDataTuple> results = new ArrayList<>();

			for (String profile : profiles) {
				String fileName = path.getFileName().toString();
				if (profile.isEmpty() == false) {
					int offset = fileName.lastIndexOf('.');
					fileName = fileName.substring(0, offset) + "-" + profile + fileName.substring(offset);
				}
				Path fullPath = (hasParent ? path.getParent().resolve(fileName) : Paths.get(fileName));
				processPath(fullPath, results);

			}
			sLogger.trace("getConfiguration(...) -> {}", results);
			return results;
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	protected abstract void processPath(Path pFullPath, List<ConfigDataTuple> pResults) throws IOException;

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "{name=" + getName() + "}";
	}
}
