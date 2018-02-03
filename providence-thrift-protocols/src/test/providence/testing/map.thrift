namespace java net.morimekta.test.providence.thrift.map

exception NotFound {}

service RemoteMap {
    bool put(1: string key,
             2: string value)

    map<string,string> putAll(1: map<string,string> source)

    string get(1: string key) throws (1: NotFound nfe)
    map<string, string> getAll(1: set<string> keys)
}
