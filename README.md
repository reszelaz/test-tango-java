This repository demonstrate problem with Tango events when network alias is used for TANGO_HOST.
Basically the events does not arrive and we get periodically the heartbeat problem.

In order to reproduce (I use Debian 9):

Prepare the server:

1. Run the server using network aliased TANGO_HOST e.g. `TangoTest`
2. Enable polling on its device State attribute e.g. `sys/tg_test/1`

Prepare the client:
1. Export the necessary variables e.g.
```console
source devenv
```
2. Compile the SimpleClient.java code e.g.
```
javac SimpleClient.java
```
3. Run the client using network aliased TANGO_HOST e.g.
```console
CLASSPATH=.:$CLASSPATH java SimpleClient sys/tg_test/1
```

After 10 s you will receive this kind of errors:

```
Thu Nov 07 17:47:37 CET 2019
sys/tg_test/1  has received a DevFailed :       No heartbeat from dserver/tangotest/test
HostStateThread.StateEventListener on sys/tg_test/1
Received Event
```

The problem disappears if I use the real host name for the TANGO_HOST.
The same thing can be reproduced using Astor.

