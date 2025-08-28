package io.github.xrickastley.originsmath.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import dev.isxander.yacl3.api.Binding;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;

public class OriginsMathConfig {
	public static Text textWithLink(MutableText text, String link) {
		final Style newStyle = text
			.getStyle()
			.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link))
			.withColor(Formatting.BLUE)
			.withUnderline(true);

		text.setStyle(newStyle);

		return text;
	}

	private static OptionDescription createDescription(Entry<?> entry, Text... prepend) {
		Text[] texts;
		
		if (entry.byServer) {
			texts = Arrays.copyOf(prepend, prepend.length + 2);
			texts[prepend.length] = Text.literal("");
			texts[prepend.length + 1] = Text.literal("This value has been set by the server and can not be changed.").formatted(Formatting.RED, Formatting.BOLD);
		} else {
			texts = prepend;
		}

		return OptionDescription.of(texts);
	}

	public static YetAnotherConfigLib createBuilder() {
		return YetAnotherConfigLib.createBuilder()
			.title(Text.literal("Origins: Math Config"))
			.category(
				ConfigCategory.createBuilder()
					.name(Text.literal("Experiements"))
					.tooltip(Text.literal("Origins: Math Experiements"))
					.option(
						Experiments.EXTENDED_COMPATIBILITY_THROUGH_ASM
							.createOptionBuilder()
							.description(
								OriginsMathConfig.createDescription(
									Experiments.EXTENDED_COMPATIBILITY_THROUGH_ASM,
									Text.literal("Whether or not the ")
										.append(OriginsMathConfig.textWithLink(Text.literal("Extended Compatibility through ASM"), "https://origins-math.readthedocs.io/en/latest/experiments/extended_compatibility_through_asm/"))
										.append(Text.literal(" experiment is enabled."))
								)
							)
							.controller(TickBoxControllerBuilder::create)
							.build()
					)
					.build()
			)
			.build();
	}

	public static class Experiments {
		public static final Entry<Boolean> EXTENDED_COMPATIBILITY_THROUGH_ASM = new Entry<Boolean>(
			"Extended Compatibility through ASM",
			true, 
			EntryLoader.of((config, value) -> config.extended_compatibility_through_asm = value, config -> config.extended_compatibility_through_asm)
		);
	}

	public static class Entry<T> {
		private static final List<Entry<?>> INSTANCES = new ArrayList<>();
		private final String name;
		private final EntryLoader<T> loader;
		private T defaultValue;
		private T value;
		private boolean byServer = false;

		public static void desyncEntries() {
			Entry.INSTANCES.forEach(Entry::desync);
		}

		private Entry(final String name, T defaultValue) {
			this(name, defaultValue, null);
		}

		private Entry(final String name, T defaultValue, EntryLoader<T> loader) {
			this.name = name;
			this.loader = loader;
			this.defaultValue = defaultValue;
			this.value = loader.loadFn.get();

			Entry.INSTANCES.add(this);
		}

		private void desync() {
			this.value = loader == null ? defaultValue : loader.loadFn.get();
			this.byServer = false;
		}

		public T getValue() {
			return this.value;
		}

		public String getName() {
			return this.name;
		}

		public void setValue(T value) {
			if (byServer) return;

			this.value = value;
			
			if (loader != null) loader.saveFn.accept(this.value);
		}

		public void setValueFromServer(T value) {
			this.value = value;
			this.byServer = true;
		}

		public Option.Builder<T> createOptionBuilder() {
			final MutableText nameText = Text.literal(this.name);

			return Option.<T>createBuilder()
				.name(nameText)
				.binding(Binding.generic(defaultValue, this::getValue, this::setValue))
				.instant(true)
				.available(!byServer);
		}
	}

	public static class EntryLoader<T> {
		protected final Consumer<T> saveFn;
		protected final Supplier<T> loadFn;

		private EntryLoader(Consumer<T> saveFn, Supplier<T> loadFn) {
			this.saveFn = saveFn;
			this.loadFn = loadFn;
		}

		public static <T> EntryLoader<T> of(BiConsumer<OriginsMathSavedConfig, T> saveFn, Function<OriginsMathSavedConfig, T> loadFn) {
			return new EntryLoader<T>(
				v -> saveFn
					.andThen((c, _v) -> OriginsMathSavedConfig.save())
					.accept(OriginsMathSavedConfig.instance(), v),
				() -> loadFn
					.apply(OriginsMathSavedConfig.instance())
			);
		}
	}
}
