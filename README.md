# chicken

A tiny toy command line twitter.

It is both a solution for test task in one company and a Clojure demo.

Provided solution might be overcomplicated for small demo,
 because I strived to provide production ready code which I would accept as
a code reviewer in a real project, so in addition to
cli functionality itself 

1) it provides code loading facility for being able to implement new command 
by just providing it in one file and no need to modify existing codebase
to plug it in.

2) It is designed to be extended 
(it is easy to switch repository to filesystem/db, 
to attach tcp interface to it to be able to work over telnet,
whatsoever...)

3) It is covered with tests.

After implementing chicken in Clojure I realized that it might be mind blowing 
for people who have no previous experience with this language. 
So tomorrow I'll supply implemenation in Scala which will be much more traditional 
to Object-Oriented approach but will do all the same logic for commands parsing and so on.

To run it you need to have jvm installed.
Clone the project with git clone,
Navigate to bin folder, 
and execute ./chicken.sh 
(previously you might need to make it executable: chmod a+x chicken.sh)

Alternatively to run chicken you can type 
java -jar chicken.jar
when you are in bin/ dir.
