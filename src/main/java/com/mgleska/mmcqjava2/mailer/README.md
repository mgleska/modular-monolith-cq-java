## Demo module _Mailer_

The purpose of this module is to **show how to use RabbitMQ** to asynchronously handle requests passed synchronously by other modules.

Following the Open Host approach, the module provides a SendMail action (with its ParamDto), which accepts a request to send an email.

The request is registered in the database. Then a message with the reference ID assigned to the registered request is placed in the RabbitMQ queue.

The asynchronous RabbitMQ message receiver selects ready messages from the queue, fetches mail message details form database, and attempts to send an email via an external service provider.

Fake service provider reports error for 25% of delivery attempts.

After an unsuccessful email delivery attempt, the message is placed back in the queue. A maximum of three attempts are made to send the email.

The asynchronous RabbitMQ message receiver can run from 2 to 4 its parallel instances, depending on number of messages waiting in the queue.