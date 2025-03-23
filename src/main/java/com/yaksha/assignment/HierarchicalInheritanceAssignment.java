package com.yaksha.assignment;

//Animal class - Base class in the hierarchy
class Animal {
	String species;

	public Animal() {
		species = "Unknown species"; // Default species
	}

	public void speak() {
		System.out.println("The animal makes a sound.");
	}
}

//Dog class - Inherits from Animal
class Dog extends Animal {

	@Override
	public void speak() {
		System.out.println("The dog barks.");
	}
}

//Cat class - Inherits from Animal
class Cat extends Animal {

	@Override
	public void speak() {
		System.out.println("The cat meows.");
	}
}

public class HierarchicalInheritanceAssignment {
	public static void main(String[] args) {
		// Creating Dog and Cat objects
		Dog dog = new Dog();
		Cat cat = new Cat();

		// Calling the speak method from Dog and Cat
		dog.speak(); // Should print "The dog barks."
		cat.speak(); // Should print "The cat meows."
	}
}
