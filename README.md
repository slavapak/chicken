# chicken

A tiny toy command line twitter.

It is both a solution for test task in one company and a Clojure demo.

Provided solution might be overcomplicated for small demo,
 because I strived to provide production ready code, so in addition to
cli functionality itself 

1) it provides code loading facility for being able to implement new command 
by just providing it in one file and no need to modify existing codebase
to plug it in.

2) It is designed to be extended 
(it is easy to switch repository to filesystem/db, 
to attach tcp interface to it to be able to work over telnet,
whatsoever...)

3) It is covered with tests.

I think I would approve such code as a reviewer if this were the real project.
But after implementing chicken I realized that it might be mind blowing for
people who have no previous experience with Clojure. So tomorrow I'll supply
implemenation in Scala which will be much more traditional to Object-Oriented
approach but will do all the same logic for commands parsing and so on.

To run it you need to have jvm installed.
Navigate to bin folder and execute ./chicken.sh 
(previously it might be needed to make it executable: chmod a+x chicken.sh)

Alternatively to run chicken you can type 
java -jar chicken.jar
when you are in bin/ dir.
