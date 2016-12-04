package com.vrbridge.protocol;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class SimpleByteProtocol implements TypeDrivenProtocol {
	protected boolean ignoreNotFoundProps = false;
	
	protected static Charset utf8 = Charset.forName("utf8");
	

    protected byte popByte(ByteFragment data) throws IOException {
        return data.bytes.buf[data.pop(1)];
    }

    protected boolean popBoolean(ByteFragment data) throws IOException {
        return (data.bytes.buf[data.pop(1)] & 0xff) != 0;
    }

    protected char popChar(ByteFragment data) throws IOException {
    	int from = data.pop(2);
        return (char)(((data.bytes.buf[from] & 0xff) << 8) + 
        		((data.bytes.buf[from + 1] & 0xff) << 0));
    }

    protected short popShort(ByteFragment data) throws IOException {
    	int from = data.pop(2);
        return (short)(((data.bytes.buf[from] & 0xff) << 8) + 
        		((data.bytes.buf[from + 1] & 0xff) << 0));
    }

    protected int popInt(ByteFragment data) throws IOException {
    	int from = data.pop(4);
        return (((data.bytes.buf[from] & 0xff) << 24) + 
        		((data.bytes.buf[from + 1] & 0xff) << 16) + 
        		((data.bytes.buf[from + 2] & 0xff) << 8) + 
        		((data.bytes.buf[from + 3] & 0xff) << 0));
    }

    protected float popFloat(ByteFragment data) throws IOException {
        return Float.intBitsToFloat(popInt(data));
    }

    protected long popLong(ByteFragment data) throws IOException {
    	int from = data.pop(8);
        return (((long)data.bytes.buf[from] << 56) +
                ((long)(data.bytes.buf[from + 1] & 255) << 48) +
                ((long)(data.bytes.buf[from + 2] & 255) << 40) +
                ((long)(data.bytes.buf[from + 3] & 255) << 32) +
                ((long)(data.bytes.buf[from + 4] & 255) << 24) +
                ((data.bytes.buf[from + 5] & 255) << 16) +
                ((data.bytes.buf[from + 6] & 255) <<  8) +
                ((data.bytes.buf[from + 7] & 255) <<  0));
    }

    protected <T> Object popBasic(ByteFragment stack, Class<T> type) throws IOException {
    	if (type == Byte.TYPE || type == Byte.class) {
    		return popByte(stack);
        } else if (type == Boolean.TYPE || type == Boolean.class) {
            return popBoolean(stack);
        } else if (type == Short.TYPE || type == Short.class) {
            return popShort(stack);
        } else if (type == Character.TYPE || type == Character.class) {
            return popChar(stack);
    	} else if (type == Integer.TYPE || type == Integer.class) {
            return popInt(stack);
        } else if (type == Float.TYPE || type == Float.class) {
        	return popFloat(stack);
        } else if (type == Long.TYPE || type == Long.class) {
        	return popLong(stack);
        } else if (type == Double.TYPE || type == Double.class) {
            return popDouble(stack);
        } else {
        	throw new IOException("Unsupported basic type: " + type.getName());
        }
    }

    protected <T> boolean isBasic(Class<T> type) {
    	return type.isPrimitive() || Number.class.isAssignableFrom(type) || 
    			type == Character.class || type == Boolean.class;
    }
    
    protected <T> int getBasicTypeSize(Class<T> type) throws IOException {
    	if (type == Byte.TYPE || type == Byte.class || 
    			type == Boolean.TYPE || type == Boolean.class) {
            return 1;
        } else if (type == Short.TYPE || type == Short.class || 
        		type == Character.TYPE || type == Character.class) {
            return 2;
    	} else if (type == Integer.TYPE || type == Integer.class || 
    			type == Float.TYPE || type == Float.class) {
        	return 4;
        } else if (type == Long.TYPE || type == Long.class || 
        		type == Double.TYPE || type == Double.class) {
            return 8;
        } else {
        	throw new IOException("Unsupported basic type: " + type.getName());
        }
    }
    
    protected double popDouble(ByteFragment data) throws IOException {
        return Double.longBitsToDouble(popLong(data));
    }

    protected byte[] asBytes(ByteFragment data) throws IOException {
    	byte[] ret = new byte[data.length];
    	if (data.length > 0) {
    		System.arraycopy(data.bytes.buf, data.from, ret, 0, data.length);
    	}
    	return ret;
    }
    
    protected String asString(ByteFragment data) throws IOException {
    	return new String(data.bytes.buf, data.from, data.length, utf8);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object asArray(ByteFragment data, Class type) throws IOException {
    	ByteFragment stack = data.copy();
        Class ccl = type.getComponentType();
        if (ccl == null) {
        	ccl = Object.class;
        }
        Object array = null;
        if (ccl.isPrimitive()) {
            if (ccl == Integer.TYPE) {
            	int[] ret = new int[stack.length / 4];
                for (int i = 0; i < ret.length; i++)
                	ret[i] = popInt(stack);
                array = ret;
            } else if (ccl == Byte.TYPE) {
            	byte[] ret = asBytes(stack);
            	array = ret;
            } else if (ccl == Long.TYPE) {
            	long[] ret = new long[stack.length / 8];
                for (int i = 0; i < ret.length; i++)
                	ret[i] = popLong(stack);
                array = ret;
            } else if (ccl == Float.TYPE) {
            	float[] ret = new float[stack.length / 4];
                for (int i = 0; i < ret.length; i++)
                	ret[i] = popFloat(stack);
                array = ret;
            } else if (ccl == Double.TYPE) {
            	double[] ret = new double[stack.length / 8];
                for (int i = 0; i < ret.length; i++)
                	ret[i] = popDouble(stack);
                array = ret;
            } else if (ccl == Short.TYPE) {
            	short[] ret = new short[stack.length / 2];
                for (int i = 0; i < ret.length; i++)
                	ret[i] = popShort(stack);
                array = ret;
            } else if (ccl == Character.TYPE) {
            	char[] ret = new char[stack.length / 2];
                for (int i = 0; i < ret.length; i++)
                	ret[i] = popChar(stack);
                array = ret;
            } else if (ccl == Boolean.TYPE) {
            	boolean[] ret = new boolean[stack.length];
                for (int i = 0; i < ret.length; i++)
                	ret[i] = popBoolean(stack);
                array = ret;
            } else {
                throw new InternalError();
            }
        } else {
        	if (isBasic(ccl)) {
                array = Array.newInstance(ccl, stack.length / getBasicTypeSize(ccl));
                Object[] ret = (Object[])array;
                for (int i = 0; i < ret.length; i++)
                	ret[i] = popBasic(stack, ccl);
        	} else {
            	List<Object> list = new ArrayList<Object>();
                while (stack.length > 0) {
                	list.add(popObject(stack, ccl));
                }
                array = list.toArray((Object[])Array.newInstance(ccl, list.size()));
        	}
        }
        return array;
    }

    @Override
    public <T> T bytesToObject(ByteFragment data, Class<T> type) throws IOException {
    	return asObject(data, type);
    }
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T> T asObject(ByteFragment data, Class<T> type) throws IOException {
		if (data.length < 0)
			return null;
		if (isBasic(type)) {
			return (T)popBasic(data.copy(), type);
		} else if (type.isArray()) {
			return (T)asArray(data, type);
		} else if (type == String.class) {
			return (T)asString(data);
		} else if (type == Object.class) {
			return (T)data.copy();
		} else {
			T ret;
			try {
				ret = type.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new IOException("Error instantiating object of type " + type.getName(), e);
			}
			ByteFragment stack = data.copy();
			while (stack.length > 0) {
				String propName = asString(popByteFragment(stack));
				ByteFragment valueData = popByteFragment(stack);
				String setterName = "set" + propName.substring(0, 1).toUpperCase() + 
						propName.substring(1);
				Class propType = null;
				Method setter = null;
				for (Method m : type.getMethods()) {
					if (m.getName().equals(setterName) && m.getParameterTypes().length == 1 &&
							(m.getModifiers() & Modifier.STATIC) == 0 &&
							(m.getModifiers() & Modifier.PUBLIC) != 0) {
						setter = m;
						propType = m.getParameterTypes()[0];
						break;
					}
				}
				Field propField = null;
				if (setter == null) {
					for (Field f : type.getDeclaredFields()) {
						if (f.getName().equals(propName) && 
								(f.getModifiers() & Modifier.STATIC) == 0 &&
								(f.getModifiers() & Modifier.FINAL) == 0) {
							if ((f.getModifiers() & Modifier.PUBLIC) == 0) {
								f.setAccessible(true);
							}
							propField = f;
							propType = f.getType();
							break;
						}
					}
				}
				if (propType == null) {
					if (ignoreNotFoundProps) {
						continue;
					} else {
						throw new IOException("Property '" + propName + "' is not found in " +
								"type " + type.getName());
					}
				}
				Object value = asObject(valueData, propType);
				if (setter != null) {
					try {
						setter.invoke(ret, value);
					} catch (IllegalArgumentException | InvocationTargetException |
							IllegalAccessException e) {
						throw new IOException("Error invocation " + setterName + " method " +
								"for type " + type.getName());
					}
				} else {
					try {
						propField.set(ret, value);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						throw new IOException("Error setting " + propName + " field " +
								"for type " + type.getName());
					}
				}
			}
			return ret;
		}
	}

	protected <T> T popObject(ByteFragment stack, Class<T> type) throws IOException {
		return asObject(popByteFragment(stack), type);
	}
	
	protected ByteFragment popByteFragment(ByteFragment stack) throws IOException {
		int length = popInt(stack);
		return new ByteFragment(stack.bytes, stack.pop(length), length);
	}
	
    protected void pushByte(ByteFragment stack, byte val) {
    	int off = stack.push(1);
        stack.bytes.buf[off] = val;
    }

    protected void pushBoolean(ByteFragment stack, boolean val) {
    	int off = stack.push(1);
        stack.bytes.buf[off] = (byte)(val ? 1 : 0);
    }

    protected void pushChar(ByteFragment stack, char val) {
       	int off = stack.push(2);
       	stack.bytes.buf[off] = (byte)(val >>> 8);
       	stack.bytes.buf[off + 1] = (byte)val;
    }

    protected void pushShort(ByteFragment stack, short val) {
       	int off = stack.push(2);
       	stack.bytes.buf[off] = (byte)(val >>> 8);
       	stack.bytes.buf[off + 1] = (byte)val;
    }

    protected void pushInt(ByteFragment stack, int val) {
       	int off = stack.push(4);
       	stack.bytes.buf[off] = (byte)(val >>> 24);
       	stack.bytes.buf[off + 1] = (byte)(val >>> 16);
       	stack.bytes.buf[off + 2] = (byte)(val >>> 8);
       	stack.bytes.buf[off + 3] = (byte)val;
    }

    protected void pushFloat(ByteFragment stack, float val) {
        pushInt(stack, Float.floatToIntBits(val));
    }

    protected void pushLong(ByteFragment stack, long val) {
       	int off = stack.push(8);
       	stack.bytes.buf[off] = (byte)(val >>> 56);
       	stack.bytes.buf[off + 1] = (byte)(val >>> 48);
       	stack.bytes.buf[off + 2] = (byte)(val >>> 40);
       	stack.bytes.buf[off + 3] = (byte)(val >>> 32);
       	stack.bytes.buf[off + 4] = (byte)(val >>> 24);
       	stack.bytes.buf[off + 5] = (byte)(val >>> 16);
       	stack.bytes.buf[off + 6] = (byte)(val >>> 8);
       	stack.bytes.buf[off + 7] = (byte)val;
    }

    protected void pushDouble(ByteFragment stack, double val) {
        pushLong(stack, Double.doubleToLongBits(val));
    }

    protected void pushBasic(ByteFragment stack, Object val) throws IOException {
    	Class<?> type = val.getClass();
    	if (type == Byte.TYPE || type == Byte.class) {
    		pushByte(stack, (Byte)val);
        } else if (type == Boolean.TYPE || type == Boolean.class) {
            pushBoolean(stack, (Boolean)val);
        } else if (type == Short.TYPE || type == Short.class) {
            pushShort(stack, (Short)val);
        } else if (type == Character.TYPE || type == Character.class) {
            pushChar(stack, (Character)val);
    	} else if (type == Integer.TYPE || type == Integer.class) {
            pushInt(stack, (Integer)val);
        } else if (type == Float.TYPE || type == Float.class) {
        	pushFloat(stack, (Float)val);
        } else if (type == Long.TYPE || type == Long.class) {
        	pushLong(stack, (Long)val);
        } else if (type == Double.TYPE || type == Double.class) {
            pushDouble(stack, (Double)val);
        } else {
        	throw new IOException("Unsupported basic type: " + type.getName());
        }
    }

    protected void pushStringContent(ByteFragment stack, String val) throws IOException {
    	pushByteContent(stack, val.getBytes(utf8));
    }
    
    protected void pushByteContent(ByteFragment stack, byte[] array) throws IOException {
    	int pos = stack.push(array.length);
    	System.arraycopy(array, 0, stack.bytes.buf, pos, array.length);
    }

    protected void pushArrayItems(ByteFragment stack, Object val) throws IOException {
    	Class<?> type = val.getClass();
        Class<?> ccl = type.getComponentType();
        if (ccl == null) {
        	ccl = Object.class;
        }
        if (ccl.isPrimitive()) {
            if (ccl == Integer.TYPE) {
            	int[] array = (int[])val;
                for (int item : array)
                	pushInt(stack, item);
            } else if (ccl == Byte.TYPE) {
            	pushByteContent(stack, (byte[])val);
            } else if (ccl == Long.TYPE) {
            	long[] array = (long[])val;
                for (long item : array)
                	pushLong(stack, item);
            } else if (ccl == Float.TYPE) {
            	float[] array = (float[])val;
                for (float item : array)
                	pushFloat(stack, item);
            } else if (ccl == Double.TYPE) {
            	double[] array = (double[])val;
                for (double item : array)
                	pushDouble(stack, item);
            } else if (ccl == Short.TYPE) {
            	short[] array = (short[])val;
                for (short item : array)
                	pushShort(stack, item);
            } else if (ccl == Character.TYPE) {
            	char[] array = (char[])val;
                for (char item : array)
                	pushChar(stack, item);
            } else if (ccl == Boolean.TYPE) {
            	boolean[] array = (boolean[])val;
                for (boolean item : array)
                	pushBoolean(stack, item);
            } else {
                throw new InternalError();
            }
        } else {
        	if (isBasic(ccl)) {
                Object[] array = (Object[])val;
                for (Object item : array)
                	pushBasic(stack, item);
        	} else {
                Object[] array = (Object[])val;
                for (Object item : array)
                	pushObject(stack, item);
        	}
        }
    }
    
    protected void pushObject(ByteFragment stack, Object val) throws IOException {
    	if (val == null) {
    		pushInt(stack, -1);
    		return;
    	}
    	ByteFragment origCopy = stack.copy();
    	stack.push(4);
    	int posBefore = stack.from + stack.length;
    	Class<?> type = val.getClass();
    	if (isBasic(type)) {
    		pushBasic(stack, val);
    	} else if (type.isArray()) {
    		pushArrayItems(stack, val);
    	} else if (type == String.class) {
    		pushStringContent(stack, (String)val);
    	} else { // Object properties
			for (Method m : type.getMethods()) {
				if (m.getName().startsWith("get") && m.getParameterTypes().length == 0 &&
						(m.getModifiers() & Modifier.STATIC) == 0 &&
						(m.getModifiers() & Modifier.PUBLIC) != 0) {
					if (m.getDeclaringClass() == Object.class) {
						continue;
					}
					String propName = m.getName().substring(3);
					if (propName.length() == 0) {
						continue;
					}
					propName = propName.substring(0, 1).toLowerCase() + propName.substring(1);
					Object propValue;
					try {
						propValue = m.invoke(val);
					} catch (IllegalArgumentException | InvocationTargetException |
							IllegalAccessException e) {
						throw new IOException("Error invocation " + m + " method " +
								"for type " + type.getName());
					}
					if (propValue != null) {
						pushObject(stack, propName);
						pushObject(stack, propValue);
					}
				}
			}
    	}
    	int posAfter = stack.from + stack.length;
    	pushInt(origCopy, posAfter - posBefore);
    }
    
	public ByteFragment objectToBytes(Object val) throws IOException {
		ResizableByteArray bytes = new ResizableByteArray();
		ByteFragment ret = new ByteFragment(bytes, 0, 0);
		pushObject(ret, val);
		return ret;
	}
}
