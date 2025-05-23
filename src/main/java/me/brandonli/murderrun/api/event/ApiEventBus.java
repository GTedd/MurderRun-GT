/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.api.event;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import io.github.classgraph.ScanResult;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.api.event.generated.GeneratedEventClass;
import me.brandonli.murderrun.api.event.generated.NonInvokable;
import me.brandonli.murderrun.utils.ClassGraphUtils;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public final class ApiEventBus implements EventBus {

  private static final Map<Class<? extends MurderRunEvent>, GeneratedEventClass> EVENT_CACHE;
  private static final Comparator<EventSubscription<?>> EVENT_COMPARATOR;

  static {
    try {
      final Set<Class<? extends MurderRunEvent>> classes = scanClasses();
      final Map<Class<? extends MurderRunEvent>, GeneratedEventClass> temp = new HashMap<>();
      for (final Class<? extends MurderRunEvent> event : classes) {
        final GeneratedEventClass handle = new GeneratedEventClass(event);
        temp.put(event, handle);
      }
      EVENT_CACHE = Map.copyOf(temp);
      EVENT_COMPARATOR = Comparator.comparingInt(EventSubscription::getPriority);
    } catch (final Throwable e) {
      throw new AssertionError(e);
    }
  }

  private static Set<Class<? extends MurderRunEvent>> scanClasses() {
    final ScanResult result = ClassGraphUtils.getCachedScanResult();
    return result
      .getClassesImplementing(MurderRunEvent.class)
      .loadClasses(MurderRunEvent.class)
      .stream()
      .filter(Class::isInterface)
      .map(clazz1 -> (Class<? extends MurderRunEvent>) clazz1)
      .collect(Collectors.toSet());
  }

  private final ListMultimap<Class<? extends MurderRunEvent>, EventSubscription<?>> subscriptions;
  private final MurderRun api;

  public ApiEventBus(final MurderRun api) {
    this.api = api;
    this.subscriptions = ArrayListMultimap.create();
  }

  @Override
  public <T extends MurderRunEvent> EventSubscription<T> subscribe(
    final Plugin plugin,
    final Class<T> eventType,
    final Consumer<? super T> handler,
    final int priority
  ) {
    final EventSubscription<T> subscription = new ApiEventSubscription<>(plugin, eventType, handler, priority);
    synchronized (this.subscriptions) {
      this.sortSubscribers(eventType, subscription);
      final Set<Class<? extends MurderRunEvent>> parentEventTypes = this.getAllApplicableEvents(eventType);
      for (final Class<? extends MurderRunEvent> parentType : parentEventTypes) {
        final List<EventSubscription<?>> parentList = this.subscriptions.get(parentType);
        parentList.add(subscription);
        parentList.sort(EVENT_COMPARATOR);
      }
    }
    return subscription;
  }

  private <T extends MurderRunEvent> @NotNull Set<Class<? extends MurderRunEvent>> getAllApplicableEvents(final Class<T> eventType) {
    final Set<Class<? extends MurderRunEvent>> parentEventTypes = this.findParentEventTypes(eventType);
    parentEventTypes.remove(eventType);
    return parentEventTypes;
  }

  private <T extends MurderRunEvent> void sortSubscribers(final Class<T> eventType, final EventSubscription<T> subscription) {
    final List<EventSubscription<?>> concreteList = this.subscriptions.get(eventType);
    concreteList.add(subscription);
    concreteList.sort(EVENT_COMPARATOR);
  }

  @Override
  public <T extends MurderRunEvent> EventSubscription<T> subscribe(
    final Plugin plugin,
    final Class<T> eventType,
    final Consumer<? super T> handler
  ) {
    return this.subscribe(plugin, eventType, handler, 0);
  }

  @Override
  public <T extends MurderRunEvent> void unsubscribe(final EventSubscription<T> subscription) {
    synchronized (this.subscriptions) {
      final Class<T> eventType = subscription.getEventType();
      final List<EventSubscription<?>> list = this.subscriptions.get(eventType);
      list.remove(subscription);
    }
    subscription.unsubscribe();
  }

  @Override
  public void unsubscribe(final Plugin plugin) {
    synchronized (this.subscriptions) {
      final Collection<EventSubscription<?>> handle = this.subscriptions.values();
      handle.removeIf(subscription -> this.unsubscribePlugin(plugin, subscription));
    }
  }

  @Override
  public <T extends MurderRunEvent> void unsubscribe(final Plugin plugin, final Class<T> eventType) {
    synchronized (this.subscriptions) {
      final List<EventSubscription<?>> list = this.subscriptions.get(eventType);
      list.removeIf(subscription -> this.unsubscribePlugin(plugin, subscription));
    }
  }

  private boolean unsubscribePlugin(final Plugin plugin, final EventSubscription<?> subscription) {
    final Plugin subscribedPlugin = subscription.getPlugin();
    if (plugin.equals(subscribedPlugin)) {
      subscription.unsubscribe();
      return true;
    } else {
      return false;
    }
  }

  public void unsubscribeAll() {
    synchronized (this.subscriptions) {
      final Collection<EventSubscription<?>> handle = this.subscriptions.values();
      handle.forEach(EventSubscription::unsubscribe);
      this.subscriptions.clear();
    }
  }

  @Override
  public @Unmodifiable Set<EventSubscription<?>> getSubscriptions(final Plugin plugin) {
    synchronized (this.subscriptions) {
      final Collection<EventSubscription<?>> allSubscriptions = this.subscriptions.values();
      final Stream<EventSubscription<?>> filteredSubscriptions = allSubscriptions
        .stream()
        .filter(subscription -> plugin.equals(subscription.getPlugin()));
      return filteredSubscriptions.collect(Collectors.toUnmodifiableSet());
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends MurderRunEvent> @Unmodifiable Set<EventSubscription<T>> getSubscriptions(
    final Plugin plugin,
    final Class<T> eventType
  ) {
    synchronized (this.subscriptions) {
      final List<EventSubscription<?>> eventTypeSubscriptions = this.subscriptions.get(eventType);
      final Stream<EventSubscription<?>> filteredSubscriptions = eventTypeSubscriptions
        .stream()
        .filter(subscription -> plugin.equals(subscription.getPlugin()));
      return filteredSubscriptions.map(subscription -> (EventSubscription<T>) subscription).collect(Collectors.toUnmodifiableSet());
    }
  }

  @Override
  public @Unmodifiable <T extends MurderRunEvent> Set<EventSubscription<?>> getAllSubscriptions(
    final Plugin plugin,
    final Class<T> eventType
  ) {
    synchronized (this.subscriptions) {
      final Collection<EventSubscription<?>> allSubscriptions = this.subscriptions.values();
      final Stream<EventSubscription<?>> filteredByPlugin = allSubscriptions
        .stream()
        .filter(subscription -> plugin.equals(subscription.getPlugin()));
      final Stream<EventSubscription<?>> filteredByEventType = filteredByPlugin.filter(subscription ->
        subscription.getEventType().isAssignableFrom(eventType)
      );
      return filteredByEventType.map(subscription -> (EventSubscription<?>) subscription).collect(Collectors.toUnmodifiableSet());
    }
  }

  @SuppressWarnings("unchecked")
  public <T extends MurderRunEvent> boolean post(final Class<T> type, final Object... args) {
    if (type.isAnnotationPresent(NonInvokable.class)) {
      final String name = type.getName();
      final String msg = "Event %s is marked as @NonInvokable and cannot be posted".formatted(name);
      throw new AssertionError(msg);
    }
    final GeneratedEventClass handle = requireNonNull(EVENT_CACHE.get(type));
    final T event = (T) handle.newInstance(this.api, args);
    synchronized (this.subscriptions) {
      final Collection<Map.Entry<Class<? extends MurderRunEvent>, EventSubscription<?>>> entries = this.subscriptions.entries();
      final List<EventSubscription<?>> sorted = entries
        .stream()
        .filter(entry -> entry.getKey().isInstance(event))
        .map(Map.Entry::getValue)
        .filter(EventSubscription::isActive)
        .sorted(Comparator.comparingInt(EventSubscription::getPriority))
        .collect(Collectors.toList());
      for (final EventSubscription<?> raw : sorted) {
        @SuppressWarnings("unchecked")
        final EventSubscription<? super T> sub = (EventSubscription<? super T>) raw;
        final Consumer<? super T> eventHandler = sub.getHandler();
        eventHandler.accept(event);
        if (event instanceof final Cancellable cancellable && cancellable.isCancelled()) {
          return true;
        }
      }
    }
    return false;
  }

  private Set<Class<? extends MurderRunEvent>> findParentEventTypes(final Class<? extends MurderRunEvent> eventType) {
    final Set<Class<? extends MurderRunEvent>> parentTypes = new HashSet<>();
    final Queue<Class<?>> processingQueue = new LinkedList<>();
    final Class<?>[] directInterfaces = eventType.getInterfaces();
    Collections.addAll(processingQueue, directInterfaces);
    while (!processingQueue.isEmpty()) {
      final Class<?> currentInterface = requireNonNull(processingQueue.poll());
      final boolean isSubtype = MurderRunEvent.class.isAssignableFrom(currentInterface);
      if (isSubtype) {
        @SuppressWarnings("unchecked")
        final Class<? extends MurderRunEvent> castedInterface = (Class<? extends MurderRunEvent>) currentInterface;
        final boolean alreadyAdded = parentTypes.contains(castedInterface);
        if (!alreadyAdded) {
          parentTypes.add(castedInterface);
          final Class<?>[] superInterfaces = currentInterface.getInterfaces();
          Collections.addAll(processingQueue, superInterfaces);
        }
      }
    }
    return parentTypes;
  }
}
