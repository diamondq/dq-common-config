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

/**
 * An abstract ConfigSource based on a Path to a document of some format.
 */
public abstract class AbstractPathDrivenConfigSource implements ConfigSource {

	private static final Logger	sLogger	= LoggerFactory.getLogger(AbstractPathDrivenConfigSource.class);

	protected final Path		mPath;

	/**
	 * Default constructor
	 *
	 * @param pPath the path
	 */
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

			Path[] envPaths;
			if (pEnvironment.isEmpty() == false) {
				Path parent = mPath.getParent();
				Path fileName = mPath.getFileName();
				Path envPath = (parent != null && fileName != null ? parent.resolve(pEnvironment).resolve(fileName)
					: Paths.get(pEnvironment, mPath.toString()));
				envPaths = new Path[] {envPath, mPath};
			}
			else
				envPaths = new Path[] {mPath};

			List<String> profiles = new ArrayList<>(pProfiles);
			profiles.add(0, "");

			List<ConfigDataTuple> results = new ArrayList<>();

			for (Path path : envPaths)
				for (String profile : profiles) {
					Path fileNamePath = path.getFileName();
					if (fileNamePath == null)
						continue;
					String fileName = fileNamePath.toString();
					if (profile.isEmpty() == false) {
						int offset = fileName.lastIndexOf('.');
						if (offset == -1)
							fileName = fileName + "-" + profile;
						else
							fileName = fileName.substring(0, offset) + "-" + profile + fileName.substring(offset);
					}
					Path parent = path.getParent();
					Path fullPath = (parent != null ? parent.resolve(fileName) : Paths.get(fileName));
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
