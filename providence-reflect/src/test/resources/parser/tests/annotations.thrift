namespace java net.morimekta.test.annotations

enum E {
    VAL (anno = "str", anno.other = "other")
} (e.anno = "E")

exception S {
    1: bool val (anno = "str")
} (other)

service Srv {
    void method(1: i32 param (abba = "7")) (anno = "anno")
    void method2(1: i32 param (abba = "7"))
      throws (1: S e (ex = "667"))
      (anno = "anno");
} (src = "src", bin = "bin")
