#!bin/bash
pkill -f ZvServer
cd target
java -cp "viz/WEB-INF/lib/*:viz-jar-with-dependencies.jar:classes/data" edu.uiuc.viz.server.VizServer

