package com.diamondq.common.config.format.dockersecrets;

import com.diamondq.common.config.spi.ConfigDataTuple;
import com.diamondq.common.config.spi.ConfigNode;
import com.diamondq.common.config.spi.ConfigParser;
import com.diamondq.common.config.spi.ConfigProp;
import com.diamondq.common.config.spi.NodeType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * The Docker Secrets format reads redacted values into a section of the Config hierarchy.
 */
@ApplicationScoped
public class DockerSecretsParser implements ConfigParser {

  private static final NodeType sNodeType = NodeType.builder().isExplicitType(true)
    .type(ConfigProp.builder().configSource("").value(DockerSecretsParser.class.getName()).build()).build();

  /**
   * Default constructor
   */
  public DockerSecretsParser() {

  }

  /**
   * @see com.diamondq.common.config.spi.ConfigParser#getReconstructionNodeType()
   */
  @Override
  public NodeType getReconstructionNodeType() {
    return sNodeType;
  }

  /**
   * @see com.diamondq.common.config.spi.ConfigParser#getReconstructionParams()
   */
  @Override
  public Map<String, String> getReconstructionParams() {
    return Collections.emptyMap();
  }

  /**
   * @see com.diamondq.common.config.spi.ConfigParser#parse(com.diamondq.common.config.spi.ConfigDataTuple)
   */
  @Override
  public List<ConfigNode> parse(ConfigDataTuple pData) throws IOException {
    InputStream stream = pData.getStream();
    String key = pData.getName();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int bytesRead;
    while ((bytesRead = stream.read(buffer)) != -1)
      baos.write(buffer, 0, bytesRead);
    baos.flush();
    byte[] bytes = baos.toByteArray();
    String data = new String(bytes, Charset.forName("UTF-8")).trim();
    String configSource = pData.getSource().getName() + ":" + key;

    ConfigNode valueNode = ConfigNode.builder().name(key)
      .putMetaData("redact", ConfigProp.builder().value("true").configSource(configSource).build())
      .type(NodeType.builder().build()).value(ConfigProp.builder().value(data).configSource(configSource).build())
      .build();
    ConfigNode secretsNode =
      ConfigNode.builder().name("secrets").type(NodeType.builder().build()).putChildren(key, valueNode).build();
    ConfigNode rootNode =
      ConfigNode.builder().name("").type(NodeType.builder().build()).putChildren("secrets", secretsNode).build();
    return Collections.singletonList(rootNode);
  }

  /**
   * @see com.diamondq.common.config.spi.ConfigParser#canParse(java.util.Optional, java.lang.String)
   */
  @Override
  public boolean canParse(Optional<String> pMediaType, @Nullable String pFileName) {
    if ("application/x-rawproperty".equals(pMediaType.orElse("application/unknown")) == true)
      return true;
    return false;
  }

  /**
   * @see com.diamondq.common.config.spi.ConfigParser#getFileExtensions()
   */
  @Override
  public Collection<String> getFileExtensions() {
    return Collections.emptyList();
  }
}
