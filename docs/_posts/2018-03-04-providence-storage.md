---
layout: page
toc_title: Storage
title: "Providence Utils: Storage"
category: util
date: 2018-03-04 12:00:00
order: 4
---

Simple storage interface that can be extended to make use of any type of
backing object store. Comes with 2 default implementations, one pure
`InMemoryMessageStore` which uses a local `HashMap` to store the messages,
and the `DirectoryMessageStore` which stores the messages in individual
files in a given directory, and uses an in-process cache to speed up
message reading.