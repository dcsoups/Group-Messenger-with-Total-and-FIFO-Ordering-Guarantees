# Group-Messenger-with-Total-and-FIFO-Ordering-Guarantees
This group messenger provides Total-FIFO ordering guarantees. That means, any android device using this app will see all the messages in the same order as well as in the order that they are sent. This will hold even is large number of messages are sent concurrently.

Providing these guarantees requires achieving distributed consensus. The app provides these consensus by implementing the ISIS algorithm.
