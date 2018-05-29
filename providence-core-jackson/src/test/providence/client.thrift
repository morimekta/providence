namespace java net.morimekta.test.providence.jackson.client

struct Request {
    1: optional binary the_binary;
    2: optional string not_binary;
    3: optional list<binary> list_of;
    4: optional map<binary,binary> map_of;
} (java.public.constructor)
