Providence Utils : Storage
==========================

Simple storage interface that can be extended to make use of any type of
backing object store. Comes with 2 default implementations, one pure
`InMemoryMessageStore` which uses a local `HashMap` to store the messages,
and the `DirectoryMessageStore` which stores the messages in individual files
in a given directory, and uses a local cache to speed up message fetching.