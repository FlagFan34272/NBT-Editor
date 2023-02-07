package com.luneruniverse.minecraft.mod.nbteditor;

import java.lang.reflect.Proxy;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.NbtTypes;

public class NbtTypeModifier {
	
	public static void loadClass() {}
	
	private static final NbtType<?>[] VALUES = NbtTypes.VALUES;
	public static final NbtType<NbtString> NBT_STRING_TYPE = makeMutable(NbtString.TYPE);
	
	@SuppressWarnings("unchecked")
	public static <T extends NbtElement> NbtType<T> makeMutable(NbtType<T> type) {
		if (!type.isImmutable())
			return type;
		for (int i = 0; i < VALUES.length; i++) {
			if (VALUES[i] == type) {
				NbtType<T> newType = (NbtType<T>) Proxy.newProxyInstance(NbtTypeModifier.class.getClassLoader(),
						new Class[] { NbtType.class }, (obj, method, args) -> {
					if (method.getName().equals("isImmutable"))
						return false;
					
					return method.invoke(type, args);
				});
				VALUES[i] = newType;
				return newType;
			}
		}
		return null;
	}
	
}
