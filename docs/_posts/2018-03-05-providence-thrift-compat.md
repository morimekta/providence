---
layout: page
toc_title: Thrift Compat
title: "Providence Utils: Thrift Compatibility"
category: util
date: 2018-03-05 12:00:00
order: 5
---

This module adds server and client implementations that can task to standard
thrift service implementations. There are both client and server classes
for each type of server.

- `SocketServer` is a simple TCP socket server talking with one client at
  a time. Each connection will bind up a thread and will only handle one
  service call at a time. See `SocketClientHandler` and `SocketServer`
  classes. The providence client is thread safe, and will queue up messages
  to be handled internally.

- `NonblockingSocketServer` is a more advanced TCP socket server that uses
  framed buffer messages between the client and server, and can handle calls
  and responses in parallel and out of order. It uses a more complex socket
  channel system to handle messages internally. The client is thread safe
  and can be handled by multiple threads at a time over the same channel.
  The server has a shared thread pool used to provision workers to handle
  each call. See `NonblockingSocketClientHandler` and `NonblockingSocketServer`
  classes.