package cas;

import java.lang.Integer;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.script.ScriptException;

public class Applet {
	
	public static void main(String[] args) throws ScriptException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Scanner scanner = new Scanner(System.in, "UTF-8");
		for (byte i=0; i<=Byte.MAX_VALUE; i++) { // Ha ha. Tricked you, silly compiler.
			System.out.print("Enter expression: ");
			Expr result = (Expr)(parseExpr(scanner.nextLine()).getObject());
			System.out.println("This object is: " + result);
			result = result.simplify();
			System.out.println("This object simplifies to: " + InformationPasser.getContainingGroup(result));
			break;
		}
		scanner.close();
	}
	
	public interface NodeData {
		public default Object getObject() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			return this;
		}
	}
	
	private static NodeData parseExpr(String str) {
		for (Transcendent value : Transcendent.values()) {
			if (str.equals(value.name())) {
				return new Transcendental(value);
			}
		}
		str = str.replace(" ", "");
//		System.out.println("Parsing: " + str);
		boolean isOnlyNumber = true;
		for (int i=0; i<str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				isOnlyNumber = false;
				break;
			}
		}
		if (isOnlyNumber) {
			return new cas.Integer(Long.parseLong(str));
		}
		String functionName = str.substring(0, str.indexOf('['));
		str = str.substring(str.indexOf('[')+1, str.length()-1);
		ArrayList<Integer> commas = new ArrayList<Integer>();
		int bracketLevel = 0;
		for (int i=0; i<str.length(); i++) {
			if (str.charAt(i) == '[') bracketLevel++;
			else if (str.charAt(i) == ']') bracketLevel--;
			else if (str.charAt(i) == ',' && bracketLevel == 0) commas.add(i);
			if (bracketLevel < 0) throw new IllegalArgumentException("Invalid parenthesis nesting");
		}
		ArrayList<String> argStrings = new ArrayList<>();
		int lastComma = -1;
		for (int comma : commas) {
			argStrings.add(str.substring(lastComma+1, comma));
			lastComma = comma;
		}
		argStrings.add(str.substring(lastComma+1));
		ArrayList<NodeData> arguments = new ArrayList<>();
		for (String arg : argStrings) {
			arguments.add(parseExpr(arg));
		}
		return new Node(functionName, arguments);
	}
	
	private static class Node implements NodeData {
		public String name;
		public ArrayList<NodeData> data;
		
		public Node(String name, ArrayList<NodeData> data) {
			this.name = name;
			this.data = data;
		}
		
		public Object getObject() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			// Reduce child nodes to objects if necessary
			ArrayList<Object> finalData = new ArrayList<>(data);
			for (int i=0; i<finalData.size(); i++) {
				finalData.set(i, ((NodeData)finalData.get(i)).getObject());
			}
			
			// Get function class to instantiate
			Class<?> $class = Class.forName("cas." + name);
			
			// Get argument classes
			ArrayList<Class<?>> $argClasses = new ArrayList<>();
			for (Object arg : finalData) {
				$argClasses.add(arg.getClass());
			}
//			System.out.println("Attempting to instantiate " + $class + " with " + $argClasses);
			
			// Get constructor to instantiate
			Constructor<?>[] $constructors = $class.getDeclaredConstructors();
			Constructor<?> $constructor = null;
			for (Constructor<?> $possibleConstructor : $constructors) {
//				System.out.println("Possible constructor: " + $possibleConstructor);
				Class<?>[] $possibleParameterTypes = $possibleConstructor.getParameterTypes();
				boolean isValidConstructor = true;
				if ($argClasses.size() == $possibleParameterTypes.length) {
					for (int i=0; i<$argClasses.size(); i++) {
						if (!$possibleParameterTypes[i].isAssignableFrom($argClasses.get(i))) {
							isValidConstructor = false;
							break;
						}
					}
				}
				else {
					if ($possibleParameterTypes.length == 1 &&
							$possibleParameterTypes[0].isArray()) {
						// We might still be able to stick our arguments in an array:
						Class<?> $arrayType = $possibleParameterTypes[0].getComponentType();
//						System.out.println("Detected array type: " + $arrayType);
						for (Class<?> $argClass : $argClasses) {
							if (!$arrayType.isAssignableFrom($argClass)) {
								isValidConstructor = false;
								break;
							}
						}
						if (isValidConstructor) {
							ArrayList<Object> temp = finalData;
							finalData = new ArrayList<Object>();
							Object dataArray = Array.newInstance($arrayType, temp.size());
							for (int i=0; i<temp.size(); i++) {
								Array.set(dataArray, i, temp.get(i));
							}
							finalData.add(dataArray);
						}
					}
					else {
						isValidConstructor = false;
					}
				}
				if (isValidConstructor) {
					$constructor = $possibleConstructor;
//					System.out.print("Selected valid constructor; ");
					break;
				}
			}
			if ($constructor == null) {
				throw new NoSuchMethodException("Couldn't find a constructor for this class!");
			}
			
			// Instantiate object
//			System.out.println("instantiating " + $constructor + " with " + finalData);
			Object $object = $constructor.newInstance(finalData.toArray());
			
			return $object;
		}
	}
	
}
