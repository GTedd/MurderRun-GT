package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;

import java.util.List;

public final class AnnotationParserHandler {

  private final MurderRun plugin;
  private final List<AnnotationFeature> features;
  private final AnnotationParser<CommandSender> parser;

  public AnnotationParserHandler(
      final MurderRun plugin, final CommandManager<CommandSender> manager) {
    this.plugin = plugin;
    this.features = List.of();
    this.parser = new AnnotationParser<>(manager, CommandSender.class);
  }

  public void registerCommands() {
    this.features.forEach(feature -> feature.registerFeature(this.plugin, this.parser));
  }
}