A short explanation to how I decided to solve the problem would be:
    1) Creating an output folder.
    2) Getting the document to parse and the order no(order01.xml => 01)
    3) I get every brand from the file with their respective elements.
    4) Every product of each and every supplier is being put to a new xml file
           with the name of the supplier and the order no.
           
I decided to use Maven due to the fact that it made the process of adding google
core libraries, namely Java, to the project. The reason why I used Guava is the 
fact that it provided me with a nice and easy way of storing the information I
needed.