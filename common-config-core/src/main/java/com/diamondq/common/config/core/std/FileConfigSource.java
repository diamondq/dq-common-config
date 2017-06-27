package com.diamondq.common.config.core.std;

import com.diamondq.common.config.spi.ConfigDataTuple;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A ConfigSource based on a File of some format
 */
public class FileConfigSource extends AbstractPathDrivenConfigSource {

	/**
	 * The constructor
	 * 
	 * @param pPath the file path
	 */
	public FileConfigSource(String pPath) {
		super(Paths.get(pPath));
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigReconstructable#getReconstructionParams()
	 */
	@Override
	public Map<String, String> getReconstructionParams() {
		return Collections.singletonMap("file", mPath.toString());
	}

	/**
	 * @see com.diamondq.common.config.spi.ConfigSource#getName()
	 */
	@Override
	public String getName() {
		return "file:" + mPath.toString();
	}

	@Override
	protected void processPath(Path pPath, List<ConfigDataTuple> pResults) throws IOException {

		if (Files.exists(pPath) == true) {
			InputStream input = Files.newInputStream(pPath, StandardOpenOption.READ);
			pResults.add(ConfigDataTuple.builder().name(pPath.toString()).source(this).stream(input).build());
		}
	}

}
