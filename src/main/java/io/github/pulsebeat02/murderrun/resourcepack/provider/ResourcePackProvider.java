/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.resourcepack.provider;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.capability.Capabilities;
import io.github.pulsebeat02.murderrun.game.extension.nexo.NexoManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.PackWrapper;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class ResourcePackProvider implements PackProvider {

  private static final Path SERVER_PACK;

  static {
    try {
      final PackWrapper wrapper = new PackWrapper();
      SERVER_PACK = wrapper.wrapPack();
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  private final ProviderMethod method;
  private final MurderRun plugin;

  private ResourcePackRequest cached;
  private String url;

  public ResourcePackProvider(final MurderRun plugin, final ProviderMethod method) {
    this.plugin = plugin;
    this.method = method;
  }

  public void cachePack() {
    final List<ResourcePackInfo> infos = Stream.of(
      this.getMainResourceInfo(),
      this.cacheProvidedResourcesExceptionally(),
      this.getNexoPackInfo()
    )
      .flatMap(Optional::stream)
      .toList();
    final Component message = Message.RESOURCEPACK_PROMPT.build();
    final ResourcePackRequest.Builder builder = ResourcePackRequest.resourcePackRequest();
    final boolean required = GameProperties.FORCE_RESOURCEPACK;
    this.cached = builder.required(required).packs(infos).prompt(message).asResourcePackRequest();
  }

  private Optional<ResourcePackInfo> getMainResourceInfo() {
    final String url = this.getFinalUrl();
    final URI uri = URI.create(url);
    final String hash = IOUtils.getSHA1Hash(uri);
    final ResourcePackInfo info = ResourcePackInfo.resourcePackInfo().uri(uri).hash(hash).build();
    return Optional.of(info);
  }

  private Optional<ResourcePackInfo> getNexoPackInfo() {
    if (Capabilities.NEXO.isEnabled()) {
      final NexoManager manager = this.plugin.getNexoManager();
      final ResourcePackInfo packInfo = manager.getPackInfo();
      return Optional.of(packInfo);
    }
    return Optional.empty();
  }

  private Optional<ResourcePackInfo> cacheProvidedResourcesExceptionally() {
    try {
      return this.cacheProvidedResources();
    } catch (final AssertionError e) {
      return Optional.empty();
    }
  }

  private Optional<ResourcePackInfo> cacheProvidedResources() {
    try {
      final ResourcePackInfo info = this.getResourceInfo();
      return Optional.ofNullable(info);
    } catch (final AssertionError e) {
      final String msg =
        "Timed-out while retrieving resource pack hash! Consider changing the resource pack provider if currently set to MC_PACK_HOSTING!";
      throw new AssertionError(msg);
    }
  }

  private @Nullable ResourcePackInfo getResourceInfo() {
    final String url = GameProperties.BUILT_IN_RESOURCES;
    if (url.equalsIgnoreCase("none")) {
      return null;
    }
    final URI uri = URI.create(url);
    final String hash = IOUtils.getSHA1Hash(uri);
    return ResourcePackInfo.resourcePackInfo().uri(uri).hash(hash).build();
  }

  public abstract String getRawUrl();

  @Override
  public ResourcePackRequest getResourcePackRequest() {
    return this.cached;
  }

  @Override
  public void start() {}

  @Override
  public void shutdown() {}

  public ProviderMethod getMethod() {
    return this.method;
  }

  public static Path getServerPack() {
    return SERVER_PACK;
  }

  public String getFinalUrl() {
    if (this.url == null) {
      this.url = this.getRawUrl();
    }
    return this.url;
  }
}
