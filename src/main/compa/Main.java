package compa;

import compa.app.ClassFinder;
import compa.app.Container;

public class Main {

	public static void main(String... args) {
		new Container(null, Container.MODE.PROD).run(new ClassFinder());
	}

}