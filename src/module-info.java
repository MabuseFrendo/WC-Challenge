module javaFX_Derby_MW1 {
	requires transitive javafx.graphics;
	requires transitive javafx.controls;
	requires java.sql;
	requires javafx.base;
	requires org.apache.logging.log4j;
	exports gui;
}