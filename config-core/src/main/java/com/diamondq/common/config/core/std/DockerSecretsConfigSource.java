package com.diamondq.common.config.core.std;

import com.diamondq.common.config.spi.ConfigDataTuple;
import com.diamondq.common.config.spi.ConfigProp;
import com.diamondq.common.config.spi.ConfigSource;
import com.diamondq.common.config.spi.NodeType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A ConfigSource based on the Docker Secrets
 */
public class DockerSecretsConfigSource implements ConfigSource {
  private static final Logger sLogger = LoggerFactory.getLogger(DockerSecretsConfigSource.class);

  /**
   * Default constructor
   */
  public DockerSecretsConfigSource() {
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
   * @see com.diamondq.common.config.spi.ConfigReconstructable#getReconstructionParams()
   */
  @Override
  public Map<String, String> getReconstructionParams() {
    return Collections.emptyMap();
  }

  @Override
  public String getName() {
    return "secrets";
  }

  /**
   * Environment and Profiles are ignored, since we're just reading all /run/secrets files
   *
   * @see com.diamondq.common.config.spi.ConfigSource#getConfiguration(java.lang.String, java.util.List)
   */
  @Override
  public List<ConfigDataTuple> getConfiguration(String pEnvironment, List<String> pProfiles) {
    sLogger.trace("getConfiguration({}, {})", pEnvironment, pProfiles);

    List<ConfigDataTuple> results = new ArrayList<>();

    File top = new File("/run/secrets");
    if (top.exists() == false)
      top = new File("run/secrets");
    if (top.exists() == true) {

      for (File secretFile : Objects.requireNonNull(top.listFiles())) {
        if (secretFile.canRead() == true)
          try {
            results.add(ConfigDataTuple.builder().name(secretFile.getName()).source(this)
              .mediaType("application/x-rawproperty").stream(new FileInputStream(secretFile)).build());
          }
          catch (IOException ex) {
            sLogger.error("Can't load secrets", ex);
          }
      }
    }

    sLogger.trace("getConfiguration(...) -> {}", results);
    return results;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getClass().getSimpleName() + "{name=" + getName() + "}";
  }
}
