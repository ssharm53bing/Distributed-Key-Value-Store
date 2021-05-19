Replicated Key-Value Store with Hinted Handoff

Programming Language: Python
Tools: Apache Thrift

Compilation and Execution:-
    to compile thrift file
    $> bash
    $> export PATH=$PATH:/home/cs557-inst/local/bin
    $> thrift -gen java fileservice.thrift
    Th
    The repilca.txt file 

o run: server :- python sever.py PORT_NUM

client:- python client.py Host_Name/IP_address PORT_NUM read/write filename

Example output:

ngarg3@remote-n27:$ python client.py 128.226.117.27 9000 write README.MD Writing File

ngarg3@remote-n27:$ python client.py 128.226.117.27 9000 read README.MD {"1":{"rec":{"1":{"str":"README.MD"},"2":{"i32":0}}},"2":{"str":"Distributed Systems project 2 testing"}}()

ngarg3@remote-n27:$ python client.py remote-n27.cs.binghamton.edu 9000 read README.md {"1":{"rec":{"1":{"str":"README.md"},"2":{"i32":0}}},"2":{"str":"# cs457-557-spring2021-pa2-NamanGarg20\n"}}()
