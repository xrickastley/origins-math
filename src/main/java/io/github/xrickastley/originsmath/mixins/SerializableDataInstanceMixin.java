package io.github.xrickastley.originsmath.mixins;

import java.util.HashMap;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.apace100.calio.data.SerializableData;
import io.github.xrickastley.originsmath.interfaces.SDIEntityInjection;
import io.github.xrickastley.originsmath.util.ResourceBacked;
import net.minecraft.entity.Entity;

@Mixin(SerializableData.Instance.class)
public abstract class SerializableDataInstanceMixin implements SDIEntityInjection {
	@Unique
	private Entity originsmath$targetEntity = null;

	@Shadow(remap = false)
    private final HashMap<String, Object> data = new HashMap<>();

	@Shadow(remap = false)
	public abstract <T> T get(String name);

	@Unique
	@Override
	public void setEntity(Entity entity) {
		this.originsmath$targetEntity = entity;
	}

	@ModifyReturnValue(
		method = "get",
		at = @At("RETURN"),
		remap = false
	)
	public <T> T injectResourceLinkToGet(T original) {
		if (!(original instanceof final ResourceBacked rb)) 
			return original;

		rb.setTargetEntity(originsmath$targetEntity);

		try {
			// someClass#method > SerializableData$Instance.get > SerializableData$Instance.originsmath$injectResourceLinkToGet > SerializableData$Instance.originsmath$getCallingContext (4)
			final LineNumberNode callCtx = this.getCallingContext(4);

			return callCtx != null
				? SerializableDataInstanceMixin.cast(precastResourceBacked(rb, callCtx))
				: original;
		} catch (Exception e) {
			return original;
		}
	}

    @SuppressWarnings("unchecked")
	private static <T> T cast(Object any) {
        return (T) any;
    }

	@Unique
	public <T> void forceInjectBytecode(String name, CallbackInfoReturnable<T> cir) {
		final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		final String callerClassName = stackTrace[3].getClassName();
		final String callerMethodName = stackTrace[3].getMethodName();
        final int callerLineNumber = stackTrace[3].getLineNumber();

		try {
			final Class<?> callerClass = Class.forName(callerClassName);
			final String classDescriptor = Type.getInternalName(callerClass);

			// Only target direct SerializableData$Instance#get() calls.
			if (classDescriptor.equals("io/github/apace100/calio/data/SerializableData$Instance")) return;

			System.out.println("descriptor: " + classDescriptor);
			System.out.println(callerMethodName);

			final ClassReader classReader = new ClassReader(Type.getInternalName(callerClass));
			final ClassNode classNode = new ClassNode();

			classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
			
			for (MethodNode method : classNode.methods) {
			    if (!method.name.equals(callerMethodName)) continue; 
				
				System.out.println(method);
				System.out.println("callerLineNumber: " + callerLineNumber);

				for (AbstractInsnNode insn : method.instructions.toArray()) {
					if (!(insn instanceof final LineNumberNode lineNode)) continue;

					if (lineNode.line != callerLineNumber) continue;
					
					System.out.println("Found calling method: " + method);
					System.out.println(String.format("\tName: %s | Line Number: %d", method.name, lineNode.line));

					AbstractInsnNode current = lineNode.getNext();

					System.out.println("current START");
					int i = 0;
					while (current != null) {
						if (i >= 10) {
							System.out.println("i has reached 1+! ABORTING...");

							break;
						}

						System.out.println("insnNode class: " + current.getClass().getName());

						i += 1;
						
						if (current instanceof final MethodInsnNode methodIsn) {
							final String methodDescriptor = "L" + methodIsn.owner + "." + methodIsn.name;

							System.out.println(methodDescriptor);

							if (methodDescriptor.equals("Lio/github/apace100/calio/data/SerializableData$Instance.get")) {
								if (current.getNext() instanceof final TypeInsnNode typeInsn) {
									switch (typeInsn.desc) {
										case "java/lang/Integer":
											System.out.println("intValue");
											// return rb.intValue();
											break;
										case "java/lang/Double":
											System.out.println("doubleValue");
											// return rb.doubleValue();
											break;
										case "java/lang/Float":
											System.out.println("floatValue");
											// return rb.floatValue();
											break;
										default:
											break;
									}
								}

								break;
							}
						}
						
						// System.out.println("methodIsn.name: " + methodIsn.name);

						current = current.getNext();
					}
					System.out.println("current END");
					
					return;
				}
			}

			// System.out.println("Origins: Math cannot find the calling method!");
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	/**
	 * Utility method to get the calling context, i.e. the method that used 
	 * {@code SerializableData$Instance#get()} <br> <br>
	 * 
	 * This gets the INSN with the corresponding method name and line number, based on the
	 * Stack Trace.
	 * 
	 * @param backtrack The amount of "backtracks" to do. This is the amount of methods called 
	 * after the call to {@code SerializableData$Instance#get()}. Add {@code 1} to account for
	 * this method.
	 */
	@Unique
	private @Nullable LineNumberNode getCallingContext(int backtrack) {
		final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		final String callerClassName = stackTrace[backtrack].getClassName();
		final String callerMethodName = stackTrace[backtrack].getMethodName();
        final int callerLineNumber = stackTrace[backtrack].getLineNumber();

		try {
			final Class<?> callerClass = Class.forName(callerClassName);
			final String classDescriptor = Type.getInternalName(callerClass);

			// Exclude calls from io/github/apace100/calio/data/SerializableData$Instance as we've already handled them in their respective injectors.
			if (classDescriptor.equals("io/github/apace100/calio/data/SerializableData$Instance")) return null;

			final ClassReader classReader = new ClassReader(Type.getInternalName(callerClass));
			final ClassNode classNode = new ClassNode();

			classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
			
			for (final MethodNode method : classNode.methods) {
			    if (!method.name.equals(callerMethodName)) continue; 

				System.out.println(String.format("Found method: %s;%s", classDescriptor, method.name));
				
				for (AbstractInsnNode insn : method.instructions.toArray()) {
					if (!(insn instanceof final LineNumberNode lineNode)) continue;

					if (lineNode.line != callerLineNumber) continue;

					return lineNode;
				}
			}
		} catch (Exception e) {
			System.err.println(e);
		}
		
		return null;
	}

	/**
	 * Utility method that automatically casts the {@code ResourceBacked} instance based on the 
	 * calling context of {@code SerializableData$Instance#get()} <br> <br>
	 * 
	 * This takes the {@code ResourceBacked} instance and casts it manually to it's {@code int},
	 * {@code double} or {@code float} forms when it sees an immediate {@code CHECKCAST} call that
	 * casts the {@code ResourceBacked} instance into those types, in order to prevent the
	 * {@code ClassCastException} error.
	 * 
	 * @param rb The {@code ResourceBacked} instance to attempt a precast of.
	 * @param lineNode The calling context, obtained from {@code getCallingContext()}.
	 * @return An instance of {@code Integer}, {@code Float}, {@code Double}, or the original 
	 * {@code ResourceBacked}, if no immediate {@code CHECKCAST} instruction was made after 
	 * call to {@code SerializableData$Instance.get()}. 
	 */
	@Unique
	private @Nullable Number precastResourceBacked(final @SuppressWarnings("rawtypes") ResourceBacked rb, final LineNumberNode lineNode) {
		AbstractInsnNode current = lineNode.getNext();

		while (current != null) {
			if (current instanceof final MethodInsnNode methodIsn) {
				final String methodDescriptor = methodIsn.owner + "." + methodIsn.name;

				if (methodDescriptor.equals("io/github/apace100/calio/data/SerializableData$Instance.get")) {
					AbstractInsnNode cur2 = current.getNext();

					if (cur2 instanceof final TypeInsnNode typeInsn && typeInsn.getOpcode() == Opcodes.CHECKCAST) {
						switch (typeInsn.desc) {
							case "java/lang/Integer":
								return rb.intValue();
							case "java/lang/Double":
								return rb.doubleValue();
							case "java/lang/Float":
								return rb.floatValue();
							default:
								return rb;
						}
					}
				
					break;
				}
			}
		
			current = current.getNext();
		}

		return rb;
	}

	@Inject(
		method = "getInt",
		at = @At("HEAD"),
		cancellable = true,
		remap = false
	)
	public void injectResourceLinkToGetInt(String name, CallbackInfoReturnable<Integer> cir) {
		if (!(this.get(name) instanceof ResourceBacked rb)) return;
		
		rb.setTargetEntity(originsmath$targetEntity);
		cir.setReturnValue(rb.intValue());
	}

	@Inject(
		method = "getFloat",
		at = @At("HEAD"),
		cancellable = true,
		remap = false
	)
	public void injectResourceLinkToGetFloat(String name, CallbackInfoReturnable<Float> cir) {
		if (!(this.get(name) instanceof ResourceBacked rb)) return;
		
		rb.setTargetEntity(originsmath$targetEntity);
		cir.setReturnValue(rb.floatValue());
	}

	@Inject(
		method = "getDouble",
		at = @At("HEAD"),
		cancellable = true,
		remap = false
	)
	public void injectResourceLinkToGetDouble(String name, CallbackInfoReturnable<Double> cir) {
		if (!(this.get(name) instanceof ResourceBacked rb)) return;
		
		rb.setTargetEntity(originsmath$targetEntity);
		cir.setReturnValue(rb.doubleValue());
	}
}
