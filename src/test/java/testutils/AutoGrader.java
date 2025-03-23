package testutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

public class AutoGrader {

	// Test if the code implements hierarchical inheritance correctly
	public boolean testHierarchicalInheritance(String filePath) throws IOException {
		System.out.println("Starting testHierarchicalInheritance with file: " + filePath);

		File participantFile = new File(filePath); // Path to participant's file
		if (!participantFile.exists()) {
			System.out.println("File does not exist at path: " + filePath);
			return false;
		}

		FileInputStream fileInputStream = new FileInputStream(participantFile);
		JavaParser javaParser = new JavaParser();
		CompilationUnit cu;
		try {
			cu = javaParser.parse(fileInputStream).getResult()
					.orElseThrow(() -> new IOException("Failed to parse the Java file"));
		} catch (IOException e) {
			System.out.println("Error parsing the file: " + e.getMessage());
			throw e;
		}

		System.out.println("Parsed the Java file successfully.");

		// Use AtomicBoolean to allow modifications inside lambda expressions
		AtomicBoolean animalClassFound = new AtomicBoolean(false);
		AtomicBoolean dogClassFound = new AtomicBoolean(false);
		AtomicBoolean catClassFound = new AtomicBoolean(false);
		AtomicBoolean dogExtendsAnimal = new AtomicBoolean(false);
		AtomicBoolean catExtendsAnimal = new AtomicBoolean(false);
		AtomicBoolean speakMethodImplementedInDog = new AtomicBoolean(false);
		AtomicBoolean speakMethodImplementedInCat = new AtomicBoolean(false);
		AtomicBoolean methodsExecuted = new AtomicBoolean(false);

		// Check for class implementation and inheritance (Dog and Cat extend Animal)
		System.out.println("------ Inheritance and Class Implementation Check ------");
		for (TypeDeclaration<?> typeDecl : cu.findAll(TypeDeclaration.class)) {
			if (typeDecl instanceof ClassOrInterfaceDeclaration) {
				ClassOrInterfaceDeclaration classDecl = (ClassOrInterfaceDeclaration) typeDecl;

				if (classDecl.getNameAsString().equals("Animal")) {
					System.out.println("Class 'Animal' found.");
					animalClassFound.set(true);
				}

				if (classDecl.getNameAsString().equals("Dog")) {
					System.out.println("Class 'Dog' found.");
					dogClassFound.set(true);
					// Check if Dog extends Animal
					if (!classDecl.getExtendedTypes().isEmpty()
							&& classDecl.getExtendedTypes(0).getNameAsString().equals("Animal")) {
						dogExtendsAnimal.set(true);
						System.out.println("Dog extends 'Animal'.");
					} else {
						System.out.println("Error: 'Dog' does not extend 'Animal'.");
					}
				}

				if (classDecl.getNameAsString().equals("Cat")) {
					System.out.println("Class 'Cat' found.");
					catClassFound.set(true);
					// Check if Cat extends Animal
					if (!classDecl.getExtendedTypes().isEmpty()
							&& classDecl.getExtendedTypes(0).getNameAsString().equals("Animal")) {
						catExtendsAnimal.set(true);
						System.out.println("Cat extends 'Animal'.");
					} else {
						System.out.println("Error: 'Cat' does not extend 'Animal'.");
					}
				}
			}
		}

		// Ensure all classes are found and inheritance is correct
		if (!animalClassFound.get() || !dogClassFound.get() || !catClassFound.get()) {
			System.out.println("Error: One or more classes (Animal, Dog, Cat) are missing.");
			return false; // Early exit if class creation is missing
		}

		if (!dogExtendsAnimal.get()) {
			System.out.println("Error: 'Dog' must extend 'Animal'.");
			return false;
		}

		if (!catExtendsAnimal.get()) {
			System.out.println("Error: 'Cat' must extend 'Animal'.");
			return false;
		}

		// Check for method overriding (speak method in Dog and Cat)
		System.out.println("------ Method Override Check ------");
		for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
			if (method.getNameAsString().equals("speak") && method.getParentNode().get().toString().contains("Dog")) {
				speakMethodImplementedInDog.set(true);
				System.out.println("Method 'speak' overridden in 'Dog' class.");
			}
			if (method.getNameAsString().equals("speak") && method.getParentNode().get().toString().contains("Cat")) {
				speakMethodImplementedInCat.set(true);
				System.out.println("Method 'speak' overridden in 'Cat' class.");
			}
		}

		if (!speakMethodImplementedInDog.get() || !speakMethodImplementedInCat.get()) {
			System.out.println("Error: One or more methods ('speak' in Dog or Cat) not overridden.");
			return false;
		}

		// Check if both methods are executed in main
		System.out.println("------ Method Execution Check in Main ------");
		for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
			if (method.getNameAsString().equals("main")) {
				if (method.getBody().isPresent()) {
					method.getBody().get().findAll(MethodCallExpr.class).forEach(callExpr -> {
						if (callExpr.getNameAsString().equals("speak")) {
							methodsExecuted.set(true);
							System.out.println("Method 'speak' is executed in the main method.");
						}
					});
				}
			}
		}

		if (!methodsExecuted.get()) {
			System.out.println("Error: Methods 'speak' not executed in the main method.");
			return false;
		}

		// If inheritance, method overriding, and method execution are correct
		System.out.println("Test passed: Hierarchical inheritance is correctly implemented.");
		return true;
	}
}
