if test "$1" == "-c"
then
javac -d bin src/project/*
exit
fi

java -classpath "bin" project.Project $@
